package kr.modusplant.domains.communication.common.util.app.http.response;

import kr.modusplant.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.domains.communication.persistence.entity.CommCommentEntity;

import java.util.UUID;

public interface CommCommentResponseTestUtils extends CommCommentEntityTestUtils {
    default CommCommentResponse createCommCommentResponse(String ulid, UUID memberUuid, String nickname) {
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().build();
        return new CommCommentResponse(ulid, commentEntity.getPath(), memberUuid, nickname, commentEntity.getContent());
    }
}
