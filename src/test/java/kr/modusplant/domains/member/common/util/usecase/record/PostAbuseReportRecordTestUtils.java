package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.PostAbuseReportRecord;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;

public interface PostAbuseReportRecordTestUtils {
    PostAbuseReportRecord testPostAbuseReportRecord = new PostAbuseReportRecord(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN, TEST_COMM_POST_ULID);
}
