package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.PostUnlikeEvent;
import kr.modusplant.shared.persistence.common.util.constant.CommPostConstant;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostUnlikeEventTestUtils {
    PostUnlikeEvent testPostUnlikeEvent = PostUnlikeEvent.create(MEMBER_BASIC_USER_UUID, CommPostConstant.TEST_COMM_POST_ULID);
}
