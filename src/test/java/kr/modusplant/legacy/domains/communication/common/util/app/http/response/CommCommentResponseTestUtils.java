package kr.modusplant.legacy.domains.communication.common.util.app.http.response;

import kr.modusplant.legacy.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.framework.out.persistence.entity.CommCommentEntity;

import java.util.UUID;

public interface CommCommentResponseTestUtils extends CommCommentEntityTestUtils {
    default CommCommentResponse createCommCommentResponse(String ulid, UUID memberUuid, String nickname) {
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().build();
        return new CommCommentResponse(ulid, commentEntity.getPath(), memberUuid, nickname, commentEntity.getContent());
    }
}
