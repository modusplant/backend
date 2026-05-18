package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.CommentAbuseReportEvent;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseReportEventTestUtils {
    CommentAbuseReportEvent testCommentAbuseReportEvent = CommentAbuseReportEvent.create(MEMBER_BASIC_USER_UUID, TEST_POST_ULID, TEST_COMM_COMMENT_PATH);
}
