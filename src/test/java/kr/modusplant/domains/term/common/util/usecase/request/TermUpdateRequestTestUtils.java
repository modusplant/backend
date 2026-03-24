package kr.modusplant.domains.term.common.util.usecase.request;

import kr.modusplant.domains.term.usecase.request.TermUpdateRequest;

import static kr.modusplant.shared.persistence.common.util.constant.TermConstant.*;

public interface TermUpdateRequestTestUtils {
    TermUpdateRequest testTermUpdateRequest = new TermUpdateRequest(
            TEST_TERMS_OF_USE_UUID,
            TEST_TERMS_OF_USE_CONTENT
    );
}
