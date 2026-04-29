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

import static kr.modusplant.shared.constant.Regex.PATTERN_NICKNAME;
import static kr.modusplant.shared.constant.Regex.PATTERN_UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Actor {
    private final UUID id;
    private final String nickname;

    public static Actor fromUuidWithNickname(UUID uuid, String nickname) {
        if (uuid == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_ACTOR_ID);
        }
        validateNickname(nickname);
        return new Actor(uuid,nickname);
    }

    public static Actor fromStringWithNickname(String value, String nickname) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_ACTOR_ID);
        }
        if (!PATTERN_UUID.matcher(value).matches()) {
            throw new InvalidValueException(NotificationErrorCode.INVALID_ACTOR_ID);
        }
        validateNickname(nickname);
        return new Actor(UUID.fromString(value), nickname);
    }

    private static void validateNickname(String nickname) {
        if (nickname == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_ACTOR_NICKNAME);
        }
        if (!PATTERN_NICKNAME.matcher(nickname).matches()) {
            throw new InvalidValueException(NotificationErrorCode.INVALID_ACTOR_NICKNAME);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Actor actor)) return false;

        return new EqualsBuilder().append(getId(), actor.getId()).append(getNickname(), actor.getNickname()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getNickname()).toHashCode();
    }
}
