package kr.modusplant.domains.tip.common.util.entity.compositekey;

import kr.modusplant.domains.tip.persistence.entity.compositekey.TipCommentCompositeKey;

public interface TipCommentCompositeKeyTestUtils {
    default TipCommentCompositeKey createCompositeKeyWithAllArgs() {
        return new TipCommentCompositeKey("01H5Z7XQ3W4F9K2G1V8R6T0Y5P","/1/6/2");
    }

    default TipCommentCompositeKey createCompositeKeyWithNoArgs() {
        return new TipCommentCompositeKey();
    }
}
