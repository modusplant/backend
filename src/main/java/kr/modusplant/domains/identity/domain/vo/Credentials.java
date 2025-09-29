package kr.modusplant.domains.identity.domain.vo;

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

    public static Credentials createWithString(String email, String password) {
        return new Credentials(Email.create(email), Password.create(password));
    }

    public static Credentials createWithDomain(Email email, Password password) {
        Email.validateSource(email.getEmail());
        Password.validateSource(password.getPassword());
        return new Credentials(email, password);
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
