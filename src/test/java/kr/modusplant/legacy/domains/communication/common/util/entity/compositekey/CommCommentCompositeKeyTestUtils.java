package kr.modusplant.legacy.domains.communication.common.util.entity.compositekey;

import kr.modusplant.legacy.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommCommentEntity;
import kr.modusplant.legacy.domains.communication.persistence.entity.compositekey.CommCommentCompositeKey;

public interface CommCommentCompositeKeyTestUtils extends CommCommentEntityTestUtils {
    default CommCommentCompositeKey createCommCommentCompositeKey(String postUlid) {
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().build();
        return new CommCommentCompositeKey(postUlid,commentEntity.getContent());
    }
}
