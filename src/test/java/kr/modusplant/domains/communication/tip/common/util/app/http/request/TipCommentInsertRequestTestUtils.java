package kr.modusplant.domains.communication.tip.common.util.app.http.request;

import kr.modusplant.domains.communication.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;

public interface TipCommentInsertRequestTestUtils extends TipCommentEntityTestUtils {
    default TipCommentInsertRequest createTipCommentInsertRequest(String postUlid) {
        TipCommentEntity commentEntity = createTipCommentEntityBuilder().build();

        return new TipCommentInsertRequest(postUlid, commentEntity.getPath(), commentEntity.getContent());
    }
}
