package kr.modusplant.shared.event.common.util;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.shared.event.PostLikeEvent;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostLikeEventTestUtils extends MemberTestUtils {
    PostLikeEvent TEST_POST_LIKE_EVENT = PostLikeEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
