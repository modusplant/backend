package kr.modusplant.shared.event.common.util;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.shared.event.PostUnlikeEvent;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostUnlikeEventTestUtils extends MemberTestUtils {
    PostUnlikeEvent TEST_POST_UNLIKE_EVENT = PostUnlikeEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
