package kr.modusplant.domains.communication.qna.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class QnaPostViewCountRedisRepository {
    // viewCount::qna_post::{ulid}::view_count
    private static final String KEY_FORMAT = "viewCount::qna_post::%s::view_count";

    private final StringRedisTemplate stringRedisTemplate;

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
        Set<String> keys = stringRedisTemplate.keys("viewCount::qna_post::*::view_count");
        Map<String, Long> result = new HashMap<>();
        if (keys != null) {
            for (String key:keys) {
                String value = stringRedisTemplate.opsForValue().get(key);
                if (value == null)
                    continue;
                Long count = Long.valueOf(value);
                String ulid = extractUlidFormKey(key);
                result.put(ulid,count);
            }
        }
        return result;
    }

    private String generatedKey(String ulid) {
        return KEY_FORMAT.formatted(ulid);
    }

    private String extractUlidFormKey(String key) {
        String[] parts = key.split("::");
        if (parts.length >= 3) {
            return parts[2];
        }
        throw new IllegalArgumentException("Invalid Redis key format: " + key);
    }
}
