package kr.modusplant.domains.post.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.infrastructure.file.service.PendingFileService;
import kr.modusplant.domains.post.domain.exception.ContentProcessingException;
import kr.modusplant.domains.post.domain.exception.PostAccessDeniedException;
import kr.modusplant.domains.post.domain.exception.PostNotFoundException;
import kr.modusplant.domains.post.domain.vo.*;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.port.processor.ContentDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.repository.*;
import kr.modusplant.domains.post.usecase.record.ContentProcessRecord;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.request.FileOrder;
import kr.modusplant.domains.post.usecase.request.PostCategoryRequest;
import kr.modusplant.domains.post.usecase.request.PostFileUploadRequest;
import kr.modusplant.domains.post.usecase.request.PostRequest;
import kr.modusplant.domains.post.usecase.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostController {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final PostQueryForMemberRepository postQueryForMemberRepository;
    private final ContentDataProcessorPort contentDataProcessorPort;
    private final PostViewCountRepository postViewCountRepository;
    private final PostViewLockRepository postViewLockRepository;
    private final PostArchiveRepository postArchiveRepository;
    private final PostRecentlyViewRepository postRecentlyViewRepository;
    private final PendingFileService pendingFileService;

    @Value("${redis.ttl.view_count}")
    private long ttlMinutes;

    public CursorLatestSortedPageResponse<PostSummaryResponse> getAll(PostCategoryRequest postCategoryRequest, UUID currentMemberUuid, String lastUlid, int size) {
        List<PostSummaryReadModel> readModels = postQueryRepository.findByCategoryWithCursor(
                postCategoryRequest.primaryCategoryId(), postCategoryRequest.secondaryCategoryIds(), currentMemberUuid, lastUlid, size
        );
        boolean hasNext = readModels.size() > size;
        List<PostSummaryResponse> responses = readModels.stream()
                .limit(size)
                .map(readModel -> postMapper.toPostSummaryResponse(readModel,getJsonNodeContentPreview(readModel.content(),readModel.thumbnailPath()))).toList();
        String nextUlid = hasNext && !responses.isEmpty() ? responses.getLast().ulid() : null;
        return CursorLatestSortedPageResponse.of(responses, nextUlid, hasNext);
    }

    public PostDetailResponse getByUlid(String ulid, UUID currentMemberUuid, UUID guestId) {
        PostId postId = PostId.create(ulid);
        return postQueryRepository.findPostDetailByPostId(postId,currentMemberUuid)
                .filter(PostDetailReadModel::isPublished)
                .map(postDetail -> {
                    increaseViewCount(ulid,currentMemberUuid,guestId);
                    postRecentlyViewRepository.recordViewPost(currentMemberUuid,postId);
                    return postMapper.toPostDetailResponse(
                            postDetail,
                            (postDetail.imagePath() != null && !postDetail.imagePath().isBlank()) ? contentDataProcessorPort.getS3SrcUrl(postDetail.imagePath()) : null,
                            getJsonNodeContent(postDetail.content()),
                            readViewCount(ulid)
                    );
                }).orElseThrow(PostNotFoundException::new);
    }

    public PostDetailDataResponse getDataByUlid(String ulid, UUID currentMemberUuid) {
        return postQueryRepository.findPostDetailDataByPostId(PostId.create(ulid))
                .filter(postDetailData -> postDetailData.isPublished() || postDetailData.authorUuid().equals(currentMemberUuid))
                .map(postDetailData -> postMapper.toPostDetailDataResponse(
                        postDetailData,
                        getJsonNodeContentWithFileKey(postDetailData.content()),
                        contentDataProcessorPort.extractOriginalFilenameFromFileKey(postDetailData.thumbnailPath())))
                .orElseThrow(PostNotFoundException::new);
    }

    public List<PostFileUploadUrlResponse> getUploadUrls(List<PostFileUploadRequest> files) {
        List<PostFileUploadUrlResponse> result = contentDataProcessorPort.getMultipleUploadUrls(files);
        List<String> issuedFileKeys = result.stream().map(PostFileUploadUrlResponse::fileKey).toList();
        pendingFileService.trackPendingFiles(issuedFileKeys);
        return result;
    }

    @Transactional
    public void createPost(PostRequest postRequest, boolean isPublished, UUID currentMemberUuid) throws IOException {
        AuthorId authorId = AuthorId.fromUuid(currentMemberUuid);
        Post post;
        if (isPublished) {  // 발행 게시글 생성
            PrimaryCategoryId primaryCategoryId = PrimaryCategoryId.create(postRequest.primaryCategoryId());
            SecondaryCategoryId secondaryCategoryId = SecondaryCategoryId.create(postRequest.secondaryCategoryId());
            ContentProcessRecord result = contentDataProcessorPort.generateContentJson(postRequest.contentText(), postRequest.contentFiles(), postRequest.thumbnailFilename());
            post = Post.createPublished(authorId, primaryCategoryId, secondaryCategoryId,  PostContent.create(postRequest.title(), result.content(), result.thumbnailPath()));
        } else {            // 임시저장 게시글 생성
            PrimaryCategoryId primaryCategoryId = postRequest.primaryCategoryId() != null ? PrimaryCategoryId.create(postRequest.primaryCategoryId()) : null;
            SecondaryCategoryId secondaryCategoryId = postRequest.secondaryCategoryId() != null ? SecondaryCategoryId.create(postRequest.secondaryCategoryId()) : null;
            ContentProcessRecord result = (postRequest.contentText() == null && postRequest.contentFiles() == null)
                    ? new ContentProcessRecord(null,null)
                    : contentDataProcessorPort.generateContentJson(postRequest.contentText(), postRequest.contentFiles(), postRequest.thumbnailFilename());
            post = Post.createDraft(authorId, primaryCategoryId, secondaryCategoryId, PostContent.createDraft(postRequest.title(), result.content(), result.thumbnailPath()));
        }
        postRepository.save(post);
        untrackContentFiles(postRequest.contentFiles());
    }

    @Transactional
    public void updatePost(String ulid, PostRequest postRequest, boolean isPublished, UUID currentMemberUuid) throws IOException {
        Post post = postRepository.getPostByUlid(PostId.create(ulid))
                .filter(p -> p.getAuthorId().equals(AuthorId.fromUuid(currentMemberUuid))).orElseThrow();
        // 기존 filekey 목록 미리 추출
        List<String> oldFileKeys = contentDataProcessorPort.extractFileKeysFromContent(post.getPostContent().getContent());

        // 게시글 검증 수행
        if (isPublished) {
            ContentProcessRecord result = contentDataProcessorPort.generateContentJson(postRequest.contentText(), postRequest.contentFiles(), postRequest.thumbnailFilename());
            post.update(
                    AuthorId.fromUuid(currentMemberUuid),
                    PrimaryCategoryId.create(postRequest.primaryCategoryId()),
                    SecondaryCategoryId.create(postRequest.secondaryCategoryId()),
                    PostContent.create(postRequest.title(), result.content(), result.thumbnailPath()),
                    PostStatus.published()
            );
        } else {
            ContentProcessRecord result = (postRequest.contentText() == null && postRequest.contentFiles() == null)
                    ? new ContentProcessRecord(null,null)
                    : contentDataProcessorPort.generateContentJson(postRequest.contentText(), postRequest.contentFiles(), postRequest.thumbnailFilename());
            post.updateDraft(
                    AuthorId.fromUuid(currentMemberUuid),
                    postRequest.primaryCategoryId() != null ? PrimaryCategoryId.create(postRequest.primaryCategoryId()) : null,
                    postRequest.secondaryCategoryId() != null ? SecondaryCategoryId.create(postRequest.secondaryCategoryId()) : null,
                    PostContent.createDraft(postRequest.title(), result.content(), result.thumbnailPath())
            );
        }
        postRepository.update(post);
        untrackContentFiles(postRequest.contentFiles());
        contentDataProcessorPort.deleteFilesWithFileKeys(oldFileKeys);
    }

    @Transactional
    public void deletePost(String ulid, UUID currentMemberUuid) {
        Post post = postRepository.getPostByUlid(PostId.create(ulid)).orElseThrow(PostNotFoundException::new);
        if (!post.getAuthorId().equals(AuthorId.fromUuid(currentMemberUuid))) {
            throw new PostAccessDeniedException();
        }
        if (post.getStatus().isPublished()) {
            postArchiveRepository.save(PostId.create(ulid));
            postRepository.deletePostLikeByPostId(post.getPostId());
            postRepository.deletePostBookmarkByPostId(post.getPostId());
            postRepository.deletePostRecentlyViewRecordByPostId(post.getPostId());
        }
        contentDataProcessorPort.deleteFiles(post.getPostContent().getContent());
        postRepository.delete(post);
    }

    public Long readViewCount(String ulid) {
        Long redisViewCount = postViewCountRepository.read(PostId.create(ulid));
        if (redisViewCount != null) {
            return redisViewCount;
        }
        Long dbViewCount = postRepository.getViewCountByUlid(PostId.create(ulid));
        postViewCountRepository.write(PostId.create(ulid), dbViewCount);
        return dbViewCount;
    }

    @Transactional
    public Long increaseViewCount(String ulid, UUID currentMemberUuid, UUID guestId) {
        // 조회수 어뷰징 정책 - 사용자는 게시글 1개당 ttl에 1번 조회수 증가
        boolean isLocked = true;
        if (currentMemberUuid != null) {
            isLocked = !postViewLockRepository.lock(PostId.create(ulid), currentMemberUuid, ttlMinutes);
        } else if (guestId != null) {
            isLocked = !postViewLockRepository.lockAnonymous(PostId.create(ulid),guestId,ttlMinutes);
        }
        if (isLocked) {
            return postViewCountRepository.read(PostId.create(ulid));
        }
        // 조회수 증가
        return postViewCountRepository.increase(PostId.create(ulid));
    }

    public OffsetPageResponse<PostSummaryResponse> getByMemberUuid(UUID memberUuid, int page, int size) {
        return OffsetPageResponse.from(
                postQueryForMemberRepository.findPublishedByAuthMemberWithOffset(AuthorId.fromUuid(memberUuid),page,size)
                        .map(postModel -> postMapper.toPostSummaryResponse(postModel,getJsonNodeContentPreview(postModel.content(),postModel.thumbnailPath()))));
    }

    public OffsetPageResponse<DraftPostResponse> getDraftByMemberUuid(UUID currentMemberUuid, int page, int size) {
        return OffsetPageResponse.from(
                postQueryForMemberRepository.findDraftByAuthMemberWithOffset(AuthorId.fromUuid(currentMemberUuid),page,size)
                        .map(postModel -> postMapper.toDraftPostResponse(postModel, getJsonNodeContentPreview(postModel.content(),postModel.thumbnailPath()))));
    }

    public OffsetPageResponse<PostSummaryResponse> getRecentlyViewByMemberUuid(UUID currentMemberUuid, int page, int size) {
        List<PostId> postIds = postRecentlyViewRepository.getRecentlyViewPostIds(currentMemberUuid,page,size);
        long totalElements = postRecentlyViewRepository.getTotalRecentlyViewPosts(currentMemberUuid);
        if (postIds.isEmpty()) {
            return OffsetPageResponse.from(new PageImpl<>(List.of(),PageRequest.of(page,size),totalElements));
        }
        List<PostSummaryResponse> postsPages = postQueryForMemberRepository.findByIds(postIds,currentMemberUuid)
                .stream()
                .map(postModel -> postMapper.toPostSummaryResponse(postModel, getJsonNodeContentPreview(postModel.content(),postModel.thumbnailPath())))
                .toList();
        return OffsetPageResponse.from(new PageImpl<>(postsPages,PageRequest.of(page,size),totalElements));
    }

    public OffsetPageResponse<PostSummaryResponse> getLikedByMemberUuid(UUID currentMemberUuid, int page, int size) {
        return OffsetPageResponse.from(
                postQueryForMemberRepository.findLikedByMemberWithOffset(currentMemberUuid,page,size)
                        .map(postModel -> postMapper.toPostSummaryResponse(postModel, getJsonNodeContentPreview(postModel.content(),postModel.thumbnailPath()))));
    }

    public OffsetPageResponse<PostSummaryResponse> getBookmarkedByMemberUuid(UUID currentMemberUuid, int page, int size) {
        return OffsetPageResponse.from(
                postQueryForMemberRepository.findBookmarkedByMemberWithOffset(currentMemberUuid,page,size)
                        .map(postModel -> postMapper.toPostSummaryResponse(postModel, getJsonNodeContentPreview(postModel.content(),postModel.thumbnailPath())))
        );
    }

    private JsonNode getJsonNodeContentPreview(JsonNode content, String thumbnailPath) {
        if (content == null) return null;
        JsonNode contentPreview;
        try {
            contentPreview = contentDataProcessorPort.convertToPreview(content, thumbnailPath);
        } catch (IOException e) {
            throw new ContentProcessingException();
        }
        return contentPreview;
    }

    private JsonNode getJsonNodeContent(JsonNode content) {
        if (content == null) return null;
        JsonNode newContent;
        try {
            newContent = contentDataProcessorPort.convertFileSrcToFullFileSrc(content);
        } catch (IOException e) {
            throw new ContentProcessingException();
        }
        return newContent;
    }

    private JsonNode getJsonNodeContentWithFileKey(JsonNode content) {
        if (content == null) return null;
        JsonNode newContent;
        try {
            newContent = contentDataProcessorPort.convertFileSrcToFullFileSrcWithFileKey(content);
        } catch (IOException e) {
            throw new ContentProcessingException();
        }
        return newContent;
    }

    private void untrackContentFiles(List<FileOrder> contentFiles) {
        List<String> fileKeys = contentFiles != null
                ? contentFiles.stream().map(FileOrder::fileKey).toList()
                : List.of();
        pendingFileService.untrackPendingFiles(fileKeys);
    }
}
