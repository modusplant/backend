package kr.modusplant.legacy.domains.communication.common.util.app.http.response;

import kr.modusplant.framework.out.jpa.entity.CommCommentEntity;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommCommentEntityTestUtils;

import java.util.UUID;

public interface CommCommentResponseTestUtils extends CommCommentEntityTestUtils {
    default CommCommentResponse createCommCommentResponse(String ulid, UUID memberUuid, String nickname) {
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().build();
        return new CommCommentResponse(ulid, commentEntity.getPath(), memberUuid, nickname, commentEntity.getContent());
    }
}
