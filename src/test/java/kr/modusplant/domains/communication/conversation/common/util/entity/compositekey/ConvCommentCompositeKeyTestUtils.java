package kr.modusplant.domains.communication.conversation.common.util.entity.compositekey;

import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCommentEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;

public interface ConvCommentCompositeKeyTestUtils extends ConvCommentEntityTestUtils {
    default ConvCommentCompositeKey createConvCommentCompositeKey(String postUlid) {
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder().build();

        return new ConvCommentCompositeKey(postUlid,commentEntity.getContent());
    }
}
