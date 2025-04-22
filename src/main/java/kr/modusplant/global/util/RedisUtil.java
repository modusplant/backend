package kr.modusplant.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    /** ===== [String 값 저장 ] ===== */
    public void setString(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }

    public Optional<String> getString(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    /** ===== [Object 값 저장] ===== */
    public void setObject(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public <T> Optional<T> getObject(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null)
            return Optional.empty();
        return Optional.of(clazz.cast(obj));
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
        Long expire = redisTemplate.getExpire(key);
        return expire != null && expire >= 0
                ? Optional.of(Duration.ofSeconds(expire))
                : Optional.empty();
    }
}
