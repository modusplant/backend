package kr.modusplant.shared.framework.redis.listener;

import kr.modusplant.domains.member.domain.event.MemberSearchHistoryRemoveEvent;
import kr.modusplant.domains.member.domain.event.PostViewCountRemoveEvent;
import kr.modusplant.domains.member.domain.event.RecentlyViewMemberRemoveEvent;
import kr.modusplant.domains.member.domain.event.RecentlyViewPostRemoveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisEventListener {
    private final StringRedisTemplate stringRedisTemplate;

    @Async("redisExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecentlyViewPostRemove(RecentlyViewPostRemoveEvent event) {
        try {
            byte[][] publishedPostUlidsBytes =
                    Arrays.stream(event.getPostIds())
                            .map(ulid -> ulid.getBytes(StandardCharsets.UTF_8))
                            .toArray(byte[][]::new);

            stringRedisTemplate.execute((RedisCallback<Void>) (RedisConnection connection) -> {
                int MAX_TARGET_KEY_SIZE = 1000;
                List<byte[]> memberKeys = new ArrayList<>(MAX_TARGET_KEY_SIZE);

                ScanOptions options = ScanOptions.scanOptions()
                        .match("recentlyView:member:*:posts")
                        .count(100)
                        .build();

                try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                    while (cursor.hasNext()) {
                        memberKeys.add(cursor.next());
                        if (memberKeys.size() >= MAX_TARGET_KEY_SIZE) {
                            deleteRecentlyViewPostRecordsWithConnection(connection, memberKeys, publishedPostUlidsBytes);
                        }
                    }
                    if (!memberKeys.isEmpty()) {
                        deleteRecentlyViewPostRecordsWithConnection(connection, memberKeys, publishedPostUlidsBytes);
                    }
                }
                return null;
            });
        } catch (Exception e) {
            log.error("[Redis] 최근에 본 게시글 제거 실패 - postIds = {}", event.getPostIds(), e);
        }
    }

    @Async("redisExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecentlyViewMemberRemove(RecentlyViewMemberRemoveEvent event) {
        try {
            stringRedisTemplate.unlink("recentlyView:member:%s:posts".formatted(event.getMemberId()));
        } catch (Exception e) {
            log.error("[Redis] 회원의 최근에 본 게시글 제거 실패 - memberId = {}", event.getMemberId(), e);
        }
    }

    @Async("redisExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemberSearchHistoryRemove(MemberSearchHistoryRemoveEvent event) {
        try {
            stringRedisTemplate.unlink("searchHistory:member:%s".formatted(event.getMemberId()));
        } catch (Exception e) {
            log.error("[Redis] 회원의 검색 기록 제거 실패 - memberId = {}", event.getMemberId(), e);
        }
    }

    @Async("redisExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostViewCountRemove(PostViewCountRemoveEvent event) {
        try {
            stringRedisTemplate.unlink(
                    Arrays.stream(event.getPostIds())
                            .map("viewCount:comm_post:%s:view_count"::formatted)
                            .toList());
        } catch (Exception e) {
            log.error("[Redis] 게시글 조회수 제거 실패 - postIds = {}", event.getPostIds(), e);
        }
    }

    private void deleteRecentlyViewPostRecordsWithConnection(
            RedisConnection connection, List<byte[]> batchKeys, byte[][] matchedValuesBytes) {
        connection.openPipeline();
        for (byte[] rawKey : batchKeys) {
            connection.zSetCommands().zRem(rawKey, matchedValuesBytes);
        }
        connection.closePipeline();
        batchKeys.clear();
    }
}
