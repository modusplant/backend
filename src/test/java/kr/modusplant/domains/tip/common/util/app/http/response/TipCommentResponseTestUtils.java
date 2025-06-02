package kr.modusplant.domains.tip.common.util.app.http.response;

import kr.modusplant.domains.communication.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;

import java.util.UUID;

public interface TipCommentResponseTestUtils extends TipCommentEntityTestUtils {
    default TipCommentResponse createTipCommentResponse(String ulid, UUID authMemberUuid, UUID createMemberUuid) {
        TipCommentEntity commentEntity = createTipCommentEntityBuilder().build();

        return new TipCommentResponse(ulid, commentEntity.getPath(),
                authMemberUuid, createMemberUuid, commentEntity.getContent());
    }
}
