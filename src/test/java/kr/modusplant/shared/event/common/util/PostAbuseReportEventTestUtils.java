package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.PostAbuseReportEvent;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_PUBLISHED_AT;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostAbuseReportEventTestUtils {
    PostAbuseReportEvent testPostAbuseReportEvent = PostAbuseReportEvent.create(TEST_POST_ULID, TEST_POST_PUBLISHED_AT);
}
