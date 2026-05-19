package kr.modusplant.domains.member.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.common.util.CommentEntityTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.CommentAbuseReportEntity.CommentAbuseReportEntityBuilder;

public interface CommentAbuseReportEntityTestUtils extends MemberEntityTestUtils, CommentEntityTestUtils {
    default CommentAbuseReportEntityBuilder createCommentAbuseReportEntityBuilder() {
        return CommentAbuseReportEntity.builder();
    }
}
