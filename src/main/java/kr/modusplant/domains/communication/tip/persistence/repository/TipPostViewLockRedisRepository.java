package kr.modusplant.domains.communication.tip.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TipPostViewLockRedisRepository {
    private final StringRedisTemplate stringRedisTemplate;

    // viewCount:tip_post:{ulid}:member:{member_uuid}:lock
    private static final String KEY_FORMAT = "viewCount:tip_post:%s:member:%s:lock";

    public boolean lock(String ulid, UUID memberUuid, long ttlMinutes) {
        String key = generateKey(ulid,memberUuid);
        return stringRedisTemplate.opsForValue().setIfAbsent(key,"",Duration.ofMinutes(ttlMinutes));
    }

    private String generateKey(String ulid, UUID memberUuid) {
        return KEY_FORMAT.formatted(ulid, memberUuid);
    }
}
