package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.event.ImageRemoveEvent;
import kr.modusplant.shared.event.RecentlyViewPostRemoveEvent;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;

import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static kr.modusplant.jooq.Tables.COMM_POST;
import static kr.modusplant.shared.event.common.util.MemberWithdrawalEventTestUtils.testMemberWithdrawalEvent;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

class MemberEventListenerTest {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

    /**
     * 단순화된 jOOQ Mocking 헬퍼 메서드
     * @param hasData = true일 경우 정해진 더미 데이터 반환, false일 경우 빈 결과 반환
     */
    private DSLContext createSimpleMockDsl(boolean hasData) {
        MockDataProvider mockDataProvider = ctx -> {
            String sql = ctx.sql().toLowerCase();
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);

            // 데이터가 없을 경우 모든 조회 / 실행 쿼리에 대해 빈 결과 반환
            if (!hasData) {
                return new MockResult[]{new MockResult(0, dsl.newResult())};
            }

            if (sql.contains("select")) {
                if (sql.contains("content")) {
                    // CONTENT 조회 쿼리인 경우 이미지가 포함된 고정된 JSONB 1건 반환
                    Result<Record1<JSONB>> result = dsl.newResult(COMM_POST.CONTENT);
                    result.add(dsl.newRecord(COMM_POST.CONTENT).values(
                            JSONB.valueOf("[{\"src\": \"%s\"}]".formatted(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1))));
                    return new MockResult[]{new MockResult(1, result)};
                } else {
                    // ULID 조회 쿼리인 경우 고정된 1건 반환
                    Result<Record1<String>> result = dsl.newResult(COMM_POST.ULID);
                    result.add(dsl.newRecord(COMM_POST.ULID).values(TEST_COMM_POST_ULID));
                    return new MockResult[]{new MockResult(1, result)};
                }
            } else {
                // 그 외 Insert, Update, Delete Batch 쿼리는 성공(1)으로 처리
                return new MockResult[]{new MockResult(1, dsl.newResult())};
            }
        };

        MockConnection mockConnection = new MockConnection(mockDataProvider);
        return DSL.using(mockConnection, SQLDialect.POSTGRES);
    }

    @Test
    @DisplayName("발행된 게시글이 있는 경우, 이미지 및 최근 본 게시글 삭제 이벤트를 발행")
    void testHandleMemberWithdrawalEvent_givenPublishedPostsExist_willPublishEvents() {
        // given
        DSLContext mockDsl = createSimpleMockDsl(true);
        MemberEventListener memberEventListener = new MemberEventListener(stringRedisTemplate, mockDsl, eventPublisher);
        willDoNothing().given(eventPublisher).publishEvent(any(ImageRemoveEvent.class));
        willDoNothing().given(eventPublisher).publishEvent(any(RecentlyViewPostRemoveEvent.class));

        // when
        memberEventListener.handleMemberWithdrawalEvent(testMemberWithdrawalEvent);

        // then
        verify(stringRedisTemplate).unlink("recentlyView:member:%s:posts".formatted(MEMBER_BASIC_USER_UUID));
        verify(eventPublisher, times(1)).publishEvent(any(ImageRemoveEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(RecentlyViewPostRemoveEvent.class));
    }

    @Test
    @DisplayName("발행된 게시글이 없는 경우, 게시글 관련 삭제 이벤트를 발행하지 않음")
    void testHandleMemberWithdrawalEvent_givenNoPublishedPosts_willSkipPostEvents() {
        // given
        DSLContext mockDsl = createSimpleMockDsl(false);
        MemberEventListener memberEventListener = new MemberEventListener(stringRedisTemplate, mockDsl, eventPublisher);

        // when
        memberEventListener.handleMemberWithdrawalEvent(testMemberWithdrawalEvent);

        // then
        verify(stringRedisTemplate).unlink("recentlyView:member:%s:posts".formatted(MEMBER_BASIC_USER_UUID));
        verify(eventPublisher, never()).publishEvent(any(ImageRemoveEvent.class));
        verify(eventPublisher, never()).publishEvent(any(RecentlyViewPostRemoveEvent.class));
    }
}