package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyEmailException;
import kr.modusplant.shared.exception.InvalidEmailException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.shared.constant.Regex.PATTERN_EMAIL;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Email {
    private final String value;

    public static Email create(String email) {
        if (email == null || email.isBlank()) {
            throw new EmptyEmailException();
        } else if (!PATTERN_EMAIL.matcher(email).matches()) {
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
