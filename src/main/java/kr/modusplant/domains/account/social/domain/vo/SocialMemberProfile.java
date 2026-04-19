package kr.modusplant.domains.account.social.domain.vo;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialMemberProfile {
    private final AccountId accountId;
    private final SocialCredentials socialCredentials;
    private final Email email;
    private final Nickname nickname;
    private final Role role;

    public static SocialMemberProfile create(AccountId accountId, SocialCredentials socialCredentials, Email email, Nickname nickname, Role role) {
        return new SocialMemberProfile(accountId, socialCredentials, email, nickname, role);
    }

    public static SocialMemberProfile createNewMember(SocialCredentials socialCredentials, Email email, Nickname nickname, Role role) {
        return new SocialMemberProfile(null, socialCredentials, email, nickname, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SocialMemberProfile socialMemberProfile)) return false;

        return new EqualsBuilder()
                .append(getAccountId(), socialMemberProfile.getAccountId())
                .append(getSocialCredentials(), socialMemberProfile.getSocialCredentials())
                .append(getEmail(), socialMemberProfile.getEmail())
                .append(getNickname(), socialMemberProfile.getNickname())
                .append(getRole(), socialMemberProfile.getRole())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37)
                .append(getAccountId())
                .append(getSocialCredentials())
                .append(getEmail())
                .append(getNickname())
                .append(getRole())
                .toHashCode();
    }
}
