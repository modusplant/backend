package kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostLikeEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.common.util.PostEntityTestUtils;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostLikeEntityTestUtils extends PostEntityTestUtils, MemberEntityTestUtils {
    default PostLikeEntity createPostLikeEntity() {
        return PostLikeEntity.of(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);
    }
}
