package kr.modusplant.domains.account.normal.domain.vo;

import kr.modusplant.shared.kernel.Nickname;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpData {
    private final NormalCredentials normalCredentials;
    private final Nickname nickname;
    private final AgreedTermVersion agreedTermsOfUseVersion;
    private final AgreedTermVersion agreedPrivacyPolicyVersion;
    private final AgreedTermVersion agreedAdInfoReceivingVersion;

    public static SignUpData create(String email, String password, String nickname,
                                    String termsOfUseVersion, String privacyPolicyVersion,
                                    String adInfoReceivingVersion) {
        return new SignUpData(NormalCredentials.createWithString(email, password),
                Nickname.create(nickname), AgreedTermVersion.create(termsOfUseVersion),
                AgreedTermVersion.create(privacyPolicyVersion), AgreedTermVersion.create(adInfoReceivingVersion));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SignUpData sign)) return false;

        return new EqualsBuilder()
                .append(getNormalCredentials(), sign.getNormalCredentials())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getNormalCredentials()).toHashCode();
    }
}
