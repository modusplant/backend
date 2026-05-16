package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.PostBookmarkEntity;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface PostBookmarkEntityTestUtils extends PostEntityTestUtils, MemberEntityTestUtils {
    default PostBookmarkEntity createPostBookmarkEntity() {
        return PostBookmarkEntity.of(TEST_COMM_POST_ULID, MEMBER_BASIC_USER_UUID);
    }
}
