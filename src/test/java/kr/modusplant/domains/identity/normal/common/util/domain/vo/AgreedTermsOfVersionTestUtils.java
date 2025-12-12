package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.AgreedTermVersion;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface AgreedTermsOfVersionTestUtils {
    AgreedTermVersion testAgreedTermsOfUse = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
    AgreedTermVersion testAgreedPrivacyPolicy = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
    AgreedTermVersion testAgreedAdReceiving = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
}
