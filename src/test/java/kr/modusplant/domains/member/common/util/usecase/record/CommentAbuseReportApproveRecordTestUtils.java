package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.CommentAbuseReportApproveRecord;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseReportApproveRecordTestUtils {
    CommentAbuseReportApproveRecord testCommentAbuseReportApproveRecord =
            new CommentAbuseReportApproveRecord(TEST_POST_ULID, TEST_COMMENT_PATH);
}
