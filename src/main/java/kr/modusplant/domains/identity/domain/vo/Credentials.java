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
public class Credentials {
    private final Email email;
    private final Password password;

    public static Credentials create(String email, String password) {
        return new Credentials(Email.create(email), Password.create(password));
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
