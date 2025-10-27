package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.domain.vo.SecondaryCategoryId;
import kr.modusplant.domains.post.usecase.model.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    PostDetailReadModel save(Post post);

    void delete(Post post);

    Page<PostSummaryReadModel> getPublishedPosts(PrimaryCategoryId primaryCategoryId, List<SecondaryCategoryId> secondaryCategoryIds, String keyword, Pageable pageable);

    Page<PostSummaryReadModel> getPublishedPostsByAuthor(AuthorId authorId, PrimaryCategoryId primaryCategoryId, List<SecondaryCategoryId> secondaryCategoryIds, String keyword, Pageable pageable);

    Page<PostSummaryReadModel> getDraftPostsByAuthor(AuthorId authorId, Pageable pageable);

    Optional<Post> getPostByUlid(PostId postId);

    Optional<PostDetailReadModel> getPostDetailByUlid(PostId postId);

    Long getViewCountByUlid(PostId postId);

    int updateViewCount(PostId postId, Long viewCount);

}
