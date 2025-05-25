package kr.modusplant.domains.tip.common.util.app.http.request;

import kr.modusplant.domains.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;

import java.util.UUID;

public interface TipCommentInsertRequestTestUtils extends TipCommentEntityTestUtils {
    default TipCommentInsertRequest createTipCommentInsertRequest(String postUlid, UUID createMemberUuid) {
        TipCommentEntity commentEntity = createTipCommentEntityBuilder().build();

        return new TipCommentInsertRequest(postUlid, commentEntity.getPath(),
                createMemberUuid, commentEntity.getContent());
    }
}
