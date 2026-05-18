package kr.modusplant.domains.term.common.util.domain.vo;

import kr.modusplant.domains.term.domain.vo.TermId;

import static kr.modusplant.domains.term.common.constant.TermConstant.TEST_TERMS_OF_USE_UUID;

public interface TermIdTestUtils {
    TermId testTermId = TermId.fromUuid(TEST_TERMS_OF_USE_UUID);
}
