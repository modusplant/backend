package kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostLikeEntity;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostEntityTestUtils;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostLikeEntityTestUtils extends PostEntityTestUtils, MemberEntityTestUtils {
    default PostLikeEntity createPostLikeEntity() {
        return PostLikeEntity.of(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);
    }
}
