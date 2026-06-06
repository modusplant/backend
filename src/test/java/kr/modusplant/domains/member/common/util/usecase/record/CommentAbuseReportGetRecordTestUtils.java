package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.usecase.record.CommentAbuseReportGetRecord;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseReportGetRecordTestUtils {
    CommentAbuseReportGetRecord testCommentAbuseReportGetRecord =
            new CommentAbuseReportGetRecord(AbuseReportStatus.UNCHECKED, TEST_POST_ULID, TEST_COMMENT_PATH, TEST_REPORT_SIZE);
}
