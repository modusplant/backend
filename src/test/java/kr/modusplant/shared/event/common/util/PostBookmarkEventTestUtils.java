package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.PostBookmarkEvent;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostBookmarkEventTestUtils {
    PostBookmarkEvent testPostBookmarkEvent = PostBookmarkEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
