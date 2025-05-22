package kr.modusplant.domains.tip.common.util.app.http.response;

import kr.modusplant.domains.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;

import java.util.UUID;

public interface TipCommentResponseTestUtils extends TipCommentEntityTestUtils {
    default TipCommentResponse createTipCommentResponse(String ulid, UUID authMemberUuid, UUID createMemberUuid) {
        TipCommentEntity commentEntity = createTipCommentEntityBuilder().build();

        return new TipCommentResponse(ulid, commentEntity.getMaterializedPath(),
                authMemberUuid, createMemberUuid, commentEntity.getContent());
    }
}
