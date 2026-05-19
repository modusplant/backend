package kr.modusplant.domains.member.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.out.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.PostAbuseReportEntity.PostAbuseReportEntityBuilder;
import kr.modusplant.domains.post.framework.out.jpa.entity.common.util.PostEntityTestUtils;

public interface PostAbuseReportEntityTestUtils extends MemberEntityTestUtils, PostEntityTestUtils {
    default PostAbuseReportEntityBuilder createPostAbuseReportEntityBuilder() {
        return PostAbuseReportEntity.builder();
    }
}
