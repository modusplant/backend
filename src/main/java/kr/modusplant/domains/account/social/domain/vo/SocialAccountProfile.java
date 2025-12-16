package kr.modusplant.domains.account.social.domain.vo;

import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialAccountProfile {
    private final SocialCredentials socialCredentials;
    private final Email email;
    private final Nickname nickname;

    public static SocialAccountProfile create(SocialCredentials socialCredentials, Email email, Nickname nickname) {
        return new SocialAccountProfile(socialCredentials, email, nickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SocialAccountProfile socialAccountProfile)) return false;

        return new EqualsBuilder()
                .append(getSocialCredentials(), socialAccountProfile.getSocialCredentials())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getSocialCredentials()).toHashCode();
    }

}
