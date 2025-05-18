package kr.modusplant.domains.tip.common.util.entity.compositekey;

import kr.modusplant.domains.tip.persistence.entity.compositekey.TipCommentCompositeKey;

import static kr.modusplant.domains.tip.common.util.domain.TipPostTestUtils.tipPostWithUlid;

public interface TipCommentCompositeKeyTestUtils {
    default TipCommentCompositeKey createCompositeKeyWithAllArgs() {
        return new TipCommentCompositeKey(tipPostWithUlid.getUlid(),"/1/6/2");
    }
}
