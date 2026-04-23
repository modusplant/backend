package kr.modusplant.domains.notification.framework.in.web.listener;

import kr.modusplant.domains.notification.adapter.controller.NotificationController;
import kr.modusplant.shared.event.CommentLikeNotificationEvent;
import kr.modusplant.shared.event.CommentNotificationEvent;
import kr.modusplant.shared.event.PostLikeNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationController notificationController;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostLikeNotification(PostLikeNotificationEvent event) {
        try {
            notificationController.createPostLikeNotification(event);
        } catch (Exception e) {
            // 가상스레드 내에서는 커스텀 예외 전파 불가
            log.error("[Notification] 게시글 좋아요 알림 실패 - actorId={}, postUlid={}, error={}",
                    event.getActorId(), event.getPostUlid(), e.getMessage(), e);
        }
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentLikeNotification(CommentLikeNotificationEvent event) {
        try {
            notificationController.createCommentLikeNotification(event);
        } catch (Exception e) {
            log.error("[Notification] 댓글 좋아요 알림 실패 - actorId={}, postUlid={}, commentPath={}, error={}",
                    event.getActorId(), event.getPostUlid(), event.getCommentPath(), e.getMessage(), e);
        }
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentNotification(CommentNotificationEvent event) {
        try {
            notificationController.createCommentNotification(event);
        } catch (Exception e) {
            log.error("[Notification] 댓글 추가 알림 실패 - actorId={}, postUlid={}, commentPath={}, action={}, error={}",
                    event.getActorId(), event.getPostUlid(), event.getCommentPath(), event.getAction(), e.getMessage(), e);
        }
    }
}
