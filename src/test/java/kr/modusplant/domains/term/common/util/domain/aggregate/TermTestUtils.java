package kr.modusplant.domains.term.common.util.domain.aggregate;

import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.vo.TermContent;
import kr.modusplant.domains.term.domain.vo.TermName;
import kr.modusplant.domains.term.domain.vo.TermVersion;

import static kr.modusplant.domains.term.common.util.domain.vo.TermIdTestUtils.testTermId;
import static kr.modusplant.shared.persistence.common.util.constant.TermConstant.*;

public interface TermTestUtils {
    default Term createTerm() {
        return Term.create(
                testTermId,
                TermName.create(TEST_TERMS_OF_USE_NAME),
                TermContent.create(TEST_TERMS_OF_USE_CONTENT),
                TermVersion.create(TEST_TERMS_OF_USE_VERSION)
        );
    }
}
