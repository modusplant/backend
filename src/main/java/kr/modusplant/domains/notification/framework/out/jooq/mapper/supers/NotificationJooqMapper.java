package kr.modusplant.domains.notification.framework.out.jooq.mapper.supers;

import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import org.jooq.Record;

public interface NotificationJooqMapper {
    NotificationReadModel toNotificationReadModel(Record record);
}
