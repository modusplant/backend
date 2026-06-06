package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.usecase.record.PostAbuseReportGetRecord;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostAbuseReportGetRecordTestUtils {
    PostAbuseReportGetRecord testPostAbuseReportGetRecord =
            new PostAbuseReportGetRecord(AbuseReportStatus.UNCHECKED, TEST_POST_ULID, TEST_REPORT_SIZE);
}
