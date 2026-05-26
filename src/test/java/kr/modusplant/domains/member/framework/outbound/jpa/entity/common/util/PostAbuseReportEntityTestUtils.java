package kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportEntity.PostAbuseReportEntityBuilder;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.common.util.PostEntityTestUtils;

public interface PostAbuseReportEntityTestUtils extends MemberEntityTestUtils, PostEntityTestUtils {
    default PostAbuseReportEntityBuilder createPostAbuseReportEntityBuilder() {
        return PostAbuseReportEntity.builder();
    }
}
