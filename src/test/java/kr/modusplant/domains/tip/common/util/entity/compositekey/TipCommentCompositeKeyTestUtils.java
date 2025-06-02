package kr.modusplant.domains.tip.common.util.entity.compositekey;

import kr.modusplant.domains.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.compositekey.TipCommentCompositeKey;

public interface TipCommentCompositeKeyTestUtils extends TipCommentEntityTestUtils {
    default TipCommentCompositeKey createTipCommentCompositeKey(String postUlid) {
        TipCommentEntity commentEntity = createTipCommentEntityBuilder().build();

        return new TipCommentCompositeKey(postUlid,commentEntity.getContent());
    }
}
