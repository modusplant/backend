package kr.modusplant.framework.out.jpa.entity.common.util;

import kr.modusplant.framework.out.jpa.entity.CommPostBookmarkEntity;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface CommPostBookmarkEntityTestUtils extends CommPostEntityTestUtils, SiteMemberEntityTestUtils {
    default CommPostBookmarkEntity createCommPostLikeEntity() {
        return CommPostBookmarkEntity.of(TEST_COMM_POST_ULID, MEMBER_BASIC_USER_UUID);
    }
}
