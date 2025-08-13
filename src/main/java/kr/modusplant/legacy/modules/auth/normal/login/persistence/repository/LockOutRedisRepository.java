package kr.modusplant.legacy.modules.auth.normal.login.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

import static kr.modusplant.global.common.util.EncryptUtils.encryptWithSha256;

@Repository
@RequiredArgsConstructor
public class LockOutRedisRepository {

    // lockOut:email:{sha256 암호화한 email}
    private static final String KEY_FORMAT = "lockOut:email:%s";
    private final StringRedisTemplate stringRedisTemplate;

    public int getFailedAttempts(String email) {
        String result = stringRedisTemplate.opsForValue().get(generateKey(email));
        return result == null ? 0 : Integer.parseInt(result);
    }

    public int increaseFailedAttempt(String email, Duration ttl) {
        String key = generateKey(email);
        Long result = stringRedisTemplate.opsForValue().increment(key);

        if (result == 1L) {
            stringRedisTemplate.expire(key, ttl);
        }
        return result.intValue();
    }

    public void removeFailedAttempt(String email) {
        stringRedisTemplate.delete(generateKey(email));
    }

    private String generateKey(String email) {
        return KEY_FORMAT.formatted(encryptWithSha256(email));
    }
}
