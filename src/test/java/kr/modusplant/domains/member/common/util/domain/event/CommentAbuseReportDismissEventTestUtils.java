package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.CommentAbuseReportDismissEvent;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseReportDismissEventTestUtils {
    CommentAbuseReportDismissEvent testCommentAbuseReportDismissEvent =
            CommentAbuseReportDismissEvent.create(TEST_POST_ULID, TEST_COMMENT_PATH);
}
