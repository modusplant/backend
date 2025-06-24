package kr.modusplant.domains.communication.tip.common.util.entity;

import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;

public interface TipLikeEntityTestUtils extends TipPostEntityTestUtils, SiteMemberEntityTestUtils {
    default TipLikeEntity createTipLikeEntity() {
        return TipLikeEntity.of(testTipPost.getUlid(), memberBasicUserWithUuid.getUuid());
    }
}
