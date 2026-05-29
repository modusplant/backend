package kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportEntity.PostAbuseReportEntityBuilder;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostEntityTestUtils;

public interface PostAbuseReportEntityTestUtils extends MemberEntityTestUtils, PostEntityTestUtils {
    default PostAbuseReportEntityBuilder createPostAbuseReportEntityBuilder() {
        return PostAbuseReportEntity.builder();
    }
}
