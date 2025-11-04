package kr.modusplant.domains.post.framework.out.redis;

import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.port.repository.PostViewCountRepository;
import kr.modusplant.shared.exception.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PostViewCountRedisRepository implements PostViewCountRepository {
    // viewCount:comm_post:{ulid}:view_count
    private static final String KEY_FORMAT = "viewCount:comm_post:%s:view_count";

    private final StringRedisTemplate stringRedisTemplate;

    @SuppressWarnings({"ConstantValue", "UnreachableCode"})
    public Long read(PostId postId) {
        String result = stringRedisTemplate.opsForValue().get(generatedKey(postId.getValue()));
        return result == null ? null : Long.parseLong(result);
    }

    public Long increase(PostId postId) {
        return stringRedisTemplate.opsForValue().increment(generatedKey(postId.getValue()));
    }

    public void write(PostId postId, Long viewCount) {
        stringRedisTemplate.opsForValue().set(generatedKey(postId.getValue()), String.valueOf(viewCount));
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
