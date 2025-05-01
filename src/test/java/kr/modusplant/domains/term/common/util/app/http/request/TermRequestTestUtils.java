package kr.modusplant.domains.term.common.util.app.http.request;

import kr.modusplant.domains.term.app.http.request.TermInsertRequest;
import kr.modusplant.domains.term.app.http.request.TermUpdateRequest;
import kr.modusplant.domains.term.common.util.domain.TermTestUtils;

public interface TermRequestTestUtils extends TermTestUtils {
    TermInsertRequest termsOfUseInsertRequest = new TermInsertRequest(termsOfUse.getName(), termsOfUse.getContent(), termsOfUse.getVersion());

    TermUpdateRequest termsOfUseUpdateRequest = new TermUpdateRequest(termsOfUseWithUuid.getUuid(), termsOfUse.getContent(), termsOfUse.getVersion());
}
