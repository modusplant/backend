package kr.modusplant.framework.outbound.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisHelper {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    /** ===== [String 값 저장 ] ===== */
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void setString(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }

    @SuppressWarnings("OptionalOfNullableMisuse")
    public Optional<String> getString(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    /** ===== [Object 값 저장] ===== */
    public void setObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setObject(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @SuppressWarnings("OptionalOfNullableMisuse")
    public <T> Optional<T> getObject(String key, Class<T> clazz) {
        return Optional.ofNullable(clazz.cast(redisTemplate.opsForValue().get(key)));
    }

    /** ===== [공통] ===== */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void expire(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
    }

    public Optional<Duration> getTTL(String key) {
        long expire = redisTemplate.getExpire(key);

        if (expire == -1) {
            int MAX_DURATION_SECONDS = 999_999_999;
            return Optional.of(Duration.ofSeconds(MAX_DURATION_SECONDS));
        }

        return expire >= 0
                ? Optional.of(Duration.ofSeconds(expire))
                : Optional.empty();
    }
}
