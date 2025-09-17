package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.domain.vo.SecondaryCategoryId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface PostRepository {

    Post save(Post post);

    void delete(Post post);

    Page<Post> getPublishedPosts(Pageable pageable);

    Page<Post> getPublishedPostsByPrimaryCategory(PrimaryCategoryId primaryCategoryId, Pageable pageable);

    Page<Post> getPublishedPostsBySecondaryCategory(SecondaryCategoryId secondaryCategoryId, Pageable pageable);

    Page<Post> getPublishedPostsByAuthor(AuthorId authorId, Pageable pageable);

    Page<Post> getDraftPostsByAuthor(AuthorId authorId, Pageable pageable);

    Optional<Post> getPostByUlid(PostId postId);

    Page<Post> getPublishedPostsByTitleOrContent(String keyword, Pageable pageable);

    Long getViewCountByUlid(PostId postId);

    int updateViewCount(PostId postId, Long viewCount);

}
