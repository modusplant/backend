package kr.modusplant.domains.notification.framework.out.jpa.mapper;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.framework.out.jpa.entity.NotificationEntity;
import kr.modusplant.domains.notification.framework.out.jpa.entity.common.util.NotificationEntityTestUtils;
import kr.modusplant.domains.notification.framework.out.jpa.mapper.supers.NotificationJpaMapper;
import kr.modusplant.shared.enums.NotificationActionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationJpaMapperImplTest implements NotificationEntityTestUtils, NotificationTestUtils {
    private final NotificationJpaMapper notificationJpaMapper = new NotificationJpaMapperImpl();

    @Test
    @DisplayName("toNotificationEntity으로 게시글 NotificationEntity 반환하기")
    void testToNotificationEntity_givenPostNotificationAndMemberEntity_willReturnCommNotificationEntity() {
        // given
        Notification notification = createPostLikedUnreadNotification(LocalDateTime.now());
        MemberEntity memberEntity = createMemberBasicAdminEntityWithUuid();

        // when
        NotificationEntity result = notificationJpaMapper.toNotificationEntity(notification, memberEntity);

        // then
        assertEquals(result.getRecipient(), memberEntity);
        assertEquals(result.getActorId(), notification.getActor().getId());
        assertEquals(result.getActorNickname(), notification.getActor().getNickname());
        assertEquals(result.getAction(), notification.getAction().getAction());
        assertEquals(result.getStatus(), notification.getStatus().getStatus());
        assertEquals(result.getPostUlid(), notification.getPostId().getValue());
        assertNull(result.getCommentPath());
        assertEquals(result.getContentPreview(), notification.getContentPreview().getContent());
        assertEquals(result.getCreatedAt(), notification.getCreatedAt());
    }

    @Test
    @DisplayName("toNotificationEntity으로 댓글 NotificationEntity 반환하기")
    void testToNotificationEntity_givenCommentNotificationAndMemberEntity_willReturnCommNotificationEntity() {
        // given
        Notification notification = createCommentLikedUnreadNotification(LocalDateTime.now());
        MemberEntity memberEntity = createMemberBasicAdminEntityWithUuid();

        // when
        NotificationEntity result = notificationJpaMapper.toNotificationEntity(notification, memberEntity);

        // then
        assertEquals(result.getRecipient(), memberEntity);
        assertEquals(result.getActorId(), notification.getActor().getId());
        assertEquals(result.getActorNickname(), notification.getActor().getNickname());
        assertEquals(result.getAction(), notification.getAction().getAction());
        assertEquals(result.getStatus(), notification.getStatus().getStatus());
        assertEquals(result.getPostUlid(), notification.getPostId().getValue());
        assertNotNull(result.getCommentPath());
        assertEquals(result.getCommentPath(), notification.getCommentPath().getPath());
        assertEquals(result.getContentPreview(), notification.getContentPreview().getContent());
        assertEquals(result.getCreatedAt(), notification.getCreatedAt());
    }

    @Test
    @DisplayName("toNotification으로 게시글 Notification 반환하기")
    void testToNotification_givenPostCommNotificationEntity_willReturnNotification() {
        // given
        MemberEntity memberEntity = createMemberBasicAdminEntityWithUuid();
        NotificationEntity notificationEntity = createPostNotificationEntityBuilderWithUlid().recipient(memberEntity).build();

        // when
        Notification result = notificationJpaMapper.toNotification(notificationEntity);

        // then
        assertEquals(result.getNotificationId().getValue(), notificationEntity.getUlid());
        assertEquals(result.getRecipientId().getValue(), notificationEntity.getRecipient().getUuid());
        assertEquals(result.getActor().getId(), notificationEntity.getActorId());
        assertEquals(result.getActor().getNickname(), notificationEntity.getActorNickname());
        assertEquals(result.getAction().getAction(), notificationEntity.getAction());
        assertEquals(result.getStatus().getStatus(), notificationEntity.getStatus());
        assertEquals(result.getPostId().getValue(), notificationEntity.getPostUlid());
        assertNull(result.getCommentPath());
        assertEquals(result.getContentPreview().getContent(), notificationEntity.getContentPreview());
        assertEquals(result.getCreatedAt(), notificationEntity.getCreatedAt());
    }

    @Test
    @DisplayName("toNotification으로 댓글 Notification 반환하기")
    void testToNotification_givenCommentCommNotificationEntity_willReturnNotification() {
        // given
        MemberEntity memberEntity = createMemberBasicAdminEntityWithUuid();
        NotificationEntity notificationEntity = createCommentNotificationEntityBuilderWithUlid(NotificationActionType.COMMENT_ADDED)
                .recipient(memberEntity).build();

        // when
        Notification result = notificationJpaMapper.toNotification(notificationEntity);

        // then
        assertEquals(result.getNotificationId().getValue(), notificationEntity.getUlid());
        assertEquals(result.getRecipientId().getValue(), notificationEntity.getRecipient().getUuid());
        assertEquals(result.getActor().getId(), notificationEntity.getActorId());
        assertEquals(result.getActor().getNickname(), notificationEntity.getActorNickname());
        assertEquals(result.getAction().getAction(), notificationEntity.getAction());
        assertEquals(result.getStatus().getStatus(), notificationEntity.getStatus());
        assertEquals(result.getPostId().getValue(), notificationEntity.getPostUlid());
        assertNotNull(result.getCommentPath());
        assertEquals(result.getCommentPath().getPath(), notificationEntity.getCommentPath());
        assertEquals(result.getContentPreview().getContent(), notificationEntity.getContentPreview());
        assertEquals(result.getCreatedAt(), notificationEntity.getCreatedAt());
    }


}