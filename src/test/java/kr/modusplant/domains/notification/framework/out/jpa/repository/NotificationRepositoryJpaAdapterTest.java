package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import kr.modusplant.domains.notification.domain.vo.NotificationId;
import kr.modusplant.domains.notification.domain.vo.NotificationStatus;
import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.domains.notification.framework.out.jpa.entity.NotificationEntity;
import kr.modusplant.domains.notification.framework.out.jpa.mapper.supers.NotificationJpaMapper;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.NotificationJpaRepository;
import kr.modusplant.shared.enums.NotificationStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_RECIPIENT_ID;
import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NotificationRepositoryJpaAdapterTest  implements NotificationTestUtils {
    private final NotificationJpaRepository notificationJpaRepository = Mockito.mock(NotificationJpaRepository.class);
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final NotificationJpaMapper notificationJpaMapper = Mockito.mock(NotificationJpaMapper.class);
    private final NotificationRepositoryJpaAdapter notificationRepositoryJpaAdapter = new NotificationRepositoryJpaAdapter(notificationJpaRepository, memberJpaRepository,notificationJpaMapper);

    @Test
    @DisplayName("알림 단건 읽음 처리하기")
    void testMarkAsRead_givenNotificationIdAndRecipientId_willMarkAsRead() {
        // given
        NotificationId notificationId = NotificationId.create(TEST_NOTIFICATION_ULID);
        RecipientId recipientId = RecipientId.fromUuid(TEST_NOTIFICATION_RECIPIENT_ID);

        given(notificationJpaRepository.updateUnreadStatusById(notificationId.getValue(), recipientId.getValue())).willReturn(1);

        // when
        notificationRepositoryJpaAdapter.markAsRead(notificationId, recipientId);

        // then
        verify(notificationJpaRepository, times(1)).updateUnreadStatusById(TEST_NOTIFICATION_ULID, TEST_NOTIFICATION_RECIPIENT_ID);
    }

    @Test
    @DisplayName("알림 단건 읽음 처리하기 - 알림이 존재하지 않는 경우")
    void testMarkAsRead_givenMissingNotification_willThrowException() {
        // given
        NotificationId notificationId = NotificationId.create(TEST_NOTIFICATION_ULID);
        RecipientId recipientId = RecipientId.fromUuid(TEST_NOTIFICATION_RECIPIENT_ID);

        given(notificationJpaRepository.updateUnreadStatusById(notificationId.getValue(), recipientId.getValue())).willReturn(0);

        // when & then
        assertThatThrownBy(() -> notificationRepositoryJpaAdapter.markAsRead(notificationId, recipientId)).isInstanceOf(InvalidValueException.class);
        verify(notificationJpaRepository, times(1)).updateUnreadStatusById(TEST_NOTIFICATION_ULID, TEST_NOTIFICATION_RECIPIENT_ID);
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
        MemberEntity recipientEntity = Mockito.mock(MemberEntity.class);
        long expectedCount = 7L;

        given(memberJpaRepository.findByUuid(TEST_NOTIFICATION_RECIPIENT_ID)).willReturn(Optional.of(recipientEntity));
        given(notificationJpaRepository.countByRecipientAndStatus(recipientEntity, NotificationStatusType.UNREAD)).willReturn(expectedCount);

        // when
        long result = notificationRepositoryJpaAdapter.countByRecipientIdAndStatus(recipientId, notificationStatus);

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(memberJpaRepository, times(1)).findByUuid(TEST_NOTIFICATION_RECIPIENT_ID);
        verify(notificationJpaRepository, times(1)).countByRecipientAndStatus(recipientEntity, NotificationStatusType.UNREAD);

    }

    @Nested
    @DisplayName("saveWithLimit 테스트")
    class SaveWithLimitTests {

        @Test
        @DisplayName("알림을 저장하고, limit을 초과하지 않으면 삭제 로직이 호출되지 않는다")
        void testSaveWithLimit_whenUnderLimit_willOnlySave() {
            // given
            int limit = 10;
            Notification notification = createPostLikedUnreadNotification(LocalDateTime.now());
            MemberEntity recipientEntity = Mockito.mock(MemberEntity.class);
            NotificationEntity savedEntity = Mockito.mock(NotificationEntity.class);

            given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(recipientEntity));
            given(recipientEntity.getUuid()).willReturn(TEST_NOTIFICATION_RECIPIENT_ID);
            given(notificationJpaMapper.toNotificationEntity(any(), any())).willReturn(savedEntity);
            given(notificationJpaRepository.save(any())).willReturn(savedEntity);
            given(notificationJpaRepository.findUlidsByRecipientId(eq(TEST_NOTIFICATION_RECIPIENT_ID), any(PageRequest.class))).willReturn(Collections.emptyList());

            // when
            notificationRepositoryJpaAdapter.saveWithLimit(notification, limit);

            // then
            verify(notificationJpaRepository, times(1)).save(any());
            verify(notificationJpaRepository, times(0)).deleteByRecipientIdAndUlidBefore(any(), any());
            verify(notificationJpaMapper, times(1)).toNotification(savedEntity);
        }

        @Test
        @DisplayName("알림을 저장하고, limit을 초과하면 기준 ULID 이전의 알림들을 삭제한다")
        void testSaveWithLimit_whenOverLimit_willSaveAndCleanup() {
            // given
            int limit = 5;
            Notification notification = createPostLikedUnreadNotification(LocalDateTime.now());
            MemberEntity recipientEntity = Mockito.mock(MemberEntity.class);
            NotificationEntity savedEntity = Mockito.mock(NotificationEntity.class);
            String cutoffUlid = "01ARZ3NDEKTSV4RRFFQ69G5FAV";

            given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(recipientEntity));
            given(recipientEntity.getUuid()).willReturn(TEST_NOTIFICATION_RECIPIENT_ID);
            given(notificationJpaMapper.toNotificationEntity(any(), any())).willReturn(savedEntity);
            given(notificationJpaRepository.save(any())).willReturn(savedEntity);
            given(notificationJpaRepository.findUlidsByRecipientId(eq(TEST_NOTIFICATION_RECIPIENT_ID), any(PageRequest.class))).willReturn(List.of(cutoffUlid));

            // when
            notificationRepositoryJpaAdapter.saveWithLimit(notification, limit);

            // then
            verify(notificationJpaRepository, times(1)).save(any());
            verify(notificationJpaRepository, times(1)).deleteByRecipientIdAndUlidBefore(TEST_NOTIFICATION_RECIPIENT_ID, cutoffUlid);
        }

        @Test
        @DisplayName("존재하지 않는 수신자 아이디로 저장 시도 시 예외가 발생한다")
        void testSaveWithLimit_givenInvalidRecipient_willThrowException() {
            // given
            Notification notification = createPostLikedUnreadNotification(LocalDateTime.now());
            given(memberJpaRepository.findByUuid(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationRepositoryJpaAdapter.saveWithLimit(notification, 10))
                    .isInstanceOf(InvalidValueException.class);
        }
    }

}