package kr.modusplant.domains.identity.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.domain.constant.IdentityDataFormat;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Credentials {
    private final String email;
    private final String password;

    public static Credentials create(String email, String password) {
        Credentials.validateEmail(email);
        Credentials.validatePassword(password);
        return new Credentials(email, password);
    }

    public static void validateEmail(String email) {
        if (email.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_EMAIL); }
        if (!email.matches(IdentityDataFormat.EMAIL_FORMAT)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_EMAIL);
        }
    }

    public static void validatePassword(String password) {
        if (password.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_PASSWORD); }
        if (!password.matches(IdentityDataFormat.PASSWORD_FORMAT)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_PASSWORD);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Credentials credentials)) return false;

        return new EqualsBuilder()
                .append(getEmail(), credentials.getEmail())
                .append(getPassword(), credentials.getPassword())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getEmail())
                .append(getPassword()).toHashCode();
    }
}
