package kr.modusplant.domains.member.adapter.listener;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.common.util.domain.event.CommentAbuseReportEventTestUtils;
import kr.modusplant.domains.member.common.util.domain.event.PostAbuseReportEventTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectCommentIdTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectPostIdTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.ReportTimeTestUtils;
import kr.modusplant.domains.member.domain.vo.ReportTime;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_CREATED_AT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MemberEventListenerTest implements PostAbuseReportEventTestUtils, CommentAbuseReportEventTestUtils,
        ActivitySubjectPostIdTestUtils, ActivitySubjectCommentIdTestUtils, ReportTimeTestUtils {
    private static final long TIMEOUT_MS = 1000L;

    private final Semaphore adminSemaphore = Mockito.mock(Semaphore.class);
    private final MemberValidationHelper memberValidationHelper = Mockito.mock(MemberValidationHelper.class);
    private final ReportDashboardRepository reportDashboardRepository = Mockito.mock(ReportDashboardRepository.class);
    private final TransactionTemplate transactionTemplate = Mockito.mock(TransactionTemplate.class);
    private final MemberEventListener memberEventListener = new MemberEventListener(
            adminSemaphore, memberValidationHelper, reportDashboardRepository, transactionTemplate);

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        ReflectionTestUtils.setField(memberEventListener, "timeoutMs", TIMEOUT_MS);
        doAnswer(invocation -> {
            ((Consumer<TransactionStatus>) invocation.getArgument(0)).accept(Mockito.mock(TransactionStatus.class));
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
    }

    @Test
    @DisplayName("게시글 신고 이벤트로 신고 대시보드 반영 시 활동 수행")
    void testReflectReportDashboard_givenPostAbuseReportEvent_willReflectPostAbuseReport() throws InterruptedException {
        // given
        given(adminSemaphore.tryAcquire(TIMEOUT_MS, TimeUnit.MILLISECONDS)).willReturn(true);

        // when
        memberEventListener.reflectReportDashboard(testPostAbuseReportEvent);

        // then
        verify(memberValidationHelper).validateIfActivitySubjectPostExists(testActivitySubjectPostId);
        verify(reportDashboardRepository).reflectPostAbuseReport(testActivitySubjectPostId, testReportTime);
        verify(adminSemaphore).release();
    }

    @Test
    @DisplayName("댓글 신고 이벤트로 신고 대시보드 반영 시 활동 수행")
    void testReflectReportDashboard_givenCommentAbuseReportEvent_willReflectCommentAbuseReport() throws InterruptedException {
        // given
        given(adminSemaphore.tryAcquire(TIMEOUT_MS, TimeUnit.MILLISECONDS)).willReturn(true);

        // when
        memberEventListener.reflectReportDashboard(testCommentAbuseReportEvent);

        // then
        verify(memberValidationHelper).validateIfActivitySubjectCommentExists(testActivitySubjectCommentId);
        verify(reportDashboardRepository).reflectCommentAbuseReport(
                testActivitySubjectCommentId, ReportTime.create(TEST_POST_CREATED_AT));
        verify(adminSemaphore).release();
    }

    @Test
    @DisplayName("세마포어 획득 시간 초과 상태로 신고 대시보드 반영 시 활동 수행")
    void testReflectReportDashboard_givenSemaphoreTimeout_willSkipReflection() throws InterruptedException {
        // given
        given(adminSemaphore.tryAcquire(TIMEOUT_MS, TimeUnit.MILLISECONDS)).willReturn(false);

        // when
        memberEventListener.reflectReportDashboard(testPostAbuseReportEvent);

        // then
        verify(transactionTemplate, never()).executeWithoutResult(any());
        verify(reportDashboardRepository, never()).reflectPostAbuseReport(any(), any());
        verify(adminSemaphore, never()).release();
    }

    @Test
    @DisplayName("검증 실패 상태로 신고 대시보드 반영 시 활동 수행")
    void testReflectReportDashboard_givenValidationFailure_willReleaseSemaphore() throws InterruptedException {
        // given
        given(adminSemaphore.tryAcquire(TIMEOUT_MS, TimeUnit.MILLISECONDS)).willReturn(true);
        doThrow(new RuntimeException()).when(memberValidationHelper)
                .validateIfActivitySubjectPostExists(testActivitySubjectPostId);

        // when
        assertDoesNotThrow(() -> memberEventListener.reflectReportDashboard(testPostAbuseReportEvent));

        // then
        verify(reportDashboardRepository, never()).reflectPostAbuseReport(any(), any());
        verify(adminSemaphore).release();
    }

    @Test
    @DisplayName("인터럽트 발생 상태로 신고 대시보드 반영 시 활동 수행")
    void testReflectReportDashboard_givenInterruption_willRestoreInterruptStatus() throws InterruptedException {
        // given
        given(adminSemaphore.tryAcquire(anyLong(), any(TimeUnit.class))).willThrow(new InterruptedException());

        // when
        memberEventListener.reflectReportDashboard(testPostAbuseReportEvent);

        // then
        assertTrue(Thread.interrupted());
        verify(transactionTemplate, never()).executeWithoutResult(any());
        verify(adminSemaphore, never()).release();
    }
}
