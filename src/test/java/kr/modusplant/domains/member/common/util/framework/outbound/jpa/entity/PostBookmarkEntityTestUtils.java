package kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostBookmarkEntity;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostEntityTestUtils;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostBookmarkEntityTestUtils extends PostEntityTestUtils, MemberEntityTestUtils {
    default PostBookmarkEntity createPostBookmarkEntity() {
        return PostBookmarkEntity.of(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);
    }
}
