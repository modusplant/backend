package kr.modusplant.domains.member.common.util.usecase.response;

import kr.modusplant.domains.member.usecase.response.PostAbuseReportDashboardResponse;

import static kr.modusplant.domains.member.common.util.usecase.model.read.PostAbuseReportDashboardReadModelTestUtils.testPostAbuseReportDashboardReadModelList;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostAbuseDashboardResponseTestUtils {
    PostAbuseReportDashboardResponse testPostAbuseReportDashboardResponseWithFalseHasNext =
            PostAbuseReportDashboardResponse.of(
                    testPostAbuseReportDashboardReadModelList, TEST_POST_ULID, false);

    PostAbuseReportDashboardResponse testPostAbuseReportDashboardResponseWithTrueHasNext =
            PostAbuseReportDashboardResponse.of(
                    testPostAbuseReportDashboardReadModelList, TEST_POST_ULID, true);
}
