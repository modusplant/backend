package kr.modusplant.domains.member.common.util.usecase.response;

import kr.modusplant.domains.member.usecase.response.CommentAbuseReportDashboardResponse;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.util.usecase.model.read.CommentAbuseReportDashboardReadModelTestUtils.testCommentAbuseReportDashboardReadModelList;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseDashboardResponseTestUtils {
    CommentAbuseReportDashboardResponse testCommentAbuseReportDashboardResponseWithFalseHasNext =
            CommentAbuseReportDashboardResponse.of(
                    testCommentAbuseReportDashboardReadModelList,
                    TEST_POST_ULID, TEST_COMMENT_PATH, false);

    CommentAbuseReportDashboardResponse testCommentAbuseReportDashboardResponseWithTrueHasNext =
            CommentAbuseReportDashboardResponse.of(
                    testCommentAbuseReportDashboardReadModelList,
                    TEST_POST_ULID, TEST_COMMENT_PATH, true);
}
