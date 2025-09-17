package kr.modusplant.domains.post.adapter.controller;

import kr.modusplant.domains.post.domain.vo.*;
import kr.modusplant.domains.post.usecase.port.repository.*;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
import kr.modusplant.domains.post.usecase.response.PostResponse;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
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

    public Page<PostResponse> getAll(Pageable pageable) {
        return postRepository.getPublishedPosts(pageable)
                .map(post -> {
                    try {
                        post.updateContent(postMapper.toContentJson(post));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostResponse(post,postViewCountRepository.read(PostId.create(post.getPostId().getValue())));
                });
    }

    public Page<PostResponse> getByMemberUuid(UUID memberUuid, Pageable pageable) {
        return postRepository.getPublishedPostsByAuthor(AuthorId.fromUuid(memberUuid),pageable)
                .map(post -> {
                    try {
                        post.updateContent(postMapper.toContentJson(post));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostResponse(post,postViewCountRepository.read(PostId.create(post.getPostId().getValue())));
                });
    }

    public Page<PostResponse> getDraftByMemberUuid(UUID currentMemberUuid, Pageable pageable) {
        return postRepository.getDraftPostsByAuthor(AuthorId.fromUuid(currentMemberUuid), pageable)
                .map(post -> {
                    try {
                        post.updateContent(postMapper.toContentJson(post));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostResponse(post, 0L);
                });
    }

    public Page<PostResponse> getByPrimaryCategoryUuid(UUID categoryUuid, Pageable pageable) {
        return postRepository.getPublishedPostsByPrimaryCategory(PrimaryCategoryId.fromUuid(categoryUuid),pageable)
                .map(post -> {
                    try {
                        post.updateContent(postMapper.toContentJson(post));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostResponse(post,postViewCountRepository.read(PostId.create(post.getPostId().getValue())));
                });
    }

    public Page<PostResponse> getBySecondaryCategoryUuid(UUID categoryUuid, Pageable pageable) {
        return postRepository.getPublishedPostsBySecondaryCategory(SecondaryCategoryId.fromUuid(categoryUuid),pageable)
                .map(post -> {
                    try {
                        post.updateContent(postMapper.toContentJson(post));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostResponse(post,postViewCountRepository.read(PostId.create(post.getPostId().getValue())));
                });
    }

    public Page<PostResponse> searchByKeyword(String keyword, Pageable pageable) {
        return postRepository.getPublishedPostsByTitleOrContent(keyword,pageable)
                .map(post -> {
                    try {
                        post.updateContent(postMapper.toContentJson(post));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostResponse(post,postViewCountRepository.read(PostId.create(post.getPostId().getValue())));
                });
    }

    public Optional<PostResponse> getByUlid(String ulid,UUID currentMemberUuid) {
        return postRepository.getPostByUlid(PostId.create(ulid))
                .filter(post -> post.getStatus().isPublished() ||
                        (post.getStatus().isDraft() && post.getAuthorId().equals(AuthorId.fromUuid(currentMemberUuid))))
                .map(post -> {
                    try {
                        post.updateContent(postMapper.toContentJson(post));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return postMapper.toPostResponse(
                            post,
                            post.getStatus().isPublished() ? postViewCountRepository.read(PostId.create(ulid)) : 0L);
                });
    }

    @Transactional
    public PostResponse createPost(PostInsertRequest postInsertRequest, UUID currentMemberUuid) throws IOException {
        AuthorId authorId = AuthorId.fromUuid(currentMemberUuid);
        PrimaryCategoryId primaryCategoryId = PrimaryCategoryId.fromUuid(postInsertRequest.primaryCategoryUuid());
        SecondaryCategoryId secondaryCategoryId = SecondaryCategoryId.fromUuid(postInsertRequest.secondaryCategoryUuid());
        PostContent postContent = postMapper.toPostContent(postInsertRequest);
        Post post = postInsertRequest.isPublished()
                ? Post.createPublished(authorId, primaryCategoryId, secondaryCategoryId, postContent)
                : Post.createDraft(authorId, primaryCategoryId, secondaryCategoryId, postContent);
        return postMapper.toPostResponse(postRepository.save(post),0L);
    }

    @Transactional
    public PostResponse updatePost(PostUpdateRequest postUpdateRequest, UUID currentMemberUuid) throws IOException {
        Post post = postRepository.getPostByUlid(PostId.create(postUpdateRequest.ulid()))
                .filter(p -> p.getAuthorId().equals(AuthorId.fromUuid(currentMemberUuid))).orElseThrow();
        multipartDataProcessorPort.deleteFiles(post.getPostContent().getContent());
        post.update(
                AuthorId.fromUuid(currentMemberUuid),
                PrimaryCategoryId.fromUuid(postUpdateRequest.primaryCategoryUuid()),
                SecondaryCategoryId.fromUuid(postUpdateRequest.secondaryCategoryUuid()),
                postMapper.toPostContent(postUpdateRequest),
                postUpdateRequest.isPublished() ? PostStatus.published() : PostStatus.draft()
        );
        return postMapper.toPostResponse(postRepository.save(post),postViewCountRepository.read(PostId.create(post.getPostId().getValue())));
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
