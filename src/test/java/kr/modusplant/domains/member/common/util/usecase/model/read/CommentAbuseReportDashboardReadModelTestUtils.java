package kr.modusplant.domains.member.common.util.usecase.model.read;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;

import java.util.List;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_CONTENT;
import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_CREATED_AT;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseReportDashboardReadModelTestUtils {
    CommentAbuseReportDashboardReadModel testCommentAbuseReportDashboardReadModel =
            new CommentAbuseReportDashboardReadModel(
                    TEST_POST_ULID,
                    TEST_COMMENT_PATH,
                    TEST_COMMENT_CONTENT,
                    TEST_REPORT_SIZE,
                    AbuseReportStatus.UNCHECKED.name(),
                    AbuseReportStatus.UNCHECKED.getValue(),
                    TEST_REPORT_CREATED_AT,
                    TEST_REPORT_CREATED_AT,
                    TEST_REPORT_CREATED_AT,
                    MEMBER_BASIC_USER_NICKNAME);
    List<CommentAbuseReportDashboardReadModel> testCommentAbuseReportDashboardReadModelList =
            List.of(testCommentAbuseReportDashboardReadModel);
}
