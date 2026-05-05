package kr.modusplant.domains.notification.adapter.mapper;

import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.domain.vo.NotificationStatus;
import kr.modusplant.domains.notification.usecase.port.mapper.NotificationMapper;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperImplTest implements NotificationTestUtils {
    private final NotificationMapper notificationMapper = new NotificationMapperImpl();

    @Test
    @DisplayName("toNotificationResponse로 NotificationResponse 반환하기")
    void testToNotificationResponse_givenReadModel_willReturnResponse() {
        // given
        LocalDateTime createdAt = LocalDateTime.now();
        NotificationReadModel readModel = new NotificationReadModel(
                TEST_NOTIFICATION_ULID,
                TEST_NOTIFICATION_RECIPIENT_ID,
                TEST_NOTIFICATION_ACTOR_ID,
                TEST_NOTIFICATION_ACTOR_NICKNAME,
                NotificationActionType.COMMENT_ADDED,
                NotificationStatusType.UNREAD,
                TEST_NOTIFICATION_POST_ULID,
                TEST_NOTIFICATION_COMMENT_PATH_DEPTH3,
                TEST_NOTIFICATION_COMMENT_PREVIEW,
                createdAt
        );

        // when
        NotificationResponse result = notificationMapper.toNotificationResponse(readModel);

        // then
        assertEquals(result.ulid(), readModel.ulid());
        assertEquals(result.actorId(), readModel.actorId());
        assertEquals(result.actorNickname(), readModel.actorNickname());
        assertEquals(result.action(), readModel.action().name());
        assertEquals(result.status(), readModel.status().getValue());
        assertEquals(result.postUlid(), readModel.postUlid());
        assertEquals(result.commentPath(), readModel.commentPath());
        assertEquals(result.contentType(), "comment");
        assertEquals(result.contentPreview(), readModel.contentPreview());
        assertEquals(result.createdAt(), createdAt);
    }

    @Test
    @DisplayName("POST_LIKED인 경우 contentType은 post로 반환하기")
    void testToNotificationResponse_givenPostLikedReadModel_willReturnPostContentType() {
        // given
        LocalDateTime createdAt = LocalDateTime.now();
        NotificationReadModel readModel = new NotificationReadModel(
                TEST_NOTIFICATION_ULID,
                TEST_NOTIFICATION_RECIPIENT_ID,
                TEST_NOTIFICATION_ACTOR_ID,
                TEST_NOTIFICATION_ACTOR_NICKNAME,
                NotificationActionType.POST_LIKED,
                NotificationStatusType.READ,
                TEST_NOTIFICATION_POST_ULID,
                null,
                TEST_NOTIFICATION_POST_PREVIEW,
                createdAt
        );

        // when
        NotificationResponse result = notificationMapper.toNotificationResponse(readModel);

        // then
        assertEquals(result.ulid(), readModel.ulid());
        assertEquals(result.actorId(), readModel.actorId());
        assertEquals(result.actorNickname(), readModel.actorNickname());
        assertEquals(result.action(), readModel.action().name());
        assertEquals(result.status(), readModel.status().getValue());
        assertEquals(result.postUlid(), readModel.postUlid());
        assertEquals(result.commentPath(), null);
        assertEquals(result.contentType(), "post");
        assertEquals(result.contentPreview(), readModel.contentPreview());
        assertEquals(result.createdAt(), createdAt);
    }

    @Test
    @DisplayName("toPostNotification으로 게시글 Notification 변환")
    void testToPostNotification_givenValue_willReturnNotification() {
        // when
        Notification result = notificationMapper.toPostNotification(testRecipientId, testActor, testNotificationActionPostLiked, testPostId, testPostContentPreview);

        // then
        assertEquals(result.getRecipientId(), testRecipientId);
        assertEquals(result.getActor(), testActor);
        assertEquals(result.getAction(), testNotificationActionPostLiked);
        assertEquals(result.getStatus(), NotificationStatus.unread());
        assertEquals(result.getPostId(), testPostId);
        assertNull(result.getCommentPath());
        assertEquals(result.getContentPreview(), testPostContentPreview);
        assertNotNull(result.getCreatedAt());
    }

    @Test
    @DisplayName("toCommentNotification으로 댓글 Notification 변환")
    void testToCommentNotification_givenValue_willReturnNotification() {
        // when
        Notification result = notificationMapper.toCommentNotification(testRecipientId, testActor, testNotificationActionCommentLiked, testPostId, testCommentPath, testCommentContentPreview);

        // then
        assertEquals(result.getRecipientId(), testRecipientId);
        assertEquals(result.getActor(), testActor);
        assertEquals(result.getAction(), testNotificationActionCommentLiked);
        assertEquals(result.getStatus(), NotificationStatus.unread());
        assertEquals(result.getPostId(), testPostId);
        assertEquals(result.getCommentPath(), testCommentPath);
        assertEquals(result.getContentPreview(), testCommentContentPreview);
        assertNotNull(result.getCreatedAt());
    }

}