package kr.modusplant.domains.member.common.util.framework.outbound.jooq.record;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.framework.outbound.jooq.record.PostAbuseReportDashboardRecord;

import java.util.List;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.post.common.constant.PostConstant.*;

public interface PostAbuseReportDashboardRecordTestUtils {
    PostAbuseReportDashboardRecord testPostAbuseReportDashboardRecord =
            new PostAbuseReportDashboardRecord(
                    TEST_POST_ULID,
                    TEST_POST_TITLE,
                    TEST_POST_CONTENT_JSON_NODE,
                    TEST_REPORT_SIZE,
                    AbuseReportStatus.UNCHECKED.name(),
                    TEST_POST_CREATED_AT,
                    TEST_POST_UPDATED_AT,
                    TEST_POST_UPDATED_AT,
                    MEMBER_BASIC_USER_NICKNAME);
    List<PostAbuseReportDashboardRecord> testPostAbuseReportDashboardRecordList =
            List.of(testPostAbuseReportDashboardRecord);
}
