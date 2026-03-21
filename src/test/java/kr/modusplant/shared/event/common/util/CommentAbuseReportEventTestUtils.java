package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.CommentAbuseReportEvent;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface CommentAbuseReportEventTestUtils {
    CommentAbuseReportEvent testCommentAbuseReportEvent = CommentAbuseReportEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH);
}
