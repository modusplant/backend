package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.domain.aggregate.Post;
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
import kr.modusplant.framework.jpa.repository.*;
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
        SiteMemberEntity authorEntity = authorJpaRepository.findByUuid(post.getAuthorId().getValue()).orElseThrow();
        SiteMemberEntity createMember = authorJpaRepository.findByUuid(post.getCreateAuthorId().getValue()).orElseThrow();
        CommPrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findById(post.getPrimaryCategoryId().getValue()).orElseThrow();
        CommSecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findById(post.getSecondaryCategoryId().getValue()).orElseThrow();
        postJpaRepository.save(
                postJpaMapper.toPostEntity(post, authorEntity,createMember,primaryCategoryEntity,secondaryCategoryEntity,0L)
        );
    }

    @Override
    public void update(Post post) {
        CommPrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findById(post.getPrimaryCategoryId().getValue()).orElseThrow();
        CommSecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findById(post.getSecondaryCategoryId().getValue()).orElseThrow();
        CommPostEntity postEntity = postJpaRepository.findByUlid(post.getPostId().getValue()).orElseThrow();
        postEntity.updatePrimaryCategory(primaryCategoryEntity);
        postEntity.updateSecondaryCategory(secondaryCategoryEntity);
        postEntity.updateViewCount(postViewCountRedisRepository.read(post.getPostId()));
        postEntity.updateTitle(post.getPostContent().getTitle());
        postEntity.updateContent(post.getPostContent().getContent());
        postEntity.updateIsPublished(post.getStatus().isPublished());
        postEntity.updatePublishedAt(post.getStatus().isPublished() ? LocalDateTime.now() : null);
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
        return postJpaRepository.findByUlid(postId.getValue()).orElseThrow().getViewCount();
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
