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
public class NormalPassword {
    private final String value;

    public static NormalPassword create(String password) {
        NormalPassword.validateSource(password);
        return new NormalPassword(password);
    }

    public static void validateSource(String password) {
        if (password == null || password.isBlank()) { throw new EmptyValueException(NormalIdentityErrorCode.EMPTY_PASSWORD); }
        if (!password.matches(Regex.REGEX_PASSWORD)) {
            throw new InvalidValueException(NormalIdentityErrorCode.INVALID_PASSWORD);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof NormalPassword pw)) return false;

        return new EqualsBuilder()
                .append(getValue(), pw.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue()).toHashCode();
    }
}
