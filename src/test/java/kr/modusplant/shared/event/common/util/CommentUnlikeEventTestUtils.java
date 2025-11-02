package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.CommentUnlikeEvent;

import static kr.modusplant.shared.persistence.common.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface CommentUnlikeEventTestUtils {
    CommentUnlikeEvent testCommentUnlikeEvent = CommentUnlikeEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH);
}
