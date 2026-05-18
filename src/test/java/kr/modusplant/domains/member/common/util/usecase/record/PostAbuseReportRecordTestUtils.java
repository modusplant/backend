package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.PostAbuseReportRecord;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostAbuseReportRecordTestUtils {
    PostAbuseReportRecord testPostAbuseReportRecord = new PostAbuseReportRecord(MEMBER_BASIC_USER_UUID, TEST_POST_ULID);
}
