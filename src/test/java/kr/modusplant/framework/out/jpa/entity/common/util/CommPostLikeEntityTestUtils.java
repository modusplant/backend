package kr.modusplant.framework.out.jpa.entity.common.util;

import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;

import static kr.modusplant.framework.out.jpa.entity.common.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.framework.out.jpa.entity.common.constant.SiteMemberEntityConstant.MEMBER_BASIC_USER_UUID;

public interface CommPostLikeEntityTestUtils extends CommPostEntityTestUtils, SiteMemberEntityTestUtils {
    default CommPostLikeEntity createCommPostLikeEntity() {
        return CommPostLikeEntity.of(TEST_COMM_POST_ULID, MEMBER_BASIC_USER_UUID);
    }
}
