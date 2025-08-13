package kr.modusplant.legacy.domains.term.common.util.app.http.response;

import kr.modusplant.legacy.domains.term.app.http.response.TermResponse;
import kr.modusplant.legacy.domains.term.common.util.domain.TermTestUtils;

public interface TermResponseTestUtils extends TermTestUtils {
    TermResponse termsOfUseResponse = new TermResponse(termsOfUseWithUuid.getUuid(), termsOfUse.getName(), termsOfUse.getContent(), termsOfUse.getVersion());
}
