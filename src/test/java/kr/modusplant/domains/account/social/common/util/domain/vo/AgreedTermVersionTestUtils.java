package kr.modusplant.domains.account.social.common.util.domain.vo;


import kr.modusplant.domains.account.social.domain.vo.AgreedTermVersion;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface AgreedTermVersionTestUtils {
    AgreedTermVersion testAgreedTermsOfUse = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
    AgreedTermVersion testAgreedPrivacyPolicy = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
    AgreedTermVersion testAgreedCommunityPolicy = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);

}
