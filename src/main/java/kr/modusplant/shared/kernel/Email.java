package kr.modusplant.shared.kernel;

import kr.modusplant.shared.constant.Regex;
import kr.modusplant.shared.exception.EmptyEmailException;
import kr.modusplant.shared.exception.InvalidEmailException;
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

        if (!(o instanceof Email)) return false;

        return new EqualsBuilder().append(getValue(), ((Email) o).getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
