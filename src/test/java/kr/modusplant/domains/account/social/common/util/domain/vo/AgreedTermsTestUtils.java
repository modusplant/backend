package kr.modusplant.domains.account.social.common.util.domain.vo;

import kr.modusplant.domains.account.social.domain.vo.AgreedTerms;

public interface AgreedTermsTestUtils extends AgreedTermVersionTestUtils {
    AgreedTerms testAgreedTerms = AgreedTerms.create(testAgreedTermsOfUse, testAgreedPrivacyPolicy, testAgreedCommunityPolicy);
}
