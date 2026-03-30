package kr.modusplant.domains.notification.adapter.controller;

import kr.modusplant.domains.notification.adapter.mapper.NotificationMapperImpl;
import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.common.util.usecase.record.NotificationReadModelTestUtils;
import kr.modusplant.domains.notification.common.util.usecase.response.NotificationResponseTestUtils;
import kr.modusplant.domains.notification.usecase.port.mapper.NotificationMapper;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationQueryRepository;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationRepository;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.CursorPageResponse;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class NotificationControllerTest implements NotificationTestUtils, NotificationReadModelTestUtils, NotificationResponseTestUtils {
    private final NotificationMapper notificationMapper = new NotificationMapperImpl();
    private final NotificationRepository notificationRepository = Mockito.mock(NotificationRepository.class);
    private final NotificationQueryRepository notificationQueryRepository = Mockito.mock(NotificationQueryRepository.class);
    private final NotificationController notificationController = new NotificationController(notificationMapper, notificationRepository, notificationQueryRepository);

    @Test
    @DisplayName("알림 목록 조회")
    void testGetNotifications_givenStatusAndCursor_willReturnCursorPageResponse() {
        // given
        int size = 10;
        List<NotificationReadModel> readModels = List.of(TEST_POST_LIKED_READ_NOTIFICATION_UNREAD_MODEL);
        given(notificationQueryRepository.findByStatusWithCursor(testNotificationStatusUnread.getStatus(),testRecipientId.getValue(),TEST_NOTIFICATION_ULID,size)).willReturn(readModels);

        // when
        CursorPageResponse<NotificationResponse> result = notificationController.getNotifications(testNotificationStatusUnread.getStatus(), testRecipientId.getValue(), TEST_NOTIFICATION_ULID, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.notifications()).hasSize(1);
        assertThat(result.notifications().get(0).ulid()).isEqualTo(TEST_POST_LIKED_READ_NOTIFICATION_UNREAD_MODEL.ulid());
        assertThat(result.nextUlid()).isNull();
        assertThat(result.hasNext()).isFalse();

        verify(notificationQueryRepository).findByStatusWithCursor(testNotificationStatusUnread.getStatus(),testRecipientId.getValue(),TEST_NOTIFICATION_ULID,size);
    }

    @Test
    @DisplayName("알림 단건 읽음 처리")
    void testReadNotification_givenNotificationIdAndCurrentMemberUuid_willCallMarkAsRead() {

        // when
        notificationController.readNotification(testNotificationId.getValue(), testRecipientId.getValue());

        // then
        verify(notificationRepository).markAsRead(testNotificationId,testRecipientId);
    }

    @Test
    @DisplayName("알림 전체 읽음 처리")
    void testReadAllNotifications_givenCurrentMemberUuid_willCallMarkAllAsRead() {
        // when
        notificationController.readAllNotifications(testRecipientId.getValue());

        // then
        verify(notificationRepository).markAllAsRead(testRecipientId);
    }

    @Test
    @DisplayName("읽지 않은 알림 개수 조회")
    void testCountUnreadNotifications_givenCurrentMemberUuid_willReturnCount() {
        // given
        Long expectedCount = 7L;
        given(notificationRepository.countByRecipientIdAndStatus(testRecipientId, testNotificationStatusUnread)).willReturn(expectedCount);

        // when
        Long result = notificationController.countUnreadNotifications(testRecipientId.getValue());

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(notificationRepository).countByRecipientIdAndStatus(testRecipientId, testNotificationStatusUnread);
    }
}