package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.framework.jpa.entity.CommentAbuseReportEntity.CommentAbuseReportEntityBuilder;

public interface CommentAbuseReportEntityTestUtils extends MemberEntityTestUtils, CommentEntityTestUtils {
    default CommentAbuseReportEntityBuilder createCommentAbuseReportEntityBuilder() {
        return CommentAbuseReportEntity.builder();
    }
}
