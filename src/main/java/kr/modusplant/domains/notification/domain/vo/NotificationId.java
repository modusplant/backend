package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import kr.modusplant.domains.notification.domain.exception.enums.NotificationErrorCode;
import kr.modusplant.framework.jpa.generator.UlidIdGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.generator.EventType;

import static kr.modusplant.shared.constant.Regex.PATTERN_ULID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationId {
    private static final UlidIdGenerator generator = new UlidIdGenerator();

    private final String value;

    public static NotificationId generate() {
        return new NotificationId(generator.generate(null,null,null, EventType.INSERT));
    }

    public static NotificationId create(String ulid) {
        if (StringUtils.isBlank(ulid)) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_NOTIFICATION_ID);
        }
        if (StringUtils.isBlank(ulid) || ulid.length() != 26 || !PATTERN_ULID.matcher(ulid).matches()) {
            throw new InvalidValueException(NotificationErrorCode.INVALID_NOTIFICATION_ID);
        }
        return new NotificationId(ulid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof NotificationId notificationId)) return false;

        return new EqualsBuilder().append(getValue(),notificationId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getValue()).toHashCode();
    }
}
