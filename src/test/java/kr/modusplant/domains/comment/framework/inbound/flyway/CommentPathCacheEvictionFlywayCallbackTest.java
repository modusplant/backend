package kr.modusplant.domains.comment.framework.inbound.flyway;

import kr.modusplant.domains.comment.usecase.port.repository.CommentCacheRepository;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class CommentPathCacheEvictionFlywayCallbackTest {
    private final CommentCacheRepository cacheRepository = Mockito.mock(CommentCacheRepository.class);
    private final Context context = Mockito.mock(Context.class);
    private final MigrationInfo migrationInfo = Mockito.mock(MigrationInfo.class);

    private final CommentPathCacheEvictionFlywayCallback callback =
            new CommentPathCacheEvictionFlywayCallback(cacheRepository);

    private static final MigrationVersion COMMENT_PATH_MIGRATION_VERSION = MigrationVersion.fromVersion("5.2.3");
    private static final MigrationVersion OTHER_MIGRATION_VERSION = MigrationVersion.fromVersion("5.2.2");

    @Test
    @DisplayName("마이그레이션 단위 종료 이벤트이면 true 반환")
    void testSupports_givenAfterEachMigrateEvent_willReturnTrue() {
        // when
        boolean result = callback.supports(Event.AFTER_EACH_MIGRATE, context);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("마이그레이션 성공 종료 이벤트이면 true 반환")
    void testSupports_givenAfterMigrateEvent_willReturnTrue() {
        // when
        boolean result = callback.supports(Event.AFTER_MIGRATE, context);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("마이그레이션 실패 종료 이벤트이면 true 반환")
    void testSupports_givenAfterMigrateErrorEvent_willReturnTrue() {
        // when
        boolean result = callback.supports(Event.AFTER_MIGRATE_ERROR, context);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("마이그레이션 종료와 무관한 이벤트이면 false 반환")
    void testSupports_givenUnrelatedEvent_willReturnFalse() {
        // when
        boolean result = callback.supports(Event.BEFORE_MIGRATE, context);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("트랜잭션 내 처리 가능 여부 조회 시 true 반환")
    void testCanHandleInTransaction_givenAnyEvent_willReturnTrue() {
        // when
        boolean result = callback.canHandleInTransaction(Event.AFTER_EACH_MIGRATE, context);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("댓글 경로 마이그레이션 적용 후 마이그레이션 성공 종료 시 경로 캐시 만료")
    void testHandle_givenCommentPathMigrationAppliedAndMigrateSucceeded_willEvictAllPathCaches() {
        // given
        given(context.getMigrationInfo()).willReturn(migrationInfo);
        given(migrationInfo.getVersion()).willReturn(COMMENT_PATH_MIGRATION_VERSION);

        // when
        callback.handle(Event.AFTER_EACH_MIGRATE, context);
        callback.handle(Event.AFTER_MIGRATE, context);

        // then
        then(cacheRepository).should(times(1)).evictAllPathCaches();
    }

    @Test
    @DisplayName("댓글 경로 마이그레이션 적용 후 마이그레이션 실패 종료 시 경로 캐시 만료")
    void testHandle_givenCommentPathMigrationAppliedAndMigrateFailed_willEvictAllPathCaches() {
        // given
        given(context.getMigrationInfo()).willReturn(migrationInfo);
        given(migrationInfo.getVersion()).willReturn(COMMENT_PATH_MIGRATION_VERSION);

        // when
        callback.handle(Event.AFTER_EACH_MIGRATE, context);
        callback.handle(Event.AFTER_MIGRATE_ERROR, context);

        // then
        then(cacheRepository).should(times(1)).evictAllPathCaches();
    }

    @Test
    @DisplayName("마이그레이션 단위 종료 시점에는 경로 캐시를 만료시키지 않음")
    void testHandle_givenOnlyAfterEachMigrateEvent_willSkipEviction() {
        // given
        given(context.getMigrationInfo()).willReturn(migrationInfo);
        given(migrationInfo.getVersion()).willReturn(COMMENT_PATH_MIGRATION_VERSION);

        // when
        callback.handle(Event.AFTER_EACH_MIGRATE, context);

        // then
        then(cacheRepository).should(never()).evictAllPathCaches();
    }

    @Test
    @DisplayName("다른 버전의 마이그레이션만 적용되면 경로 캐시를 만료시키지 않음")
    void testHandle_givenOtherMigrationApplied_willSkipEviction() {
        // given
        given(context.getMigrationInfo()).willReturn(migrationInfo);
        given(migrationInfo.getVersion()).willReturn(OTHER_MIGRATION_VERSION);

        // when
        callback.handle(Event.AFTER_EACH_MIGRATE, context);
        callback.handle(Event.AFTER_MIGRATE, context);

        // then
        then(cacheRepository).should(never()).evictAllPathCaches();
    }

    @Test
    @DisplayName("마이그레이션 정보가 없으면 경로 캐시를 만료시키지 않음")
    void testHandle_givenNullMigrationInfo_willSkipEviction() {
        // given
        given(context.getMigrationInfo()).willReturn(null);

        // when
        callback.handle(Event.AFTER_EACH_MIGRATE, context);
        callback.handle(Event.AFTER_MIGRATE, context);

        // then
        then(cacheRepository).should(never()).evictAllPathCaches();
    }

    @Test
    @DisplayName("경로 캐시 만료 후 다시 마이그레이션이 종료되면 중복 만료하지 않음")
    void testHandle_givenMigrateFinishedTwiceAfterCommentPathMigration_willEvictAllPathCachesOnce() {
        // given
        given(context.getMigrationInfo()).willReturn(migrationInfo);
        given(migrationInfo.getVersion()).willReturn(COMMENT_PATH_MIGRATION_VERSION);

        // when
        callback.handle(Event.AFTER_EACH_MIGRATE, context);
        callback.handle(Event.AFTER_MIGRATE, context);
        callback.handle(Event.AFTER_MIGRATE, context);

        // then
        then(cacheRepository).should(times(1)).evictAllPathCaches();
    }

    @Test
    @DisplayName("콜백 이름 조회 시 클래스 이름 String 반환")
    void testGetCallbackName_givenCallback_willReturnString() {
        // when
        String result = callback.getCallbackName();

        // then
        assertThat(result).isEqualTo("CommentPathCacheEvictionFlywayCallback");
    }
}
