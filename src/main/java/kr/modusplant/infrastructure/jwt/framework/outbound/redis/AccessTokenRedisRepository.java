package kr.modusplant.infrastructure.jwt.framework.outbound.redis;

import kr.modusplant.shared.framework.redis.RedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;

import static kr.modusplant.shared.framework.redis.RedisKeys.BLACKLIST_ACCESS_TOKEN_PREFIX;
import static kr.modusplant.shared.framework.redis.RedisKeys.generateRedisKeyWithEqualCase;
import static kr.modusplant.shared.util.EncryptUtils.encryptWithSha256;

@Repository
@RequiredArgsConstructor
public class AccessTokenRedisRepository {
    private final RedisHelper redisHelper;

    public void addToBlacklist(String token, Long ttlSeconds) {
        redisHelper.setString(generateKey(token), token, Duration.ofSeconds(ttlSeconds));
    }

    public boolean isBlacklisted(String token) {
        return redisHelper.exists(generateKey(token));
    }

    public void removeFromBlacklist(String token) {
        redisHelper.delete(generateKey(token));
    }

    private String generateKey(String token) {
        return generateRedisKeyWithEqualCase(BLACKLIST_ACCESS_TOKEN_PREFIX, encryptWithSha256(token));
    }
}
