package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.PostUnlikeEvent;

import static kr.modusplant.shared.persistence.common.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostUnlikeEventTestUtils {
    PostUnlikeEvent testPostUnlikeEvent = PostUnlikeEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
