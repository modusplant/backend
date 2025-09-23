package kr.modusplant.domains.identity.domain.vo;

import kr.modusplant.domains.identity.domain.constant.IdentityDataFormat;
import kr.modusplant.domains.identity.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Nickname {
    private final String nickname;

    public static Nickname create(String input) {
        Nickname.validateSource(input);
        return new Nickname(input);
    }

    public static void validateSource(String input) {
        if (input == null || input.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_NICKNAME); }
        if (!input.matches(IdentityDataFormat.NICKNAME_FORMAT)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_NICKNAME);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Nickname name)) return false;

        return new EqualsBuilder()
                .append(getNickname(), name.getNickname())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getNickname()).toHashCode();
    }
}
