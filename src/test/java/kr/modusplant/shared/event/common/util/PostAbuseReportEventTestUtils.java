package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.PostAbuseReportEvent;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostAbuseReportEventTestUtils {
    PostAbuseReportEvent testPostAbuseReportEvent = PostAbuseReportEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
