package kr.modusplant.legacy.domains.communication.common.util.entity.compositekey;

import kr.modusplant.framework.out.persistence.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.out.persistence.jpa.entity.compositekey.CommCommentId;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommCommentEntityTestUtils;

public interface CommCommentCompositeKeyTestUtils extends CommCommentEntityTestUtils {
    default CommCommentId createCommCommentCompositeKey(String postUlid) {
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().build();
        return new CommCommentId(postUlid,commentEntity.getContent());
    }
}
