package kr.modusplant.domains.term.domain.aggregate;

import kr.modusplant.domains.term.domain.exception.EmptyValueException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.domains.term.domain.vo.SiteMemberTermId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SiteMemberTerm {
    private final SiteMemberTermId siteMemberTermId;
    private final String agreedTermsOfUseVersion;
    private final String agreedPrivacyPolicyVersion;
    private final String agreedAdInfoReceivingVersion;

    public static SiteMemberTerm create(SiteMemberTermId siteMemberTermId, String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
        if(siteMemberTermId == null) throw new EmptyValueException(TermErrorCode.EMPTY_SITE_MEMBER_TERM_ID);
        if(agreedTermsOfUseVersion == null) throw new EmptyValueException(TermErrorCode.EMPTY_AGREED_TERM_OF_USE_VERSION);
        if(agreedPrivacyPolicyVersion == null) throw new EmptyValueException(TermErrorCode.EMPTY_AGREED_PRIVACY_POLICY_VERSION);
        if(agreedAdInfoReceivingVersion == null) throw new EmptyValueException(TermErrorCode.EMPTY_AGREED_AD_INFO_RECEIVING_VERSION);
        return new SiteMemberTerm(siteMemberTermId, agreedTermsOfUseVersion,  agreedPrivacyPolicyVersion, agreedAdInfoReceivingVersion);
    }

    public SiteMemberTerm create(String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
        if(agreedTermsOfUseVersion == null) agreedTermsOfUseVersion = this.agreedTermsOfUseVersion;
        if(agreedPrivacyPolicyVersion == null) agreedPrivacyPolicyVersion = this.agreedPrivacyPolicyVersion;
        if(agreedAdInfoReceivingVersion == null) agreedAdInfoReceivingVersion = this.agreedAdInfoReceivingVersion;
        return new SiteMemberTerm(this.siteMemberTermId, agreedTermsOfUseVersion, agreedPrivacyPolicyVersion, agreedAdInfoReceivingVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteMemberTerm siteMemberTerm)) return false;

        return new EqualsBuilder().append(getSiteMemberTermId(), siteMemberTerm.getSiteMemberTermId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getSiteMemberTermId()).toHashCode();
    }
}
