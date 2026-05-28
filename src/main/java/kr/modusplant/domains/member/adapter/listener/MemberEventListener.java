package kr.modusplant.domains.member.adapter.listener;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.domain.event.PostAbuseReportEvent;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.ReportTime;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MemberEventListener {
    private final Semaphore adminSemaphore;
    private final MemberValidationHelper memberValidationHelper;
    private final ReportDashboardRepository reportDashboardRepository;

    @Value("${app.semaphore.datasource.bulkhead.admin.timeout-ms}")
    private long timeoutMs;

    public MemberEventListener(@Qualifier("adminSemaphore")Semaphore adminSemaphore,
                               MemberValidationHelper memberValidationHelper,
                               ReportDashboardRepository reportDashboardRepository) {
        this.adminSemaphore = adminSemaphore;
        this.memberValidationHelper = memberValidationHelper;
        this.reportDashboardRepository = reportDashboardRepository;
    }

    @Async("memberDomainExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reflectReportDashboard(PostAbuseReportEvent event) {
        acquireAndProcess(
                () -> {
                    ActivitySubjectPostId postId = ActivitySubjectPostId.create(event.getPostUlid());
                    ReportTime reportTime = ReportTime.create(event.getCreatedAt());
                    memberValidationHelper.validateIfActivitySubjectPostExists(postId);

                    reportDashboardRepository.reflectPostAbuseReport(postId, reportTime);
                },
                "[Member Domain] 게시글 신고 대시보드 삽입 또는 갱신 실패 - postUlid = {}",
                event.getPostUlid()
        );
    }

    private void acquireAndProcess(Runnable task, String errorMsg, Object... args) {
        boolean acquired;
        try {
            acquired = adminSemaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[Member Domain] 세마포어 대기 중 인터럽트 발생");
            return;
        }

        try {
            if (!acquired) {
                log.warn("[Member Domain] 벌크헤드 타임아웃 초과로 회원 도메인 기능 스킵 - {}ms 초과", timeoutMs);
                return;
            }
            task.run();
        } catch (Exception e) {
            log.error(errorMsg, args, e);
        } finally {
            if (acquired) {
                adminSemaphore.release();
            }
        }
    }
}
