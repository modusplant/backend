package kr.modusplant.domains.term.common.util.usecase.response;

import kr.modusplant.domains.term.usecase.response.TermResponse;

import static kr.modusplant.domains.term.common.constant.TermConstant.*;

public interface TermResponseTestUtils {
    TermResponse testTermResponse = new TermResponse(
            TEST_TERMS_OF_USE_UUID,
            TEST_TERMS_OF_USE_NAME,
            TEST_TERMS_OF_USE_CONTENT,
            TEST_TERMS_OF_USE_VERSION
    );
}
