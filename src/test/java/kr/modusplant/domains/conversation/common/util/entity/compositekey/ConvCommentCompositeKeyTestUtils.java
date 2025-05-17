package kr.modusplant.domains.conversation.common.util.entity.compositekey;

import kr.modusplant.domains.conversation.common.util.app.http.request.ConvCommentRequestTestUtils;
import kr.modusplant.domains.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;

public interface ConvCommentCompositeKeyTestUtils {
    default ConvCommentCompositeKey createCompositeKeyWithAllArgs() {
        return new ConvCommentCompositeKey("01H5Z7XQ3W4F9K2G1V8R6T0Y5P","/1/6/2");
    }

    default ConvCommentCompositeKey createCompositeKeyWithNoArgs() {
        return new ConvCommentCompositeKey();
    }
}
