package kr.modusplant.domains.notification.framework.in.web.listener;

import kr.modusplant.domains.notification.adapter.controller.NotificationController;
import kr.modusplant.shared.event.common.util.CommentLikeNotificationEventTestUtils;
import kr.modusplant.shared.event.common.util.CommentNotificationEventTestUtils;
import kr.modusplant.shared.event.common.util.PostLikeNotificationEventTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest implements PostLikeNotificationEventTestUtils, CommentLikeNotificationEventTestUtils, CommentNotificationEventTestUtils {
    @Mock
    private NotificationController notificationController;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @Test
    @DisplayName("게시글 좋아요 이벤트 수신 시 컨트롤러의 생성 메서드를 호출한다")
    void handlePostLikeNotification_shouldCallController() {
        // when
        notificationEventListener.handlePostLikeNotification(testPostLikeNotificationEvent);

        // then
        verify(notificationController, times(1)).createPostLikeNotification(testPostLikeNotificationEvent);
    }

    @Test
    @DisplayName("댓글 좋아요 이벤트 수신 시 컨트롤러의 생성 메서드를 호출한다")
    void handleCommentLikeNotification_shouldCallController() {
        // when
        notificationEventListener.handleCommentLikeNotification(testCommentLikeNotificationEvent);

        // then
        verify(notificationController, times(1)).createCommentLikeNotification(testCommentLikeNotificationEvent);
    }

    @Test
    @DisplayName("댓글 추가 이벤트 수신 시 컨트롤러의 생성 메서드를 호출한다")
    void handleCommentNotification_shouldCallController() {
        // when
        notificationEventListener.handleCommentNotification(testCommentNotificationEvent);

        // then
        verify(notificationController, times(1)).createCommentNotification(testCommentNotificationEvent);
    }

    @Test
    @DisplayName("컨트롤러 호출 중 예외가 발생해도 이벤트 리스너는 예외를 외부로 전파하지 않고 로그만 남긴다")
    void handlePostLikeNotification_whenException_shouldNotThrow() {
        // 의도적으로 예외 발생 설정
        doThrow(new RuntimeException("DB Error")).when(notificationController).createCommentNotification(any());

        // when & then
        assertDoesNotThrow(() -> notificationEventListener.handleCommentNotification(testCommentReplyNotificationEvent));
        verify(notificationController, times(1)).createCommentNotification(testCommentReplyNotificationEvent);
    }
}