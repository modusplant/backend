package kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostBookmarkEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.common.util.PostEntityTestUtils;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostBookmarkEntityTestUtils extends PostEntityTestUtils, MemberEntityTestUtils {
    default PostBookmarkEntity createPostBookmarkEntity() {
        return PostBookmarkEntity.of(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);
    }
}
