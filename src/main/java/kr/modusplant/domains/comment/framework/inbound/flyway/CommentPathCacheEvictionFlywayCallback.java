package kr.modusplant.domains.comment.framework.inbound.flyway;

import kr.modusplant.domains.comment.usecase.port.repository.CommentCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 댓글 경로를 일괄 변경하는 Flyway 마이그레이션(V5.2.3)이 적용된 실행에 한해, 마이그레이션 종료 후 댓글 경로가 포함된 모든 캐시를 만료시킨다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CommentPathCacheEvictionFlywayCallback implements Callback {
    private static final MigrationVersion COMMENT_PATH_MIGRATION_VERSION = MigrationVersion.fromVersion("5.2.3");
    // 마이그레이션은 각자의 트랜잭션에서 커밋되므로, 이후 마이그레이션이 실패해도 V5.2.3 적용 결과는 유지되어 만료가 필요함
    private static final Set<Event> MIGRATE_FINISH_EVENTS = Set.of(Event.AFTER_MIGRATE, Event.AFTER_MIGRATE_ERROR);

    private final CommentCacheRepository cacheRepository;

    private boolean isCommentPathMigrationApplied = false;

    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_EACH_MIGRATE || MIGRATE_FINISH_EVENTS.contains(event);
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        if (event == Event.AFTER_EACH_MIGRATE) {
            MigrationInfo migrationInfo = context.getMigrationInfo();
            if (migrationInfo != null && COMMENT_PATH_MIGRATION_VERSION.equals(migrationInfo.getVersion())) {
                isCommentPathMigrationApplied = true;
            }
            return;
        }

        // 트랜잭션 커밋 이후의 마이그레이션 종료 시점에 만료
        if (isCommentPathMigrationApplied) {
            long evictedCount = cacheRepository.evictAllPathCaches();
            isCommentPathMigrationApplied = false;
            log.info("[Flyway] Evicted {} comment path cache keys after migration V{}",
                    evictedCount, COMMENT_PATH_MIGRATION_VERSION);
        }
    }

    @Override
    public String getCallbackName() {
        return getClass().getSimpleName();
    }
}
