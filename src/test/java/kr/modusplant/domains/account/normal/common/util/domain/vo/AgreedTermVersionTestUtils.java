package kr.modusplant.domains.account.normal.common.util.domain.vo;

import kr.modusplant.domains.account.normal.domain.vo.AgreedTermVersion;

import static kr.modusplant.domains.term.common.constant.MemberTermConstant.*;

public interface AgreedTermVersionTestUtils {
    AgreedTermVersion testAgreedTermsOfUse = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
    AgreedTermVersion testAgreedPrivacyPolicy = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
    AgreedTermVersion testAgreedCommunityPolicy = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);
}
