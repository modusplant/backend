package kr.modusplant.domains.identity.normal.domain.vo;

import kr.modusplant.shared.kernel.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Credentials {
    private final Email email;
    private final NormalPassword password;

    public static Credentials createWithString(String email, String password) {
        return new Credentials(Email.create(email), NormalPassword.create(password));
    }

    public static Credentials createWithDomain(Email email, NormalPassword normalPassword) {
        NormalPassword.validateSource(normalPassword.getValue());
        return new Credentials(email, normalPassword);
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
