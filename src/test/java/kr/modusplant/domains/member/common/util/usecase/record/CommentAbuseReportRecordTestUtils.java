package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.CommentAbuseReportRecord;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface CommentAbuseReportRecordTestUtils {
    CommentAbuseReportRecord testCommentAbuseReportRecord = new CommentAbuseReportRecord(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH);
}
