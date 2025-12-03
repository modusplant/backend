package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostJpaMapper;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PostJpaRepository;
import kr.modusplant.domains.post.framework.out.redis.PostViewCountRedisRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostRepository;
import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.CommPrimaryCategoryJpaRepository;
import kr.modusplant.framework.jpa.repository.CommSecondaryCategoryJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryJpaAdapter implements PostRepository {
    private final PostJpaMapper postJpaMapper;
    private final PostJpaRepository postJpaRepository;
    private final SiteMemberJpaRepository authorJpaRepository;
    private final CommPrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final CommSecondaryCategoryJpaRepository secondaryCategoryJpaRepository;
    private final PostViewCountRedisRepository postViewCountRedisRepository;

    @Override
    public void save(Post post) {
        SiteMemberEntity authorEntity = authorJpaRepository.findByUuid(post.getAuthorId().getValue()).orElseThrow();
        SiteMemberEntity createMember = authorJpaRepository.findByUuid(post.getCreateAuthorId().getValue()).orElseThrow();
        CommPrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findByUuid(post.getPrimaryCategoryId().getValue()).orElseThrow();
        CommSecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findByUuid(post.getSecondaryCategoryId().getValue()).orElseThrow();
        postJpaRepository.save(
                postJpaMapper.toPostEntity(post, authorEntity,createMember,primaryCategoryEntity,secondaryCategoryEntity,0L)
        );
    }

    @Override
    public void update(Post post) {
        SiteMemberEntity authorEntity = authorJpaRepository.findByUuid(post.getAuthorId().getValue()).orElseThrow();
        SiteMemberEntity createMember = authorJpaRepository.findByUuid(post.getCreateAuthorId().getValue()).orElseThrow();
        CommPrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findByUuid(post.getPrimaryCategoryId().getValue()).orElseThrow();
        CommSecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findByUuid(post.getSecondaryCategoryId().getValue()).orElseThrow();
        postJpaRepository.save(
                postJpaMapper.toPostEntity(post, authorEntity,createMember,primaryCategoryEntity,secondaryCategoryEntity,postViewCountRedisRepository.read(post.getPostId()))
        );
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

}
