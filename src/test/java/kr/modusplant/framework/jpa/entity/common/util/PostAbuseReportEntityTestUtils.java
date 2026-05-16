package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.framework.jpa.entity.PostAbuseReportEntity.PostAbuseReportEntityBuilder;

public interface PostAbuseReportEntityTestUtils extends MemberEntityTestUtils, PostEntityTestUtils {
    default PostAbuseReportEntityBuilder createPostAbuseReportEntityBuilder() {
        return PostAbuseReportEntity.builder();
    }
}
