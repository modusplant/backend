package kr.modusplant.domains.post.framework.out.redis;

import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.port.repository.PostRecentlyViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class PostRecentlyViewRedisRepository implements PostRecentlyViewRepository {
    private static final String KEY_FORMAT = "recentlyView:member:%s:posts";
    private static final long TTL_DAYS = 7;          // TTL 30일

    private final StringRedisTemplate stringRedisTemplate;

    // 조회 기록 저장
    public void recordViewPost(UUID memberUuid, PostId postId) {
        if (memberUuid == null) {
            return;
        }
        double score = System.currentTimeMillis() / 1000.0;
        String key = generatedKey(memberUuid);

        // 조회 기록 업데이트
        stringRedisTemplate.opsForZSet().add(key,postId.getValue(),score);

        // TTL 갱신
        stringRedisTemplate.expire(key,TTL_DAYS, TimeUnit.DAYS);
    }

    public List<PostId> getRecentlyViewPostIds(UUID memberUuid, int page, int size) {
        long offset = (long) page * size;

        // reverseRange: 최신 순으로 조회 (score 높은 것부터)
        Set<String> ulids = stringRedisTemplate.opsForZSet()
                .reverseRange(generatedKey(memberUuid),offset,offset + size - 1);

        return ulids != null
                ? ulids.stream().map(PostId::create).toList()
                : List.of();
    }

    public long getTotalRecentlyViewPosts(UUID memberUuid) {
        Long totalCount = stringRedisTemplate.opsForZSet()
                .zCard(generatedKey(memberUuid));
        return totalCount != null ? totalCount : 0;
    }

    public void removeViewPost(UUID memberUuid, PostId postId) {
        stringRedisTemplate.opsForZSet()
                .remove(generatedKey(memberUuid), postId.getValue());
    }

    private String generatedKey(UUID memberUuid) {
        return KEY_FORMAT.formatted(memberUuid);
    }
}
