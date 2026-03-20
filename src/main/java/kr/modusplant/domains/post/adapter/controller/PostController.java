package kr.modusplant.domains.post.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.exception.ContentProcessingException;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.PostAccessDeniedException;
import kr.modusplant.domains.post.domain.exception.PostNotFoundException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.domains.post.domain.vo.*;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.repository.*;
import kr.modusplant.domains.post.usecase.record.ContentProcessRecord;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.request.PostCategoryRequest;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.usecase.request.PostSearchRequest;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
import kr.modusplant.domains.post.usecase.response.*;
import kr.modusplant.framework.aws.service.S3FileService;
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
    private final MultipartDataProcessorPort multipartDataProcessorPort;
    private final PostViewCountRepository postViewCountRepository;
    private final PostViewLockRepository postViewLockRepository;
    private final PostArchiveRepository postArchiveRepository;
    private final PostRecentlyViewRepository postRecentlyViewRepository;
    private final PostSearchHistoryRepository postSearchHistoryRepository;
    private final S3FileService s3FileService;

    @Value("${redis.ttl.view_count}")
    private long ttlMinutes;

    public CursorPageResponse<PostSummaryResponse> getAll(PostCategoryRequest postCategoryRequest, UUID currentMemberUuid, String lastUlid, int size) {
        List<PostSummaryReadModel> readModels = postQueryRepository.findByCategoryWithCursor(
                postCategoryRequest.primaryCategoryId(), postCategoryRequest.secondaryCategoryIds(), currentMemberUuid, lastUlid, size
        );
        boolean hasNext = readModels.size() > size;
        List<PostSummaryResponse> responses = readModels.stream()
                .limit(size)
                .map(readModel -> postMapper.toPostSummaryResponse(readModel,getJsonNodeContentPreview(readModel.content(),readModel.thumbnailPath()))).toList();
        String nextUlid = hasNext && !responses.isEmpty() ? responses.get(responses.size() - 1).ulid() : null;
        return CursorPageResponse.of(responses, nextUlid, hasNext);
    }

    @Transactional
    public CursorPageResponse<PostSummaryResponse> getByKeyword(PostSearchRequest postSearchRequest, UUID currentMemberUuid, String lastUlid, int size) {
        List<PostSummaryReadModel> readModels = postQueryRepository.findByKeywordWithCursor(postSearchRequest.option(), postSearchRequest.keyword(), postSearchRequest.sort(),postSearchRequest.category().primaryCategoryId(), postSearchRequest.category().secondaryCategoryIds(), currentMemberUuid, lastUlid, size);
        boolean hasNext = readModels.size() > size;
        List<PostSummaryResponse> responses = readModels.stream()
                .limit(size)
                .map(readModel -> postMapper.toPostSummaryResponse(readModel,getJsonNodeContentPreview(readModel.content(),readModel.thumbnailPath()))).toList();
        String nextUlid = hasNext && !responses.isEmpty() ? responses.get(responses.size() - 1).ulid() : null;
        postSearchHistoryRepository.saveSearchKeyword(currentMemberUuid, postSearchRequest.keyword());
        return CursorPageResponse.of(responses, nextUlid, hasNext);
    }

    public PostDetailResponse getByUlid(String ulid, UUID currentMemberUuid, UUID guestId) {
        PostId postId = PostId.create(ulid);
        return postQueryRepository.findPostDetailByPostId(postId,currentMemberUuid)
                .filter(PostDetailReadModel::isPublished)
                .map(postDetail -> {
                    increaseViewCount(ulid,currentMemberUuid,guestId);
                    postRecentlyViewRepository.recordViewPost(currentMemberUuid,postId);
                    return postMapper.postDetailReadModelToPostDetailResponse(
                            postDetail,
                            (postDetail.imagePath() != null && !postDetail.imagePath().isBlank()) ? s3FileService.generateS3SrcUrl(postDetail.imagePath()) : null,
                            getJsonNodeContent(postDetail.content()),
                            readViewCount(ulid)
                    );
                }).orElseThrow(() -> new PostNotFoundException());
    }

    public PostDetailResponse getDataByUlid(String ulid, UUID currentMemberUuid) {
        return postQueryRepository.findPostDetailDataByPostId(PostId.create(ulid))
                .filter(postDetailData -> postDetailData.isPublished() ||
                        (!postDetailData.isPublished() && postDetailData.authorUuid().equals(currentMemberUuid)))
                .map(postDetailData -> postMapper.postDetailDataReadModelToPostDetailResponse(
                        postDetailData,
                        getJsonNodeContent(postDetailData.content()),
                        multipartDataProcessorPort.extractOriginalFilenameFromFileKey(postDetailData.thumbnailPath())))
                .orElseThrow(() -> new PostNotFoundException());
    }

    @Transactional
    public void createPost(PostInsertRequest postInsertRequest, UUID currentMemberUuid) throws IOException {
        AuthorId authorId = AuthorId.fromUuid(currentMemberUuid);
        Post post;
        if (postInsertRequest.isPublished()) {
            PrimaryCategoryId primaryCategoryId = PrimaryCategoryId.create(postInsertRequest.primaryCategoryId());
            SecondaryCategoryId secondaryCategoryId = SecondaryCategoryId.create(postInsertRequest.secondaryCategoryId());
            ContentProcessRecord result = multipartDataProcessorPort.saveFilesAndGenerateContentJson(postInsertRequest.content(), postInsertRequest.orderInfo(), postInsertRequest.thumbnailFilename());
            post = Post.createPublished(authorId, primaryCategoryId, secondaryCategoryId,  PostContent.create(postInsertRequest.title(), result.content(), result.thumbnailPath()));
        } else {
            PrimaryCategoryId primaryCategoryId = postInsertRequest.primaryCategoryId() != null ? PrimaryCategoryId.create(postInsertRequest.primaryCategoryId()) : null;
            SecondaryCategoryId secondaryCategoryId = postInsertRequest.secondaryCategoryId() != null ? SecondaryCategoryId.create(postInsertRequest.secondaryCategoryId()) : null;
            ContentProcessRecord result;
            if (postInsertRequest.content() != null && postInsertRequest.orderInfo() != null) {
                result = multipartDataProcessorPort.saveFilesAndGenerateContentJson(postInsertRequest.content(), postInsertRequest.orderInfo(), postInsertRequest.thumbnailFilename());
            } else if (postInsertRequest.content() == null && postInsertRequest.orderInfo() == null) {
                result = new ContentProcessRecord(null,null);
            } else {
                throw new EmptyValueException(PostErrorCode.EMPTY_POST_CONTENT);
            }
            post = Post.createDraft(authorId, primaryCategoryId, secondaryCategoryId, PostContent.createDraft(postInsertRequest.title(), result.content(), result.thumbnailPath()));
        }
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(PostUpdateRequest postUpdateRequest, UUID currentMemberUuid) throws IOException {
        Post post = postRepository.getPostByUlid(PostId.create(postUpdateRequest.ulid()))
                .filter(p -> p.getAuthorId().equals(AuthorId.fromUuid(currentMemberUuid))).orElseThrow();
        // 게시글 검증 수행
        multipartDataProcessorPort.deleteFiles(post.getPostContent().getContent());
        if (postUpdateRequest.isPublished()) {
            ContentProcessRecord result = multipartDataProcessorPort.saveFilesAndGenerateContentJson(postUpdateRequest.content(), postUpdateRequest.orderInfo(), postUpdateRequest.thumbnailFilename());
            post.update(
                    AuthorId.fromUuid(currentMemberUuid),
                    PrimaryCategoryId.create(postUpdateRequest.primaryCategoryId()),
                    SecondaryCategoryId.create(postUpdateRequest.secondaryCategoryId()),
                    PostContent.create(postUpdateRequest.title(), result.content(), result.thumbnailPath()),
                    PostStatus.published()
            );
        } else {
            ContentProcessRecord result;
            if (postUpdateRequest.content() != null && postUpdateRequest.orderInfo() != null) {
                result = multipartDataProcessorPort.saveFilesAndGenerateContentJson(postUpdateRequest.content(), postUpdateRequest.orderInfo(), postUpdateRequest.thumbnailFilename());
            } else if (postUpdateRequest.content() == null && postUpdateRequest.orderInfo() == null) {
                result = new ContentProcessRecord(null,null);
            } else {
                throw new EmptyValueException(PostErrorCode.EMPTY_POST_CONTENT);
            }
            post.updateDraft(
                    AuthorId.fromUuid(currentMemberUuid),
                    postUpdateRequest.primaryCategoryId() != null ? PrimaryCategoryId.create(postUpdateRequest.primaryCategoryId()) : null,
                    postUpdateRequest.secondaryCategoryId() != null ? SecondaryCategoryId.create(postUpdateRequest.secondaryCategoryId()) : null,
                    PostContent.createDraft(postUpdateRequest.title(), result.content(), result.thumbnailPath())
            );
        }
        postRepository.update(post);
    }

    @Transactional
    public void deletePost(String ulid, UUID currentMemberUuid) {
        Post post = postRepository.getPostByUlid(PostId.create(ulid)).orElseThrow(() -> new PostNotFoundException());
        if (!post.getAuthorId().equals(AuthorId.fromUuid(currentMemberUuid))) {
            throw new PostAccessDeniedException();
        }
        if (post.getStatus().isPublished()) {
            postArchiveRepository.save(PostId.create(ulid));
            postRepository.deletePostLikeByPostId(post.getPostId());
            postRepository.deletePostBookmarkByPostId(post.getPostId());
            postRepository.deletePostRecentlyViewRecordByPostId(post.getPostId());
        }
        multipartDataProcessorPort.deleteFiles(post.getPostContent().getContent());
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

    public List<String> getSearchHistory(UUID currentMemberUuid, int size) {
        return postSearchHistoryRepository.getSearchHistory(currentMemberUuid, size);
    }

    public void deleteSearchKeyword(UUID currentMemberUuid, String keyword) {
        postSearchHistoryRepository.removeSearchKeyword(currentMemberUuid,keyword);
    }

    public void deleteAllSearchHistory(UUID currentMemberUuid) {
        postSearchHistoryRepository.removeAllSearchHistory(currentMemberUuid);
    }

    private JsonNode getJsonNodeContentPreview(JsonNode content, String thumbnailPath) {
        if (content == null) return null;
        JsonNode contentPreview;
        try {
            contentPreview = multipartDataProcessorPort.convertToPreview(content, thumbnailPath);
        } catch (IOException e) {
            throw new ContentProcessingException();
        }
        return contentPreview;
    }

    private JsonNode getJsonNodeContent(JsonNode content) {
        if (content == null) return null;
        JsonNode newContent;
        try {
            newContent = multipartDataProcessorPort.convertFileSrcToFullFileSrc(content);
        } catch (IOException e) {
            throw new ContentProcessingException();
        }
        return newContent;
    }
}
