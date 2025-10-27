package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.domain.vo.SecondaryCategoryId;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostJpaMapper;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PostJpaRepository;
import kr.modusplant.domains.post.framework.out.redis.PostViewCountRedisRepository;
import kr.modusplant.domains.post.usecase.model.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.port.repository.PostRepository;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.CommPrimaryCategoryJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommSecondaryCategoryJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public PostDetailReadModel save(Post post) {
        SiteMemberEntity authorEntity = authorJpaRepository.findByUuid(post.getAuthorId().getValue()).orElseThrow();
        SiteMemberEntity createMember = authorJpaRepository.findByUuid(post.getCreateAuthorId().getValue()).orElseThrow();
        CommPrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findByUuid(post.getPrimaryCategoryId().getValue()).orElseThrow();
        CommSecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findByUuid(post.getSecondaryCategoryId().getValue()).orElseThrow();
        return postJpaMapper.toPostDetailReadModel(postJpaRepository.save(
                postJpaMapper.toPostEntity(post, authorEntity,createMember,primaryCategoryEntity,secondaryCategoryEntity,postViewCountRedisRepository.read(post.getPostId()))
        ));
    }

    @Override
    public void delete(Post post) {
        postJpaRepository.deleteByUlid(post.getPostId().getValue());
    }


    @Override
    public Page<PostSummaryReadModel> getPublishedPosts(PrimaryCategoryId primaryCategoryId, List<SecondaryCategoryId> secondaryCategoryIds, String keyword, Pageable pageable) {
        return postJpaRepository.findByDynamicConditionsAndIsPublishedTrue(
                primaryCategoryId.getValue(),
                secondaryCategoryIds.stream().map(SecondaryCategoryId::getValue).toList(),
                keyword,
                pageable
        );
    }

    @Override
    public Page<PostSummaryReadModel> getPublishedPostsByAuthor(AuthorId authorId, PrimaryCategoryId primaryCategoryId, List<SecondaryCategoryId> secondaryCategoryIds, String keyword, Pageable pageable) {
        return postJpaRepository.findByAuthMemberAndDynamicConditionsAndIsPublishedTrue(
                authorId.getValue(),
                primaryCategoryId.getValue(),
                secondaryCategoryIds.stream().map(SecondaryCategoryId::getValue).toList(),
                keyword,
                pageable
        );
    }

    @Override
    public Page<PostSummaryReadModel> getDraftPostsByAuthor(AuthorId authorId, Pageable pageable) {
        return postJpaRepository.findByAuthMemberAndIsPublishedFalseOrderByUpdatedAtDesc(
                authorJpaRepository.findByUuid(authorId.getValue()).orElseThrow(),
                pageable
        ).map(postJpaMapper::toPostSummaryReadModel);
    }

    @Override
    public Optional<Post> getPostByUlid(PostId postId) {
        return postJpaRepository.findByUlid(postId.getValue()).map(postJpaMapper::toPost);
    }

    @Override
    public Optional<PostDetailReadModel> getPostDetailByUlid(PostId postId) {
        return postJpaRepository.findByUlid(postId.getValue()).map(postJpaMapper::toPostDetailReadModel);
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
