package kr.modusplant.domains.account.social.domain.vo;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.shared.kernel.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialProfile {
    private final SocialProvider socialProvider;
    private final String providerId;
    private final Email email;
    private final String socialNickname;

    public static SocialProfile create(SocialProvider socialProvider, String providerId, Email email, String socialNickname) {
        return new SocialProfile(socialProvider, providerId, email, socialNickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SocialProfile socialProfile)) return false;

        return new EqualsBuilder()
                .append(getSocialProvider(), socialProfile.getSocialProvider())
                .append(getProviderId(), socialProfile.getProviderId())
                .append(getEmail(), socialProfile.getEmail())
                .append(getSocialNickname(), socialProfile.getSocialNickname())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37)
                .append(getSocialProvider())
                .append(getProviderId())
                .append(getEmail())
                .append(getSocialNickname())
                .toHashCode();
    }

}
