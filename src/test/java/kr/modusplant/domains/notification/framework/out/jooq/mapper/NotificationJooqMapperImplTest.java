package kr.modusplant.domains.notification.framework.out.jooq.mapper;

import kr.modusplant.domains.notification.framework.out.jooq.mapper.supers.NotificationJooqMapper;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import org.jooq.Record;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static kr.modusplant.jooq.Tables.COMM_NOTIFICATION;
import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class NotificationJooqMapperImplTest {
    private final NotificationJooqMapper notificationJooqMapper = new NotificationJooqMapperImpl();

    @Test
    @DisplayName("toNotificationReadModel로 NotificationReadModel 반환하기")
    void testToNotificationReadModel_givenRecord_willReturnNotificationReadModel() {
        // given
        Record record = mock(Record.class);
        LocalDateTime createdAt = LocalDateTime.now();

        given(record.get(COMM_NOTIFICATION.ULID)).willReturn(TEST_NOTIFICATION_ULID);
        given(record.get(COMM_NOTIFICATION.RECIPIENT_ID)).willReturn(TEST_NOTIFICATION_RECIPIENT_ID);
        given(record.get(COMM_NOTIFICATION.ACTOR_ID)).willReturn(TEST_NOTIFICATION_ACTOR_ID);
        given(record.get(COMM_NOTIFICATION.ACTOR_NICKNAME)).willReturn(TEST_NOTIFICATION_ACTOR_NICKNAME);
        given(record.get(COMM_NOTIFICATION.ACTION)).willReturn(NotificationActionType.COMMENT_ADDED.getValue());
        given(record.get(COMM_NOTIFICATION.STATUS)).willReturn(NotificationStatusType.UNREAD.name());
        given(record.get(COMM_NOTIFICATION.POST_ULID)).willReturn(TEST_NOTIFICATION_POST_ULID);
        given(record.get(COMM_NOTIFICATION.COMMENT_PATH)).willReturn(TEST_NOTIFICATION_COMMENT_PATH);
        given(record.get(COMM_NOTIFICATION.CONTENT_PREVIEW)).willReturn(TEST_NOTIFICATION_POST_PREVIEW);
        given(record.get(COMM_NOTIFICATION.CREATED_AT)).willReturn(createdAt);

        // when
        NotificationReadModel result = notificationJooqMapper.toNotificationReadModel(record);

        // then
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("ulid", TEST_NOTIFICATION_ULID)
                .hasFieldOrPropertyWithValue("recipientId", TEST_NOTIFICATION_RECIPIENT_ID)
                .hasFieldOrPropertyWithValue("actorId", TEST_NOTIFICATION_ACTOR_ID)
                .hasFieldOrPropertyWithValue("actorNickname", TEST_NOTIFICATION_ACTOR_NICKNAME)
                .hasFieldOrPropertyWithValue("action", NotificationActionType.COMMENT_ADDED)
                .hasFieldOrPropertyWithValue("status", NotificationStatusType.UNREAD)
                .hasFieldOrPropertyWithValue("postUlid", TEST_NOTIFICATION_POST_ULID)
                .hasFieldOrPropertyWithValue("commentPath", TEST_NOTIFICATION_COMMENT_PATH)
                .hasFieldOrPropertyWithValue("contentPreview", TEST_NOTIFICATION_POST_PREVIEW)
                .hasFieldOrPropertyWithValue("createdAt", createdAt);
    }

}