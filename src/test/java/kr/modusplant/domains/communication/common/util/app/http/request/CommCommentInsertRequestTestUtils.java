package kr.modusplant.domains.communication.common.util.app.http.request;

import kr.modusplant.domains.communication.app.http.request.CommCommentInsertRequest;
import kr.modusplant.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.domains.communication.persistence.entity.CommCommentEntity;

public interface CommCommentInsertRequestTestUtils extends CommCommentEntityTestUtils {
    default CommCommentInsertRequest createCommCommentInsertRequest(String postUlid) {
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().build();

        return new CommCommentInsertRequest(postUlid, commentEntity.getPath(), commentEntity.getContent());
    }
}
