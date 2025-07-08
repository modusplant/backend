package kr.modusplant.domains.communication.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommPostViewLockRedisRepository {
    private final StringRedisTemplate stringRedisTemplate;

    // viewCount:comm_post:{ulid}:member:{member_uuid}:lock
    private static final String KEY_FORMAT = "viewCount:comm_post:%s:member:%s:lock";

    public boolean lock(String ulid, UUID memberUuid, long ttlMinutes) {
        String key = generateKey(ulid,memberUuid);
        return stringRedisTemplate.opsForValue().setIfAbsent(key,"",Duration.ofMinutes(ttlMinutes));
    }

    private String generateKey(String ulid, UUID memberUuid) {
        return KEY_FORMAT.formatted(ulid, memberUuid);
    }
}
