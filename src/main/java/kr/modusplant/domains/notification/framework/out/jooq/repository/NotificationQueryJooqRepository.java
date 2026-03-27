package kr.modusplant.domains.notification.framework.out.jooq.repository;

import kr.modusplant.domains.notification.framework.out.jooq.mapper.supers.NotificationJooqMapper;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationQueryRepository;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.shared.enums.NotificationStatusType;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.COMM_NOTIFICATION;
import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.row;

@Repository
@RequiredArgsConstructor
public class NotificationQueryJooqRepository implements NotificationQueryRepository {
    private final DSLContext dsl;
    private final NotificationJooqMapper notificationJooqMapper;

    public List<NotificationReadModel> findByStatusWithCursor(NotificationStatusType status, UUID currentMemberUuid, String cursorUlid, int size) {
        return dsl
                .select(
                        COMM_NOTIFICATION.ULID,
                        COMM_NOTIFICATION.RECIPIENT_ID,
                        COMM_NOTIFICATION.ACTOR_ID,
                        COMM_NOTIFICATION.ACTOR_NICKNAME,
                        COMM_NOTIFICATION.ACTION,
                        COMM_NOTIFICATION.STATUS,
                        COMM_NOTIFICATION.POST_ULID,
                        COMM_NOTIFICATION.COMMENT_PATH,
                        COMM_NOTIFICATION.CONTENT_PREVIEW,
                        COMM_NOTIFICATION.CREATED_AT
                )
                .from(COMM_NOTIFICATION)
                .where(COMM_NOTIFICATION.RECIPIENT_ID.eq(currentMemberUuid))
                .and(buildStatusCondition(status))
                .and(buildCursorCondition(cursorUlid))
                .orderBy(COMM_NOTIFICATION.CREATED_AT.desc(), COMM_NOTIFICATION.ULID.desc())
                .limit(size+1)
                .fetch()
                .map(notificationJooqMapper::toNotificationReadModel);
    }

    private Condition buildStatusCondition(NotificationStatusType status) {
        if (status == null) {
            return noCondition();
        }
        return COMM_NOTIFICATION.STATUS.eq(status.getValue().toUpperCase());
    }

    private Condition buildCursorCondition(String cursorUlid) {
        if (cursorUlid == null || cursorUlid.isBlank()) {
            return noCondition();
        }
        LocalDateTime cursorCreatedAt = dsl.select(COMM_NOTIFICATION.CREATED_AT)
                .from(COMM_NOTIFICATION)
                .where(COMM_NOTIFICATION.ULID.eq(cursorUlid))
                .fetchOne(COMM_NOTIFICATION.CREATED_AT);

        return row(COMM_NOTIFICATION.CREATED_AT, COMM_NOTIFICATION.ULID).lessThan(cursorCreatedAt,cursorUlid);
    }
}
