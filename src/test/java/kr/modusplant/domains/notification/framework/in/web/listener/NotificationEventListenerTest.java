package kr.modusplant.domains.notification.framework.in.web.listener;

import kr.modusplant.domains.notification.adapter.controller.NotificationController;
import kr.modusplant.shared.event.common.util.CommentLikeNotificationEventTestUtils;
import kr.modusplant.shared.event.common.util.CommentNotificationEventTestUtils;
import kr.modusplant.shared.event.common.util.PostLikeNotificationEventTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest implements PostLikeNotificationEventTestUtils, CommentLikeNotificationEventTestUtils, CommentNotificationEventTestUtils {
    @Mock
    private NotificationController notificationController;

    @Mock
    private Semaphore notificationSemaphore;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationEventListener, "timeoutMs", 1000L);
    }

    @Test
    @DisplayName("게시글 좋아요 이벤트 수신 시 컨트롤러의 생성 메서드를 호출한다")
    void handlePostLikeNotification_shouldCallController() throws InterruptedException {
        // given
        given(notificationSemaphore.tryAcquire(anyLong(), any(TimeUnit.class))).willReturn(true);

        // when
        notificationEventListener.handlePostLikeNotification(testPostLikeNotificationEvent);

        // then
        verify(notificationController, times(1)).createPostLikeNotification(testPostLikeNotificationEvent);
        verify(notificationSemaphore, times(1)).release();
    }

    @Test
    @DisplayName("댓글 좋아요 이벤트 수신 시 컨트롤러의 생성 메서드를 호출한다")
    void handleCommentLikeNotification_shouldCallController() throws InterruptedException {
        // given
        given(notificationSemaphore.tryAcquire(anyLong(), any(TimeUnit.class))).willReturn(true);

        // when
        notificationEventListener.handleCommentLikeNotification(testCommentLikeNotificationEvent);

        // then
        verify(notificationController, times(1)).createCommentLikeNotification(testCommentLikeNotificationEvent);
        verify(notificationSemaphore, times(1)).release();
    }

    @Test
    @DisplayName("댓글 추가 이벤트 수신 시 컨트롤러의 생성 메서드를 호출한다")
    void handleCommentNotification_shouldCallController() throws InterruptedException {
        // given
        given(notificationSemaphore.tryAcquire(anyLong(), any(TimeUnit.class))).willReturn(true);

        // when
        notificationEventListener.handleCommentNotification(testCommentNotificationEvent);

        // then
        verify(notificationController, times(1)).createCommentNotification(testCommentNotificationEvent);
        verify(notificationSemaphore, times(1)).release();
    }

    @Test
    @DisplayName("세마포어 획득에 실패(Bulkhead 작동)하면 컨트롤러 메서드를 호출하지 않고 스킵한다")
    void handlePostLikeNotification_whenSemaphoreFull_shouldSkip() throws InterruptedException {
        // given
        given(notificationSemaphore.tryAcquire(anyLong(), any(TimeUnit.class))).willReturn(false);

        // when
        notificationEventListener.handlePostLikeNotification(testPostLikeNotificationEvent);

        // then
        verify(notificationController, never()).createPostLikeNotification(any());
        verify(notificationSemaphore, never()).release();
    }

    @Test
    @DisplayName("컨트롤러 호출 중 예외가 발생해도 세마포어는 반드시 해제된다")
    void handlePostLikeNotification_whenException_shouldStillRelease() throws InterruptedException {
        // given
        given(notificationSemaphore.tryAcquire(anyLong(), any(TimeUnit.class))).willReturn(true);
        doThrow(new RuntimeException("Fail")).when(notificationController).createPostLikeNotification(any());

        // when
        assertDoesNotThrow(() -> notificationEventListener.handlePostLikeNotification(testPostLikeNotificationEvent));

        // then
        verify(notificationController, times(1)).createPostLikeNotification(any());
        verify(notificationSemaphore, times(1)).release();
    }

    @Test
    @DisplayName("세마포어 대기 중 인터럽트 발생 시 로직을 중단한다")
    void handlePostLikeNotification_whenInterrupted_shouldStop() throws InterruptedException {
        // given
        given(notificationSemaphore.tryAcquire(anyLong(), any(TimeUnit.class))).willThrow(new InterruptedException());

        // when
        notificationEventListener.handlePostLikeNotification(testPostLikeNotificationEvent);

        // then
        verify(notificationController, never()).createPostLikeNotification(any());
    }
}