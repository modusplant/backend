package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.CommentAbuseReportEvent;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_CREATED_AT;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseReportEventTestUtils {
    CommentAbuseReportEvent testCommentAbuseReportEvent = CommentAbuseReportEvent.create(TEST_POST_ULID, TEST_COMMENT_PATH, TEST_POST_CREATED_AT);
}
