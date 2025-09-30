package kr.modusplant.legacy.domains.communication.common.util.app.http.request;

import kr.modusplant.framework.out.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.out.jpa.entity.util.CommCommentEntityTestUtils;
import kr.modusplant.legacy.domains.communication.app.http.request.CommCommentInsertRequest;

public interface CommCommentInsertRequestTestUtils extends CommCommentEntityTestUtils {
    default CommCommentInsertRequest createCommCommentInsertRequest(String postUlid) {
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().build();

        return new CommCommentInsertRequest(postUlid, commentEntity.getPath(), commentEntity.getContent());
    }
}
