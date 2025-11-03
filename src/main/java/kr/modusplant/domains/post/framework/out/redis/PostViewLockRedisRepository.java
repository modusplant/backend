package kr.modusplant.domains.post.framework.out.redis;

import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.port.repository.PostViewLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostViewLockRedisRepository implements PostViewLockRepository {
    private final StringRedisTemplate stringRedisTemplate;

    // viewCount:comm_post:{ulid}:member:{member_uuid}:lock
    private static final String KEY_FORMAT = "viewCount:comm_post:%s:member:%s:lock";

    public boolean lock(PostId postId, UUID memberUuid, long ttlMinutes) {
        String key = generateKey(postId.getValue(),memberUuid);
        return stringRedisTemplate.opsForValue().setIfAbsent(key,"",Duration.ofMinutes(ttlMinutes));
    }

    private String generateKey(String ulid, UUID memberUuid) {
        return KEY_FORMAT.formatted(ulid, memberUuid);
    }
}
