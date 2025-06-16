package kr.modusplant.domains.communication.conversation.common.util.app.http.request;

import kr.modusplant.domains.communication.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCommentEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;

public interface ConvCommentInsertRequestTestUtils extends ConvCommentEntityTestUtils {
    default ConvCommentInsertRequest createConvCommentInsertRequest(String postUlid) {
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder().build();

        return new ConvCommentInsertRequest(postUlid, commentEntity.getPath(), commentEntity.getContent());
    }
}
