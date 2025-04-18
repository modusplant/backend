package kr.modusplant.domains.term.common.app.http.response;

import kr.modusplant.domains.term.app.http.response.TermResponse;
import kr.modusplant.domains.term.common.util.domain.TermTestUtils;

public interface TermResponseTestUtils extends TermTestUtils {
    TermResponse termsOfUseResponse = new TermResponse(null, termsOfUse.getName(), termsOfUse.getContent(), termsOfUse.getVersion());

    TermResponse termsOfUseResponseWithUuid = new TermResponse(termsOfUseWithUuid.getUuid(), termsOfUse.getName(), termsOfUse.getContent(), termsOfUse.getVersion());
}
