package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.CommentUnlikeEvent;

import static kr.modusplant.shared.persistence.common.util.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface CommentUnlikeEventTestUtils {
    CommentUnlikeEvent testCommentUnlikeEvent = CommentUnlikeEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH);
}
