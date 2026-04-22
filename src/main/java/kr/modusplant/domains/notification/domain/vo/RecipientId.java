package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import kr.modusplant.domains.notification.domain.exception.enums.NotificationErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.PATTERN_UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipientId {
    private final UUID value;

    public static RecipientId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_RECIPIENT_ID);
        }
        return new RecipientId(uuid);
    }

    public static RecipientId fromString(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_RECIPIENT_ID);
        }
        if (!PATTERN_UUID.matcher(value).matches()) {
            throw new InvalidValueException(NotificationErrorCode.INVALID_RECIPIENT_ID);
        }
        return new RecipientId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RecipientId recipientId)) return false;

        return new EqualsBuilder().append(getValue(), recipientId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
