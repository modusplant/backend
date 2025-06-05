package kr.modusplant.domains.communication.conversation.common.util.app.http.response;

import kr.modusplant.domains.communication.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCommentEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;

import java.util.UUID;

public interface ConvCommentResponseTestUtils extends ConvCommentEntityTestUtils {
    default ConvCommentResponse createConvCommentResponse(String ulid, UUID authMemberUuid, UUID createMemberUuid) {
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder().build();

        return new ConvCommentResponse(ulid, commentEntity.getPath(),
                authMemberUuid, createMemberUuid, commentEntity.getContent());
    }
}
