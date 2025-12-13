package kr.modusplant.domains.account.normal.domain.vo;

import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NormalCredentials {
    private final Email email;
    private final Password password;

    public static NormalCredentials createWithString(String email, String password) {
        return new NormalCredentials(Email.create(email), Password.create(password));
    }

    public static NormalCredentials createWithDomain(Email email, Password password) {
        return new NormalCredentials(email, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof NormalCredentials normalCredentials)) return false;

        return new EqualsBuilder()
                .append(getEmail(), normalCredentials.getEmail())
                .append(getPassword(), normalCredentials.getPassword())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getEmail())
                .append(getPassword()).toHashCode();
    }
}
