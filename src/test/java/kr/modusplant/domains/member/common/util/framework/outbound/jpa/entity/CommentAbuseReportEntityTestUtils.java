package kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.comment.common.util.framework.outbound.persistence.jpa.entity.CommentEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentAbuseReportEntity.CommentAbuseReportEntityBuilder;

public interface CommentAbuseReportEntityTestUtils extends MemberEntityTestUtils, CommentEntityTestUtils {
    default CommentAbuseReportEntityBuilder createCommentAbuseReportEntityBuilder() {
        return CommentAbuseReportEntity.builder();
    }
}
