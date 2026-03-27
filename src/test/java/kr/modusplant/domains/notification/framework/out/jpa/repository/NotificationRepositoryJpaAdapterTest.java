package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import kr.modusplant.domains.notification.domain.vo.NotificationId;
import kr.modusplant.domains.notification.domain.vo.NotificationStatus;
import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.NotificationJpaRepository;
import kr.modusplant.framework.jpa.entity.CommNotificationEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.enums.NotificationStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_RECIPIENT_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NotificationRepositoryJpaAdapterTest {
    private final NotificationJpaRepository notificationJpaRepository = Mockito.mock(NotificationJpaRepository.class);
    private final SiteMemberJpaRepository siteMemberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final NotificationRepositoryJpaAdapter notificationRepositoryJpaAdapter = new NotificationRepositoryJpaAdapter(notificationJpaRepository, siteMemberJpaRepository);

    @Test
    @DisplayName("알림 단건 읽음 처리하기")
    void testMarkAsRead_givenNotificationIdAndRecipientId_willMarkAsRead() {
        // given
        NotificationId notificationId = NotificationId.create(TEST_NOTIFICATION_ULID);
        RecipientId recipientId = RecipientId.fromUuid(TEST_NOTIFICATION_RECIPIENT_ID);

        SiteMemberEntity recipientEntity = Mockito.mock(SiteMemberEntity.class);
        CommNotificationEntity notificationEntity = Mockito.mock(CommNotificationEntity.class);

        given(siteMemberJpaRepository.findByUuid(TEST_NOTIFICATION_RECIPIENT_ID)).willReturn(Optional.of(recipientEntity));
        given(notificationJpaRepository.findByUlidAndRecipient(TEST_NOTIFICATION_ULID, recipientEntity)).willReturn(Optional.of(notificationEntity));

        // when
        notificationRepositoryJpaAdapter.markAsRead(notificationId, recipientId);

        // then
        verify(siteMemberJpaRepository, times(1)).findByUuid(TEST_NOTIFICATION_RECIPIENT_ID);
        verify(notificationJpaRepository, times(1)).findByUlidAndRecipient(TEST_NOTIFICATION_ULID, recipientEntity);
        verify(notificationEntity, times(1)).read();
        verify(notificationJpaRepository, times(1)).save(notificationEntity);
    }

    @Test
    @DisplayName("알림 단건 읽음 처리하기 - 알림이 존재하지 않는 경우")
    void testMarkAsRead_givenMissingNotification_willThrowException() {
        // given
        NotificationId notificationId = NotificationId.create(TEST_NOTIFICATION_ULID);
        RecipientId recipientId = RecipientId.fromUuid(TEST_NOTIFICATION_RECIPIENT_ID);

        SiteMemberEntity recipientEntity = Mockito.mock(SiteMemberEntity.class);

        given(siteMemberJpaRepository.findByUuid(TEST_NOTIFICATION_RECIPIENT_ID)).willReturn(Optional.of(recipientEntity));
        given(notificationJpaRepository.findByUlidAndRecipient(TEST_NOTIFICATION_ULID, recipientEntity))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationRepositoryJpaAdapter.markAsRead(notificationId, recipientId)).isInstanceOf(InvalidValueException.class);

        verify(notificationJpaRepository, times(1)).findByUlidAndRecipient(TEST_NOTIFICATION_ULID, recipientEntity);
    }

    @Test
    @DisplayName("알림 전체 읽음 처리하기")
    void testMarkAllAsRead_givenRecipientId_willMarkAllAsRead() {
        // given
        RecipientId recipientId = RecipientId.fromUuid(TEST_NOTIFICATION_RECIPIENT_ID);

        // when
        notificationRepositoryJpaAdapter.markAllAsRead(recipientId);

        // then
        verify(notificationJpaRepository, times(1)).updateUnreadStatus(TEST_NOTIFICATION_RECIPIENT_ID);
    }

    @Test
    @DisplayName("읽지 않은 알림 개수 조회하기")
    void testCountByRecipientIdAndStatus_givenRecipientIdAndNotificationStatus_willReturnCount() {
        // given
        RecipientId recipientId = RecipientId.fromUuid(TEST_NOTIFICATION_RECIPIENT_ID);
        NotificationStatus notificationStatus = NotificationStatus.unread();
        SiteMemberEntity recipientEntity = Mockito.mock(SiteMemberEntity.class);
        long expectedCount = 7L;

        given(siteMemberJpaRepository.findByUuid(TEST_NOTIFICATION_RECIPIENT_ID)).willReturn(Optional.of(recipientEntity));
        given(notificationJpaRepository.countByRecipientAndStatus(recipientEntity, NotificationStatusType.UNREAD)).willReturn(expectedCount);

        // when
        long result = notificationRepositoryJpaAdapter.countByRecipientIdAndStatus(recipientId, notificationStatus);

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(siteMemberJpaRepository, times(1)).findByUuid(TEST_NOTIFICATION_RECIPIENT_ID);
        verify(notificationJpaRepository, times(1)).countByRecipientAndStatus(recipientEntity, NotificationStatusType.UNREAD);

    }

}