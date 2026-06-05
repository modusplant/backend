package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.PostAbuseReportDismissEvent;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostAbuseReportDismissEventTestUtils {
    PostAbuseReportDismissEvent testPostAbuseReportDismissEvent = PostAbuseReportDismissEvent.create(TEST_POST_ULID);
}
