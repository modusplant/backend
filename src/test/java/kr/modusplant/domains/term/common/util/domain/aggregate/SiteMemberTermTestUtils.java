package kr.modusplant.domains.term.common.util.domain.aggregate;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;

import static kr.modusplant.domains.term.common.util.domain.vo.SiteMemberTermIdTestUtils.testSiteMemberTermId;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface SiteMemberTermTestUtils {
    default SiteMemberTerm createSiteMemberTerm() {
        return SiteMemberTerm.create(
                testSiteMemberTermId,
                MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION,
                MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION,
                MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION
        );
    }
}
