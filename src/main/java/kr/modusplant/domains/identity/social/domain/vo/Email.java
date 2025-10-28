package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.domains.identity.social.domain.exception.EmptyEmailException;
import kr.modusplant.domains.identity.social.domain.exception.InvalidEmailException;
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
        if (email == null || email.isBlank()) {
            throw new EmptyEmailException();
        }
        if (!email.matches(Regex.REGEX_EMAIL)) {
            throw new InvalidEmailException();
        }
        return new Email(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Email email)) return false;

        return new EqualsBuilder().append(getEmail(), email.getEmail()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getEmail()).toHashCode();
    }
}
