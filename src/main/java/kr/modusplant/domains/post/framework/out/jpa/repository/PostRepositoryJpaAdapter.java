package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.domain.vo.SecondaryCategoryId;
import kr.modusplant.domains.post.framework.out.jpa.entity.AuthorEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.out.jpa.mapper.PostJpaMapperImpl;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.AuthorJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PostJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.SecondaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.out.redis.PostViewCountRedisRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryJpaAdapter implements PostRepository {
    private final PostJpaMapperImpl postJpaMapper;
    private final PostJpaRepository postJpaRepository;
    private final AuthorJpaRepository authorJpaRepository;
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryJpaRepository;
    private final PostViewCountRedisRepository postViewCountRedisRepository;

    @Override
    public Post save(Post post) {
        AuthorEntity authorEntity = authorJpaRepository.findByUuid(post.getAuthorId().getValue()).orElseThrow();
        AuthorEntity createMember = authorJpaRepository.findByUuid(post.getCreateAuthorId().getValue()).orElseThrow();
        PrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findByUuid(post.getPrimaryCategoryId().getValue()).orElseThrow();
        SecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findByUuid(post.getSecondaryCategoryId().getValue()).orElseThrow();
        return postJpaMapper.toPost(postJpaRepository.save(
                postJpaMapper.toPostEntity(post, authorEntity,createMember,primaryCategoryEntity,secondaryCategoryEntity,postViewCountRedisRepository.read(post.getPostId()))
        ));
    }

    @Override
    public void delete(Post post) {
        postJpaRepository.deleteByUlid(post.getPostId().getValue());
    }


    @Override
    public Page<Post> getPublishedPosts(Pageable pageable) {
        return postJpaRepository.findByIsPublishedTrueOrderByPublishedAtDesc(pageable)
                .map(postJpaMapper::toPost);
    }

    @Override
    public Page<Post> getPublishedPostsByPrimaryCategory(PrimaryCategoryId primaryCategoryId, Pageable pageable) {
        PrimaryCategoryEntity primaryCategory = primaryCategoryJpaRepository.findByUuid(primaryCategoryId.getValue()).orElseThrow();
        return postJpaRepository.findByPrimaryCategoryAndIsPublishedTrueOrderByPublishedAtDesc(primaryCategory, pageable)
                .map(postJpaMapper::toPost);
    }

    @Override
    public Page<Post> getPublishedPostsBySecondaryCategory(SecondaryCategoryId secondaryCategoryId, Pageable pageable) {
        SecondaryCategoryEntity secondaryCategory = secondaryCategoryJpaRepository.findByUuid(secondaryCategoryId.getValue()).orElseThrow();
        return postJpaRepository.findBySecondaryCategoryAndIsPublishedTrueOrderByPublishedAtDesc(secondaryCategory, pageable)
                .map(postJpaMapper::toPost);
    }

    @Override
    public Page<Post> getPublishedPostsByAuthor(AuthorId authorId, Pageable pageable) {
        AuthorEntity authorEntity = authorJpaRepository.findByUuid(authorId.getValue()).orElseThrow();
        return postJpaRepository.findByAuthMemberAndIsPublishedTrueOrderByPublishedAtDesc(authorEntity, pageable)
                .map(postJpaMapper::toPost);
    }

    @Override
    public Page<Post> getDraftPostsByAuthor(AuthorId authorId, Pageable pageable) {
        AuthorEntity authorEntity = authorJpaRepository.findByUuid(authorId.getValue()).orElseThrow();
        return postJpaRepository.findByAuthMemberAndIsPublishedFalseOrderByUpdatedAtDesc(authorEntity, pageable)
                .map(postJpaMapper::toPost);
    }

    @Override
    public Optional<Post> getPostByUlid(PostId postId) {
        return postJpaRepository.findByUlid(postId.getValue()).map(postJpaMapper::toPost);
    }

    @Override
    public Page<Post> getPublishedPostsByTitleOrContent(String keyword, Pageable pageable) {
        return postJpaRepository.searchByTitleOrContent(keyword, pageable)
                .map(postJpaMapper::toPost);
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
