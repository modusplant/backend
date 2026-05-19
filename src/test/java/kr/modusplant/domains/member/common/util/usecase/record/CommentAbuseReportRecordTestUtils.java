package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.CommentAbuseReportRecord;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseReportRecordTestUtils {
    CommentAbuseReportRecord testCommentAbuseReportRecord = new CommentAbuseReportRecord(MEMBER_BASIC_USER_UUID, TEST_POST_ULID, TEST_COMM_COMMENT_PATH);
}
