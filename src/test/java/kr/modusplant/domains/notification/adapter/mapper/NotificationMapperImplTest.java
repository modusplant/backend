package kr.modusplant.domains.notification.adapter.mapper;

import kr.modusplant.domains.notification.usecase.port.mapper.NotificationMapper;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationMapperImplTest {
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
                TEST_NOTIFICATION_COMMENT_PATH,
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

}