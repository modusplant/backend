package kr.modusplant.domains.member.common.util.usecase.model.read;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;

import java.util.List;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.post.common.constant.PostConstant.*;

public interface PostAbuseReportDashboardReadModelTestUtils {
    PostAbuseReportDashboardReadModel testPostAbuseReportDashboardReadModel =
            new PostAbuseReportDashboardReadModel(
                    TEST_POST_ULID,
                    TEST_POST_TITLE,
                    TEST_POST_CONTENT_JSON_NODE,
                    TEST_REPORT_SIZE,
                    AbuseReportStatus.UNCHECKED.name(),
                    TEST_POST_CREATED_AT,
                    TEST_POST_UPDATED_AT,
                    TEST_POST_UPDATED_AT,
                    MEMBER_BASIC_USER_NICKNAME);
    List<PostAbuseReportDashboardReadModel> testPostAbuseReportDashboardReadModelList =
            List.of(testPostAbuseReportDashboardReadModel);
}
