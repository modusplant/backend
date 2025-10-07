package kr.modusplant.domains.identity.normal.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpData {
    private final Credentials credentials;
    private final Nickname nickname;
    private final AgreedTermsOfVersion agreedTermsOfUseVersion;
    private final AgreedTermsOfVersion agreedPrivacyPolicyVersion;
    private final AgreedTermsOfVersion agreedAdInfoReceivingVersion;

    public static SignUpData create(String email, String password, String nickname,
                                    String termsOfUseVersion, String privacyPolicyVersion,
                                    String adInfoReceivingVersion) {
        return new SignUpData(Credentials.createWithString(email, password),
                Nickname.create(nickname), AgreedTermsOfVersion.create(termsOfUseVersion),
                AgreedTermsOfVersion.create(privacyPolicyVersion), AgreedTermsOfVersion.create(adInfoReceivingVersion));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SignUpData sign)) return false;

        return new EqualsBuilder()
                .append(getCredentials(), sign.getCredentials())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getCredentials()).toHashCode();
    }
}
