package kr.modusplant.domains.identity.account.domain.vo;

import kr.modusplant.domains.identity.account.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.account.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.account.domain.exception.enums.AccountErrorCode;
import kr.modusplant.shared.constant.Regex;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Email {
    private final String value;

    public static Email create(String email) {
        Email.validateSource(email);
        return new Email(email);
    }

    public static void validateSource(String email) {
        if (email == null || email.isBlank()) { throw new EmptyValueException(AccountErrorCode.EMPTY_EMAIL); }
        if (!email.matches(Regex.REGEX_EMAIL)) {
            throw new InvalidValueException(AccountErrorCode.INVALID_EMAIL);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Email mail)) return false;

        return new EqualsBuilder()
                .append(getValue(), mail.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue()).toHashCode();
    }
}
