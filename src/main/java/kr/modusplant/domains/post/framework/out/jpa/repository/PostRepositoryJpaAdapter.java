package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.repository.PostBookmarkJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.repository.PostLikeJpaRepository;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.exception.AuthorNotFoundException;
import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import kr.modusplant.domains.post.domain.exception.PostNotFoundException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.out.jpa.mapper.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.mapper.SecondaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostJpaMapper;
import kr.modusplant.domains.post.framework.out.redis.PostRecentlyViewRedisRepository;
import kr.modusplant.domains.post.framework.out.redis.PostViewCountRedisRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryJpaAdapter implements PostRepository {
    private final PostJpaMapper postJpaMapper;
    private final PostJpaRepository postJpaRepository;
    private final MemberJpaRepository authorJpaRepository;
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryJpaRepository;
    private final PostViewCountRedisRepository postViewCountRedisRepository;
    private final PostLikeJpaRepository postLikeJpaRepository;
    private final PostBookmarkJpaRepository postBookmarkJpaRepository;
    private final PostRecentlyViewRedisRepository postRecentlyViewRedisRepository;

    @Override
    public void save(Post post) {
        // post : category id, title, content는 null일수도 있음
        MemberEntity authorEntity = authorJpaRepository.findByUuid(post.getAuthorId().getValue()).orElseThrow(AuthorNotFoundException::new);
        PrimaryCategoryEntity primaryCategoryEntity = post.getPrimaryCategoryId() != null
                ? primaryCategoryJpaRepository.findById(post.getPrimaryCategoryId().getValue()).orElseThrow(() -> new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ID))
                : null;
        SecondaryCategoryEntity secondaryCategoryEntity = post.getSecondaryCategoryId() != null
                ? secondaryCategoryJpaRepository.findById(post.getSecondaryCategoryId().getValue())
                    .filter(secondaryCategory -> secondaryCategory.getPrimaryCategory().equals(primaryCategoryEntity))
                    .orElseThrow(() -> new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ID))
                : null;
        postJpaRepository.save(
                postJpaMapper.toPostEntity(post, authorEntity, primaryCategoryEntity,secondaryCategoryEntity,0L)
        );
    }

    @Override
    public void update(Post post) {
        PostEntity postEntity = postJpaRepository.findByUlid(post.getPostId().getValue()).orElseThrow(PostNotFoundException::new);
        PrimaryCategoryEntity primaryCategoryEntity = post.getPrimaryCategoryId() != null
                ? primaryCategoryJpaRepository.findById(post.getPrimaryCategoryId().getValue()).orElseThrow(() -> new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ID))
                : null;
        SecondaryCategoryEntity secondaryCategoryEntity = post.getSecondaryCategoryId() != null
                ? secondaryCategoryJpaRepository.findById(post.getSecondaryCategoryId().getValue())
                    .filter(secondaryCategory -> secondaryCategory.getPrimaryCategory().equals(primaryCategoryEntity))
                    .orElseThrow(() -> new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ID))
                : null;
        postEntity.updatePrimaryCategory(primaryCategoryEntity);
        postEntity.updateSecondaryCategory(secondaryCategoryEntity);
        postEntity.updateViewCount(postViewCountRedisRepository.read(post.getPostId()));
        postEntity.updateTitle(post.getPostContent().getTitle());
        postEntity.updateContent(post.getPostContent().getContent());
        postEntity.updateThumbnailPath(post.getPostContent().getThumbnailPath());
        if (!postEntity.getIsPublished()) {
            postEntity.updatePublishedAt(post.getStatus().isPublished() ? LocalDateTime.now() : null);
            postEntity.updateIsPublished(post.getStatus().isPublished());
        }
        postJpaRepository.save(postEntity);
    }

    @Override
    public void delete(Post post) {
        postJpaRepository.deleteByUlid(post.getPostId().getValue());
    }

    @Override
    public Optional<Post> getPostByUlid(PostId postId) {
        return postJpaRepository.findByUlid(postId.getValue()).map(postJpaMapper::toPost);
    }

    @Override
    public Long getViewCountByUlid(PostId postId) {
        return postJpaRepository.findByUlid(postId.getValue()).orElseThrow(PostNotFoundException::new).getViewCount();
    }

    @Override
    public int updateViewCount(PostId postId, Long viewCount) {
        return postJpaRepository.updateViewCount(postId.getValue(),viewCount);
    }

    @Override
    public void deletePostLikeByPostId(PostId postId) {
        postLikeJpaRepository.deleteByPostId(postId.getValue());
    }

    @Override
    public void deletePostBookmarkByPostId(PostId postId) {
        postBookmarkJpaRepository.deleteByPostId(postId.getValue());
    }

    @Override
    public void deletePostRecentlyViewRecordByPostId(PostId postId) {
        postRecentlyViewRedisRepository.removePostFromAllMembers(postId);
    }

}
