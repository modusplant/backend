package kr.modusplant.domains.identity.normal.domain.vo;

import kr.modusplant.domains.identity.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.normal.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.shared.constant.Regex;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NormalNickname {
    private final String value;

    public static NormalNickname create(String input) {
        NormalNickname.validateSource(input);
        return new NormalNickname(input);
    }

    public static void validateSource(String input) {
        if (input == null || input.isBlank()) { throw new EmptyValueException(NormalIdentityErrorCode.EMPTY_NICKNAME); }
        if (!input.matches(Regex.REGEX_NICKNAME)) {
            throw new InvalidValueException(NormalIdentityErrorCode.INVALID_NICKNAME);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof NormalNickname name)) return false;

        return new EqualsBuilder()
                .append(getValue(), name.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue()).toHashCode();
    }
}
