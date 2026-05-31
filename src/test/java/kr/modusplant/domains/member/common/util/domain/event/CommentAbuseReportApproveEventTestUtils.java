package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.CommentAbuseReportApproveEvent;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseReportApproveEventTestUtils {
    CommentAbuseReportApproveEvent testCommentAbuseReportApproveEvent =
            CommentAbuseReportApproveEvent.create(TEST_POST_ULID, TEST_COMM_COMMENT_PATH);
}
