package kr.modusplant.domains.term.common.util.usecase.request;

import kr.modusplant.domains.term.usecase.request.TermCreateRequest;

import static kr.modusplant.shared.persistence.common.util.constant.TermConstant.*;

public interface TermCreateRequestTestUtils {
    TermCreateRequest testTermCreateRequest = new TermCreateRequest(
            TEST_TERMS_OF_USE_NAME,
            TEST_TERMS_OF_USE_VERSION,
            TEST_TERMS_OF_USE_CONTENT
    );
}
