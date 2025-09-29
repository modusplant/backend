package kr.modusplant.legacy.domains.communication.common.util.entity;

import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberEntityTestUtils;

public interface CommPostLikeEntityTestUtils extends CommPostEntityTestUtils, SiteMemberEntityTestUtils {
    default CommPostLikeEntity createCommPostLikeEntity() {
        return CommPostLikeEntity.of(TEST_COMM_POST.getUlid(), MEMBER_BASIC_USER_UUID);
    }
}
