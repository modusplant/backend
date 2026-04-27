package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import kr.modusplant.domains.notification.domain.exception.enums.NotificationErrorCode;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
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
public class ActorId {
    private final UUID value;

    public static ActorId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_ACTOR_ID);
        }
        return new ActorId(uuid);
    }

    public static ActorId fromString(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_ACTOR_ID);
        }
        if (!PATTERN_UUID.matcher(value).matches()) {
            throw new InvalidValueException(NotificationErrorCode.INVALID_ACTOR_ID);
        }
        return new ActorId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ActorId actorId)) return false;

        return new EqualsBuilder().append(getValue(), actorId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
