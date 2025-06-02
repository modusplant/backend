package kr.modusplant.domains.communication.conversation.common.util.entity;

import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;

public interface ConvLikeEntityTestUtils extends ConvPostEntityTestUtils, SiteMemberEntityTestUtils {
    default ConvLikeEntity createConvLikeEntity() {
        return ConvLikeEntity.of(convPost.getUlid(), memberBasicUserWithUuid.getUuid());
    }
}
