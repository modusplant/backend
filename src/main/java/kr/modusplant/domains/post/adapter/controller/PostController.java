package kr.modusplant.domains.post.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.exception.ContentProcessingException;
import kr.modusplant.domains.post.domain.exception.PostAccessDeniedException;
import kr.modusplant.domains.post.domain.exception.PostNotFoundException;
import kr.modusplant.domains.post.domain.vo.*;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.repository.*;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.request.PostCategoryRequest;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
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
    private final MultipartDataProcessorPort multipartDataProcessorPort;
    private final PostViewCountRepository postViewCountRepository;
    private final PostViewLockRepository postViewLockRepository;
    private final PostArchiveRepository postArchiveRepository;
    private final PostRecentlyViewRepository postRecentlyViewRepository;

    @Value("${redis.ttl.view_count}")
    private long ttlMinutes;

    public CursorPageResponse<PostSummaryResponse> getAll(PostCategoryRequest postCategoryRequest, UUID currentMemberUuid, String lastUlid, int size) {
        List<PostSummaryReadModel> readModels = postQueryRepository.findByCategoryWithCursor(
                postCategoryRequest.primaryCategoryUuid(), postCategoryRequest.secondaryCategoryUuids(), currentMemberUuid, lastUlid, size
        );
        boolean hasNext = readModels.size() > size;
        List<PostSummaryResponse> responses = readModels.stream()
                .limit(size)
                .map(readModel -> postMapper.toPostSummaryResponse(readModel,getJsonNodeContentPreview(readModel.content()))).toList();
        String nextUlid = hasNext && !responses.isEmpty() ? responses.get(responses.size() - 1).ulid() : null;
        return CursorPageResponse.of(responses, nextUlid, hasNext);
    }

    public CursorPageResponse<PostSummaryResponse> getByKeyword(String keyword, UUID currentMemberUuid, String lastUlid, int size) {
        List<PostSummaryReadModel> readModels = postQueryRepository.findByKeywordWithCursor(keyword,currentMemberUuid, lastUlid, size);
        boolean hasNext = readModels.size() > size;
        List<PostSummaryResponse> responses = readModels.stream()
                .limit(size)
                .map(readModel -> postMapper.toPostSummaryResponse(readModel,getJsonNodeContentPreview(readModel.content()))).toList();
        String nextUlid = hasNext && !responses.isEmpty() ? responses.get(responses.size() - 1).ulid() : null;
        return CursorPageResponse.of(responses, nextUlid, hasNext);
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
                            getJsonNodeContent(postDetail.content()),
                            readViewCount(ulid)
                    );
                }).orElseThrow(() -> new PostNotFoundException());
    }

    public PostDetailResponse getDataByUlid(String ulid, UUID currentMemberUuid) {
        return postQueryRepository.findPostDetailDataByPostId(PostId.create(ulid))
                .filter(postDetailData -> postDetailData.isPublished() ||
                        (!postDetailData.isPublished() && postDetailData.authorUuid().equals(currentMemberUuid)))
                .map(postDetailData -> postMapper.toPostDetailResponse(postDetailData, getJsonNodeContent(postDetailData.content())))
                .orElseThrow(() -> new PostNotFoundException());
    }

    @Transactional
    public void createPost(PostInsertRequest postInsertRequest, UUID currentMemberUuid) throws IOException {
        AuthorId authorId = AuthorId.fromUuid(currentMemberUuid);
        PrimaryCategoryId primaryCategoryId = PrimaryCategoryId.fromUuid(postInsertRequest.primaryCategoryUuid());
        SecondaryCategoryId secondaryCategoryId = SecondaryCategoryId.fromUuid(postInsertRequest.secondaryCategoryUuid());
        PostContent postContent = PostContent.create(postInsertRequest.title(), multipartDataProcessorPort.saveFilesAndGenerateContentJson(postInsertRequest.content(), postInsertRequest.orderInfo()));
        Post post = postInsertRequest.isPublished()
                ? Post.createPublished(authorId, primaryCategoryId, secondaryCategoryId, postContent)
                : Post.createDraft(authorId, primaryCategoryId, secondaryCategoryId, postContent);
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(PostUpdateRequest postUpdateRequest, UUID currentMemberUuid) throws IOException {
        Post post = postRepository.getPostByUlid(PostId.create(postUpdateRequest.ulid()))
                .filter(p -> p.getAuthorId().equals(AuthorId.fromUuid(currentMemberUuid))).orElseThrow();
        multipartDataProcessorPort.deleteFiles(post.getPostContent().getContent());
        PostContent postContent = PostContent.create(postUpdateRequest.title(),multipartDataProcessorPort.saveFilesAndGenerateContentJson(postUpdateRequest.content(), postUpdateRequest.orderInfo()));
        post.update(
                AuthorId.fromUuid(currentMemberUuid),
                PrimaryCategoryId.fromUuid(postUpdateRequest.primaryCategoryUuid()),
                SecondaryCategoryId.fromUuid(postUpdateRequest.secondaryCategoryUuid()),
                postContent,
                postUpdateRequest.isPublished() ? PostStatus.published() : PostStatus.draft()
        );
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
                        .map(postModel -> postMapper.toPostSummaryResponse(postModel,getJsonNodeContentPreview(postModel.content()))));
    }

    public OffsetPageResponse<DraftPostResponse> getDraftByMemberUuid(UUID currentMemberUuid, int page, int size) {
        return OffsetPageResponse.from(
                postQueryForMemberRepository.findDraftByAuthMemberWithOffset(AuthorId.fromUuid(currentMemberUuid),page,size)
                        .map(postModel -> postMapper.toDraftPostResponse(postModel, getJsonNodeContentPreview(postModel.content()))));
    }

    public OffsetPageResponse<PostSummaryResponse> getRecentlyViewByMemberUuid(UUID currentMemberUuid, int page, int size) {
        List<PostId> postIds = postRecentlyViewRepository.getRecentlyViewPostIds(currentMemberUuid,page,size);
        long totalElements = postRecentlyViewRepository.getTotalRecentlyViewPosts(currentMemberUuid);
        if (postIds.isEmpty()) {
            return OffsetPageResponse.from(new PageImpl<>(List.of(),PageRequest.of(page,size),totalElements));
        }
        List<PostSummaryResponse> postsPages = postQueryForMemberRepository.findByIds(postIds,currentMemberUuid)
                .stream()
                .map(postModel -> postMapper.toPostSummaryResponse(postModel, getJsonNodeContentPreview(postModel.content())))
                .toList();
        return OffsetPageResponse.from(new PageImpl<>(postsPages,PageRequest.of(page,size),totalElements));
    }

    public OffsetPageResponse<PostSummaryResponse> getLikedByMemberUuid(UUID currentMemberUuid, int page, int size) {
        return OffsetPageResponse.from(
                postQueryForMemberRepository.findLikedByMemberWithOffset(currentMemberUuid,page,size)
                        .map(postModel -> postMapper.toPostSummaryResponse(postModel, getJsonNodeContentPreview(postModel.content()))));
    }

    public OffsetPageResponse<PostSummaryResponse> getBookmarkedByMemberUuid(UUID currentMemberUuid, int page, int size) {
        return OffsetPageResponse.from(
                postQueryForMemberRepository.findBookmarkedByMemberWithOffset(currentMemberUuid,page,size)
                        .map(postModel -> postMapper.toPostSummaryResponse(postModel, getJsonNodeContentPreview(postModel.content())))
        );
    }

    private JsonNode getJsonNodeContentPreview(JsonNode content) {
        JsonNode contentPreview;
        try {
            contentPreview = multipartDataProcessorPort.convertToPreview(content);
        } catch (IOException e) {
            throw new ContentProcessingException();
        }
        return contentPreview;
    }

    private JsonNode getJsonNodeContent(JsonNode content) {
        JsonNode newContent;
        try {
            newContent = multipartDataProcessorPort.convertFileSrcToFullFileSrc(content);
        } catch (IOException e) {
            throw new ContentProcessingException();
        }
        return newContent;
    }
}
