package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.CommentAbuseReportEvent;

import static kr.modusplant.shared.persistence.common.util.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface CommentAbuseReportEventTestUtils {
    CommentAbuseReportEvent testCommentAbuseReportEvent = CommentAbuseReportEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH);
}
