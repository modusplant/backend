package kr.modusplant.domains.account.social.domain.vo;

import kr.modusplant.domains.account.social.domain.exception.EmptyValueException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AgreedTerms {
    private final AgreedTermVersion agreedTermsOfUseVersion;
    private final AgreedTermVersion agreedPrivacyPolicyVersion;
    private final AgreedTermVersion agreedCommunityPolicyVersion;

    public static AgreedTerms create(AgreedTermVersion termsOfUserVersion, AgreedTermVersion privacyPolicyVersion, AgreedTermVersion communityPolicyVersion) {
        if(termsOfUserVersion==null || privacyPolicyVersion == null || communityPolicyVersion == null) {
            throw new EmptyValueException(SocialIdentityErrorCode.EMPTY_AGREED_TERMS_OF_VERSION);
        }
        return new AgreedTerms(termsOfUserVersion, privacyPolicyVersion, communityPolicyVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AgreedTerms agreedTerms)) return false;

        return new EqualsBuilder()
                .append(getAgreedTermsOfUseVersion(), agreedTerms.getAgreedTermsOfUseVersion())
                .append(getAgreedPrivacyPolicyVersion(), agreedTerms.getAgreedPrivacyPolicyVersion())
                .append(getAgreedCommunityPolicyVersion(), agreedTerms.getAgreedCommunityPolicyVersion())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37)
                .append(getAgreedTermsOfUseVersion())
                .append(getAgreedPrivacyPolicyVersion())
                .append(getAgreedCommunityPolicyVersion())
                .toHashCode();
    }

}
