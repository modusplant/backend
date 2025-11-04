package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.PostId;

import java.util.UUID;

public interface PostViewLockRepository {
    boolean lock(PostId postId, UUID memberUuid, long ttlMinutes);
}
