package kr.modusplant.domains.post.framework.out.redis;

import kr.modusplant.domains.post.usecase.port.repository.PostSearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class PostSearchHistoryRedisRepository implements PostSearchHistoryRepository {
    private static final String KEY_FORMAT = "searchHistory:member:%s";
    private static final int MAX_HISTORY = 20;
    private static final long TTL_DAYS = 30;

    private final StringRedisTemplate stringRedisTemplate;

    public void saveSearchKeyword(UUID memberUuid, String keyword) {
        if (memberUuid == null || !StringUtils.hasText(keyword)) {
            return;
        }

        double score = System.currentTimeMillis();
        String key = generatedKey(memberUuid);
        ZSetOperations<String, String> zSet = stringRedisTemplate.opsForZSet();

        // 동일 검색어 있으면 score(시간)만 갱신됨
        zSet.add(key, keyword.trim(), score);

        // 20개 초과 시 가장 오래된 항목 제거
        Long size = zSet.zCard(key);
        if (size != null && size > MAX_HISTORY) {
            zSet.removeRange(key, 0, size - MAX_HISTORY - 1);
        }

        // TTL 30일 갱신
        stringRedisTemplate.expire(key, TTL_DAYS, TimeUnit.DAYS);
    }

    public List<String> getSearchHistory(UUID memberUuid, int size) {
        String key = generatedKey(memberUuid);
        ZSetOperations<String, String> zSet = stringRedisTemplate.opsForZSet();

        // 30일 지난 검색어 제거
        double expiredScore = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(TTL_DAYS);
        zSet.removeRangeByScore(key, 0, expiredScore);

        // 최신순으로 반환
        Set<String> keywords = zSet.reverseRange(generatedKey(memberUuid), 0, size-1);
        return keywords != null ? new ArrayList<>(keywords) : Collections.emptyList();
    }

    public void removeSearchKeyword(UUID memberUuid, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        stringRedisTemplate.opsForZSet().remove(generatedKey(memberUuid), keyword);
    }

    public void removeAllSearchHistory(UUID memberUuid) {
        stringRedisTemplate.delete(generatedKey(memberUuid));
    }

    private String generatedKey(UUID memberUuid) {
        return KEY_FORMAT.formatted(memberUuid);
    }

}
