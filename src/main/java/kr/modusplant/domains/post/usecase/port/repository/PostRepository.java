package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.PostId;

import java.util.Optional;

public interface PostRepository {

    void save(Post post);

    void update(Post post);

    void delete(Post post);

    Optional<Post> getPostByUlid(PostId postId);

    Long getViewCountByUlid(PostId postId);

    int updateViewCount(PostId postId, Long viewCount);

    void deletePostLikeByPostId(PostId postId);

    void deletePostBookmarkByPostId(PostId postId);

    void deletePostRecentlyViewRecordByPostId(PostId postId);

}
