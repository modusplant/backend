package kr.modusplant.legacy.modules.jwt.persistence.repository;

import kr.modusplant.global.middleware.redis.RedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;

import static kr.modusplant.shared.util.EncryptUtils.encryptWithSha256;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {
    private final RedisHelper redisHelper;
    private static final String KEY_FORMAT = "blacklist:access_token:%s";

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
        return KEY_FORMAT.formatted(encryptWithSha256(token));
    }

}
