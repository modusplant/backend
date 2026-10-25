package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.PostAbuseReportApproveEvent;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostAbuseReportApproveEventTestUtils {
    PostAbuseReportApproveEvent testPostAbuseReportApproveEvent = PostAbuseReportApproveEvent.create(TEST_POST_ULID, MEMBER_BASIC_ADMIN_UUID);
}
