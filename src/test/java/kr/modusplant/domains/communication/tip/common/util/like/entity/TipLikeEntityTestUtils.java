package kr.modusplant.domains.communication.tip.common.util.like.entity;

import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.like.entity.TipLikeEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;

public interface TipLikeEntityTestUtils extends TipPostEntityTestUtils, SiteMemberEntityTestUtils {
    default TipLikeEntity createTipLikeEntity() {
        return TipLikeEntity.of(tipPost.getUlid(), memberBasicUserWithUuid.getUuid());
    }
}
