package kr.modusplant.legacy.domains.communication.common.util.entity;

import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityConstant;

public interface CommPostLikeEntityTestUtils extends CommPostEntityTestUtils, SiteMemberEntityConstant {
    default CommPostLikeEntity createCommPostLikeEntity() {
        return CommPostLikeEntity.of(TEST_COMM_POST.getUlid(), memberBasicUserWithUuid.getUuid());
    }
}
