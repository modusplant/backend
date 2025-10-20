package kr.modusplant.legacy.domains.communication.persistence.repository;

import kr.modusplant.legacy.domains.common.error.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CommPostViewCountRedisRepository {
    // viewCount:comm_post:{ulid}:view_count
    private static final String KEY_FORMAT = "viewCount:comm_post:%s:view_count";

    private final StringRedisTemplate stringRedisTemplate;

    @SuppressWarnings({"ConstantValue", "UnreachableCode"})
    public Long read(String ulid) {
        String result = stringRedisTemplate.opsForValue().get(generatedKey(ulid));
        return result == null ? null : Long.parseLong(result);
    }

    public Long increase(String ulid) {
        return stringRedisTemplate.opsForValue().increment(generatedKey(ulid));
    }

    public void write(String ulid, Long viewCount) {
        stringRedisTemplate.opsForValue().set(generatedKey(ulid), String.valueOf(viewCount));
    }

    public Map<String, Long> findAll() {
        Set<String> keys = stringRedisTemplate.keys("viewCount:comm_post:*:view_count");
        Map<String, Long> result = new HashMap<>();
        for (String key : keys) {
            String ulid = extractUlidFromKey(key);
            Long count = Long.valueOf(stringRedisTemplate.opsForValue().get(key));
            result.put(ulid, count);
        }
        return result;
    }

    private String generatedKey(String ulid) {
        return KEY_FORMAT.formatted(ulid);
    }

    private String extractUlidFromKey(String key) {
        String[] parts = key.split(":");
        if (parts.length == 4) {
            return parts[2];
        }
        throw new InvalidFormatException("redisKey");
    }
}
