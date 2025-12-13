package kr.modusplant.domains.account.normal.common.util.domain.vo;

import kr.modusplant.domains.account.normal.domain.vo.AgreedTermVersion;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface AgreedTermVersionTestUtils {
    AgreedTermVersion testAgreedTermsOfUse = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
    AgreedTermVersion testAgreedPrivacyPolicy = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
    AgreedTermVersion testAgreedAdReceiving = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
}
