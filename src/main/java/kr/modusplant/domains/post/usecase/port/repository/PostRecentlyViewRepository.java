package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.PostId;

import java.util.List;
import java.util.UUID;

public interface PostRecentlyViewRepository {
    void recordViewPost(UUID memberUuid, PostId postId);

    List<PostId> getRecentlyViewPostIds(UUID memberUuid, int page, int size);

    long getTotalRecentlyViewPosts(UUID memberUuid);
}
