package kr.modusplant.domains.notification.framework.inbound.web.listener;

import kr.modusplant.domains.notification.adapter.controller.NotificationController;
import kr.modusplant.shared.event.CommentLikeNotificationEvent;
import kr.modusplant.shared.event.CommentNotificationEvent;
import kr.modusplant.shared.event.PostLikeNotificationEvent;
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
public class NotificationEventListener {

    private final NotificationController notificationController;
    private final Semaphore notificationSemaphore;

    @Value("${app.semaphore.datasource.bulkhead.notification.timeout-ms}")
    private long timeoutMs;

    public NotificationEventListener(NotificationController notificationController,
                                     @Qualifier("notificationSemaphore") Semaphore notificationSemaphore) {
        this.notificationController = notificationController;
        this.notificationSemaphore = notificationSemaphore;
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostLikeNotification(PostLikeNotificationEvent event) {
        acquireAndProcess(
                () -> notificationController.createPostLikeNotification(event),
                "[Notification] 게시글 좋아요 알림 실패 - actorId={}, postUlid={}",
                event.getActorId(), event.getPostUlid()
        );
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentLikeNotification(CommentLikeNotificationEvent event) {
        acquireAndProcess(
                () -> notificationController.createCommentLikeNotification(event),
                "[Notification] 댓글 좋아요 알림 실패 - actorId={}, postUlid={}, commentPath={}",
                event.getActorId(), event.getPostUlid(), event.getCommentPath()
        );
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentNotification(CommentNotificationEvent event) {
        acquireAndProcess(
                () -> notificationController.createCommentNotification(event),
                "[Notification] 댓글 추가 알림 실패 - actorId={}, postUlid={}, commentPath={}, action={}",
                event.getActorId(), event.getPostUlid(), event.getCommentPath(), event.getAction()
        );
    }

    private void acquireAndProcess(Runnable task, String errorMsg, Object... args) {
        boolean acquired;
        try {
            acquired = notificationSemaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[Notification] 세마포어 대기 중 인터럽트 발생");
            return;
        }

        try {
            if (!acquired) {
                log.warn("[Notification] 벌크헤드 timeout 초과로 알림 스킵 - {}ms 초과", timeoutMs);
                return;
            }
            task.run();
        } catch (Exception e) {
            log.error(errorMsg, args, e);
        } finally {
            if (acquired) {
                notificationSemaphore.release();
            }
        }
    }
}
