package kr.modusplant.domains.notification.framework.out.jooq.mapper;

import kr.modusplant.domains.notification.framework.out.jooq.mapper.supers.NotificationJooqMapper;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import static kr.modusplant.jooq.Tables.COMM_NOTIFICATION;

@Component
public class NotificationJooqMapperImpl implements NotificationJooqMapper {

    @Override
    public NotificationReadModel toNotificationReadModel(Record record) {
        return new NotificationReadModel(
                record.get(COMM_NOTIFICATION.ULID),
                record.get(COMM_NOTIFICATION.RECIPIENT_ID),
                record.get(COMM_NOTIFICATION.ACTOR_ID),
                record.get(COMM_NOTIFICATION.ACTOR_NICKNAME),
                NotificationActionType.valueOf(record.get(COMM_NOTIFICATION.ACTION)),
                NotificationStatusType.valueOf(record.get(COMM_NOTIFICATION.STATUS)),
                record.get(COMM_NOTIFICATION.POST_ULID),
                record.get(COMM_NOTIFICATION.COMMENT_PATH),
                record.get(COMM_NOTIFICATION.CONTENT_PREVIEW),
                record.get(COMM_NOTIFICATION.CREATED_AT)
        );
    }
}
