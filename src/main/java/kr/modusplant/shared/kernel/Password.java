package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidPasswordException;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.shared.constant.Regex.PATTERN_PASSWORD;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Password {
    private final String value;

    public static Password create(String password) {
        if (password == null || password.isBlank()) {
            throw new EmptyValueException(KernelErrorCode.EMPTY_PASSWORD, "password");
        } else if (!PATTERN_PASSWORD.matcher(password).matches()) {
            throw new InvalidPasswordException();
        }
        return new Password(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Password pw)) return false;

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
