package kr.modusplant.domains.notification.common.helper;

import kr.modusplant.jooq.tables.records.CommNotificationRecord;
import kr.modusplant.jooq.tables.records.SiteMemberRecord;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.COMM_NOTIFICATION;

@Component
@RequiredArgsConstructor
public class NotificationTestDataHelper {
    private final DSLContext dsl;

    public CommNotificationRecord insertTestNotification(
            String ulid,
            SiteMemberRecord recipient,
            UUID actorId,
            String actorNickname,
            NotificationActionType action,
            NotificationStatusType status,
            String postUlid,
            String commentPath,
            String contentPreview,
            LocalDateTime createdAt
    ) {
        return dsl.insertInto(COMM_NOTIFICATION)
                .set(COMM_NOTIFICATION.ULID, ulid)
                .set(COMM_NOTIFICATION.RECIPIENT_ID, recipient.getUuid())
                .set(COMM_NOTIFICATION.ACTOR_ID, actorId)
                .set(COMM_NOTIFICATION.ACTOR_NICKNAME, actorNickname)
                .set(COMM_NOTIFICATION.ACTION, action.getValue())
                .set(COMM_NOTIFICATION.STATUS, status.getValue().toUpperCase())
                .set(COMM_NOTIFICATION.POST_ULID, postUlid)
                .set(COMM_NOTIFICATION.COMMENT_PATH, commentPath)
                .set(COMM_NOTIFICATION.CONTENT_PREVIEW, contentPreview)
                .set(COMM_NOTIFICATION.CREATED_AT, createdAt)
                .returning()
                .fetchOneInto(CommNotificationRecord.class);
    }

    public void deleteTestNotifications(CommNotificationRecord... notifications) {
        String[] ulids = java.util.Arrays.stream(notifications)
                .map(CommNotificationRecord::getUlid)
                .toArray(String[]::new);

        dsl.deleteFrom(COMM_NOTIFICATION)
                .where(COMM_NOTIFICATION.ULID.in(ulids))
                .execute();
    }
}
