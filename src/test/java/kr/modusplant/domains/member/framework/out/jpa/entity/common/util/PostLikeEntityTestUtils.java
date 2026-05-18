package kr.modusplant.domains.member.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.out.jpa.entity.PostLikeEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.common.util.PostEntityTestUtils;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface PostLikeEntityTestUtils extends PostEntityTestUtils, MemberEntityTestUtils {
    default PostLikeEntity createPostLikeEntity() {
        return PostLikeEntity.of(TEST_COMM_POST_ULID, MEMBER_BASIC_USER_UUID);
    }
}
