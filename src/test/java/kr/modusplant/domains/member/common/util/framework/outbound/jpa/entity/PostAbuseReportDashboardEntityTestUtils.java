package kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportDashboardEntity;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostEntityTestUtils;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_CREATED_AT;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_DISMISSED_AT;
import static kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportDashboardEntity.PostAbuseReportDashboardEntityBuilder;

public interface PostAbuseReportDashboardEntityTestUtils extends PostEntityTestUtils {
    default PostAbuseReportDashboardEntityBuilder createPostAbuseReportDashboardUncheckedEntityBuilder() {
        return PostAbuseReportDashboardEntity.builder()
                .status(AbuseReportStatus.UNCHECKED)
                .firstReportedAt(TEST_REPORT_CREATED_AT)
                .lastReportedAt(TEST_REPORT_CREATED_AT);
    }

    default PostAbuseReportDashboardEntityBuilder createPostAbuseReportDashboardDismissedEntityBuilder() {
        return PostAbuseReportDashboardEntity.builder()
                .status(AbuseReportStatus.DISMISSED)
                .firstReportedAt(TEST_REPORT_CREATED_AT)
                .lastReportedAt(TEST_REPORT_DISMISSED_AT);
    }
}
