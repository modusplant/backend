package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.PostBookmarkEvent;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface PostBookmarkEventTestUtils {
    PostBookmarkEvent testPostBookmarkEvent = PostBookmarkEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
