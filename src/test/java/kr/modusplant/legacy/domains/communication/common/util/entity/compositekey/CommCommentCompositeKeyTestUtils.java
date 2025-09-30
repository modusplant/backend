package kr.modusplant.legacy.domains.communication.common.util.entity.compositekey;

import kr.modusplant.framework.out.jpa.entity.CommCommentEntity;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;

public interface CommCommentCompositeKeyTestUtils extends CommCommentEntityTestUtils {
    default CommCommentId createCommCommentCompositeKey(String postUlid) {
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().build();
        return new CommCommentId(postUlid,commentEntity.getContent());
    }
}
