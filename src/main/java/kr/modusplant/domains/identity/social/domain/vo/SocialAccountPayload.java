package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialAccountPayload {
    private final MemberId memberId;
    private final Nickname nickname;
    private final Email email;
    private final Role role;

    public static SocialAccountPayload create(MemberId memberId, Nickname nickname, Email email, Role role) {
        return new SocialAccountPayload(memberId, nickname, email, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SocialAccountPayload socialAccountPayload)) return false;

        return new EqualsBuilder()
                .append(getMemberId(), socialAccountPayload.getMemberId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMemberId()).toHashCode();
    }


}
