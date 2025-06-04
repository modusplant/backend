package kr.modusplant.domains.communication.conversation.common.util.domain;

import kr.modusplant.domains.communication.conversation.domain.model.ConvLike;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;

public interface ConvLikeTestUtils extends ConvPostTestUtils, SiteMemberTestUtils {
    ConvLike convLike = ConvLike.builder()
            .convPostId(convPostWithUlid.getUlid())
            .memberId(memberBasicUserWithUuid.getUuid())
            .build();
}
