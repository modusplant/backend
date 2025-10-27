package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.PostId;

public interface PostArchiveRepository {
    void save(PostId postId);
}
