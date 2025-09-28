package kr.modusplant.domains.identity.domain.vo;

import kr.modusplant.domains.identity.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import kr.modusplant.shared.constant.Regex;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Email {
    private final String email;

    public static Email create(String email) {
        Email.validateSource(email);
        return new Email(email);
    }

    public static void validateSource(String email) {
        if (email == null || email.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_EMAIL); }
        if (!email.matches(Regex.REGEX_EMAIL)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_EMAIL);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Email mail)) return false;

        return new EqualsBuilder()
                .append(getEmail(), mail.getEmail())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getEmail()).toHashCode();
    }
}
