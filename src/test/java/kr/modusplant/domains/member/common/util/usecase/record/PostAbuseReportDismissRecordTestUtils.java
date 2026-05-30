package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.PostAbuseReportDismissRecord;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostAbuseReportDismissRecordTestUtils {
    PostAbuseReportDismissRecord testPostAbuseReportDismissRecord =
            new PostAbuseReportDismissRecord(TEST_POST_ULID);
}
