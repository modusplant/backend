package kr.modusplant.domains.identity.domain.vo;

import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import kr.modusplant.domains.identity.domain.vo.enums.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Role {
    private final UserRole role;

    public static Role create(String input) {
        if(!UserRole.isValidStatus(input)) { throw new InvalidValueException(IdentityErrorCode.INVALID_ROLE); }
        return new Role(UserRole.valueOf(input));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Role roleObj)) return false;

        return new EqualsBuilder()
                .append(getRole(), roleObj.getRole())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getRole()).toHashCode();
    }
}
