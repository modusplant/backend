package kr.modusplant.domains.identity.email.domain.vo;

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
public class Password {
    private final String password;

    public static Password create(String password) {
        Password.validateSource(password);
        return new Password(password);
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

        if (!(o instanceof Password pw)) return false;

        return new EqualsBuilder()
                .append(getPassword(), pw.getPassword())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPassword()).toHashCode();
    }
}
