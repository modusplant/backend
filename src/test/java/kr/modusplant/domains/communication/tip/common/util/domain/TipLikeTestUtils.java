package kr.modusplant.domains.communication.tip.common.util.domain;

import kr.modusplant.domains.communication.tip.domain.model.TipLike;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;

public interface TipLikeTestUtils extends TipPostTestUtils, SiteMemberTestUtils {
    TipLike tipLike = TipLike.builder()
            .tipPostId(tipPostWithUlid.getUlid())
            .memberId(memberBasicUserWithUuid.getUuid())
            .build();
}
