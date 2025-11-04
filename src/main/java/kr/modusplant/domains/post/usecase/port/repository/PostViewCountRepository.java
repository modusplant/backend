package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.PostId;

import java.util.Map;

public interface PostViewCountRepository {
    Long read(PostId postId);
    Long increase(PostId postId);
    void write(PostId postId, Long viewCount);
    Map<String, Long> findAll();
}
