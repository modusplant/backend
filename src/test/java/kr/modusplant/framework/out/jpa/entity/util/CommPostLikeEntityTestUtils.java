package kr.modusplant.framework.out.jpa.entity.util;

import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;

import static kr.modusplant.framework.out.jpa.entity.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.framework.out.jpa.entity.constant.SiteMemberEntityConstant.MEMBER_BASIC_USER_UUID;

public interface CommPostLikeEntityTestUtils extends CommPostEntityTestUtils, SiteMemberEntityTestUtils {
    default CommPostLikeEntity createCommPostLikeEntity() {
        return CommPostLikeEntity.of(TEST_COMM_POST_ULID, MEMBER_BASIC_USER_UUID);
    }
}
