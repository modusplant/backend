package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.PostBookmarkCancelEvent;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostCancelPostBookmarkEventTestUtils {
    PostBookmarkCancelEvent testPostBookmarkCancelEvent = PostBookmarkCancelEvent.create(MEMBER_BASIC_USER_UUID, TEST_POST_ULID);
}
