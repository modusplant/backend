package kr.modusplant.domains.post.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.*;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.repository.PostArchiveRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostViewCountRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostViewLockRepository;
import kr.modusplant.domains.post.usecase.request.PostFilterRequest;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostController {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final MultipartDataProcessorPort multipartDataProcessorPort;
    private final PostViewCountRepository postViewCountRepository;
    private final PostViewLockRepository postViewLockRepository;
    private final PostArchiveRepository postArchiveRepository;

    @Value("${redis.ttl.view_count}")
    private long ttlMinutes;

    public Page<PostSummaryResponse> getAll(PostFilterRequest postFilterRequest, Pageable pageable) {
        return postRepository.getPublishedPosts(
                    PrimaryCategoryId.fromUuid(postFilterRequest.primaryCategoryUuid()),
                    postFilterRequest.secondaryCategoryUuids().stream().map(SecondaryCategoryId::fromUuid).toList(),
                    postFilterRequest.keyword(),
                    pageable
                ).map(postModel -> {
                    JsonNode contentPreview;
                    try {
                        contentPreview = multipartDataProcessorPort.convertToPreviewData(postModel.content());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostSummaryResponse(postModel,contentPreview);
                });
    }

    public Page<PostSummaryResponse> getByMemberUuid(UUID memberUuid, PostFilterRequest postFilterRequest, Pageable pageable) {
        return postRepository.getPublishedPostsByAuthor(
                    AuthorId.fromUuid(memberUuid),
                    PrimaryCategoryId.fromUuid(postFilterRequest.primaryCategoryUuid()),
                    postFilterRequest.secondaryCategoryUuids().stream().map(SecondaryCategoryId::fromUuid).toList(),
                    postFilterRequest.keyword(),
                    pageable
                ).map(postModel -> {
                    JsonNode contentPreview;
                    try {
                        contentPreview = multipartDataProcessorPort.convertToPreviewData(postModel.content());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostSummaryResponse(postModel,contentPreview);
                });
    }

    public Page<PostSummaryResponse> getDraftByMemberUuid(UUID currentMemberUuid, Pageable pageable) {
        return postRepository.getDraftPostsByAuthor(AuthorId.fromUuid(currentMemberUuid), pageable)
                .map(postModel -> {
                    JsonNode contentPreview;
                    try {
                        contentPreview = multipartDataProcessorPort.convertToPreviewData(postModel.content());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostSummaryResponse(postModel, contentPreview);
                });
    }

    public Optional<PostDetailResponse> getByUlid(String ulid, UUID currentMemberUuid) {
        return postRepository.getPostDetailByUlid(PostId.create(ulid))
                .filter(postDetail -> postDetail.isPublished() ||
                        (!postDetail.isPublished() && postDetail.authorUuid().equals(currentMemberUuid)))
                .map(postDetail -> {
                    JsonNode content;
                    try {
                        content = multipartDataProcessorPort.convertFileSrcToBinaryData(postDetail.content());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostDetailResponse(
                            postDetail,
                            content,
                            postDetail.isPublished() ? postViewCountRepository.read(PostId.create(ulid)) : 0L);
                });
    }

    @Transactional
    public void createPost(PostInsertRequest postInsertRequest, UUID currentMemberUuid) throws IOException {
        AuthorId authorId = AuthorId.fromUuid(currentMemberUuid);
        PrimaryCategoryId primaryCategoryId = PrimaryCategoryId.fromUuid(postInsertRequest.primaryCategoryUuid());
        SecondaryCategoryId secondaryCategoryId = SecondaryCategoryId.fromUuid(postInsertRequest.secondaryCategoryUuid());
        PostContent postContent = PostContent.create(postInsertRequest.title(), multipartDataProcessorPort.saveFilesAndGenerateContentJson(postInsertRequest.content()));
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
        PostContent postContent = PostContent.create(postUpdateRequest.title(),multipartDataProcessorPort.saveFilesAndGenerateContentJson(postUpdateRequest.content()));
        post.update(
                AuthorId.fromUuid(currentMemberUuid),
                PrimaryCategoryId.fromUuid(postUpdateRequest.primaryCategoryUuid()),
                SecondaryCategoryId.fromUuid(postUpdateRequest.secondaryCategoryUuid()),
                postContent,
                postUpdateRequest.isPublished() ? PostStatus.published() : PostStatus.draft()
        );
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(String ulid, UUID currentMemberUuid) {
        Post post = postRepository.getPostByUlid(PostId.create(ulid))
                .filter(p -> p.getAuthorId().equals(AuthorId.fromUuid(currentMemberUuid))).orElseThrow();
        if (post.getStatus().isPublished()) {
            postArchiveRepository.save(PostId.create(ulid));
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
    public Long increaseViewCount(String ulid, UUID currentMemberUuid) {
        // 조회수 어뷰징 정책 - 사용자는 게시글 1개당 ttl에 1번 조회수 증가
        if (!postViewLockRepository.lock(PostId.create(ulid), currentMemberUuid, ttlMinutes)) {
            return postViewCountRepository.read(PostId.create(ulid));
        }
        // 조회수 증가
        return postViewCountRepository.increase(PostId.create(ulid));
    }
}
