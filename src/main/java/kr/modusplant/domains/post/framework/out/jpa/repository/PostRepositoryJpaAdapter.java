package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.exception.AuthorNotFoundException;
import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import kr.modusplant.domains.post.domain.exception.PostNotFoundException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostJpaMapper;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PostJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.SecondaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.out.redis.PostRecentlyViewRedisRepository;
import kr.modusplant.domains.post.framework.out.redis.PostViewCountRedisRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostRepository;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.CommPostBookmarkJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostLikeJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryJpaAdapter implements PostRepository {
    private final PostJpaMapper postJpaMapper;
    private final PostJpaRepository postJpaRepository;
    private final SiteMemberJpaRepository authorJpaRepository;
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryJpaRepository;
    private final PostViewCountRedisRepository postViewCountRedisRepository;
    private final CommPostLikeJpaRepository postLikeJpaRepository;
    private final CommPostBookmarkJpaRepository postBookmarkJpaRepository;
    private final PostRecentlyViewRedisRepository postRecentlyViewRedisRepository;

    @Override
    public void save(Post post) {
        SiteMemberEntity authorEntity = authorJpaRepository.findByUuid(post.getAuthorId().getValue()).orElseThrow(() -> new AuthorNotFoundException());
        SiteMemberEntity createMember = authorJpaRepository.findByUuid(post.getCreateAuthorId().getValue()).orElseThrow(() -> new AuthorNotFoundException());
        CommPrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findById(post.getPrimaryCategoryId().getValue()).orElseThrow(() -> new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ID));
        CommSecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findById(post.getSecondaryCategoryId().getValue())
                .filter(secondaryCategory -> secondaryCategory.getPrimaryCategoryEntity().equals(primaryCategoryEntity))
                .orElseThrow(() -> new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ID));
        postJpaRepository.save(
                postJpaMapper.toPostEntity(post, authorEntity,createMember,primaryCategoryEntity,secondaryCategoryEntity,0L)
        );
    }

    @Override
    public void update(Post post) {
        CommPrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findById(post.getPrimaryCategoryId().getValue()).orElseThrow(() -> new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ID));
        CommSecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findById(post.getSecondaryCategoryId().getValue())
                .filter(secondaryCategory -> secondaryCategory.getPrimaryCategoryEntity().equals(primaryCategoryEntity))
                .orElseThrow(() -> new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ID));
        CommPostEntity postEntity = postJpaRepository.findByUlid(post.getPostId().getValue()).orElseThrow(() -> new PostNotFoundException());
        postEntity.updatePrimaryCategory(primaryCategoryEntity);
        postEntity.updateSecondaryCategory(secondaryCategoryEntity);
        postEntity.updateViewCount(postViewCountRedisRepository.read(post.getPostId()));
        postEntity.updateTitle(post.getPostContent().getTitle());
        postEntity.updateContent(post.getPostContent().getContent());
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
        return postJpaRepository.findByUlid(postId.getValue()).orElseThrow(() -> new PostNotFoundException()).getViewCount();
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
