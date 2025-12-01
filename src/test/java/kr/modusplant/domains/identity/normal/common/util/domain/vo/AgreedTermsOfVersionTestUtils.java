package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.AgreedTermsOfVersion;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface AgreedTermsOfVersionTestUtils {
    AgreedTermsOfVersion testAgreedTermsOfUse = AgreedTermsOfVersion.create(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
    AgreedTermsOfVersion testAgreedPrivacyPolicy = AgreedTermsOfVersion.create(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
    AgreedTermsOfVersion testAgreedAdReceiving = AgreedTermsOfVersion.create(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
}
