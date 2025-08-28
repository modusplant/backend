package kr.modusplant.legacy.domains.communication.common.util.entity.compositekey;

import kr.modusplant.legacy.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.framework.out.persistence.entity.CommCommentEntity;
import kr.modusplant.framework.out.persistence.entity.compositekey.CommCommentId;

public interface CommCommentCompositeKeyTestUtils extends CommCommentEntityTestUtils {
    default CommCommentId createCommCommentCompositeKey(String postUlid) {
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().build();
        return new CommCommentId(postUlid,commentEntity.getContent());
    }
}
