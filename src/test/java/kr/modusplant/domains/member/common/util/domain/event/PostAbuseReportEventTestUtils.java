package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.PostAbuseReportEvent;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_PUBLISHED_AT;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostAbuseReportEventTestUtils {
    PostAbuseReportEvent testPostAbuseReportEvent = PostAbuseReportEvent.create(TEST_POST_ULID, TEST_POST_PUBLISHED_AT);
}
