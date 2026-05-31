package kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentAbuseReportDashboardEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentAbuseReportDashboardEntity.CommentAbuseReportDashboardEntityBuilder;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.ReportConstant.*;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentAbuseReportDashboardEntityTestUtils {
    default CommentAbuseReportDashboardEntityBuilder createCommentAbuseReportDashboardUncheckedEntityBuilder() {
        return CommentAbuseReportDashboardEntity.builder()
                .postUlid(TEST_POST_ULID)
                .path(TEST_COMM_COMMENT_PATH)
                .status(AbuseReportStatus.UNCHECKED)
                .firstReportedAt(TEST_REPORT_CREATED_AT)
                .lastReportedAt(TEST_REPORT_CREATED_AT);
    }

    default CommentAbuseReportDashboardEntityBuilder createCommentAbuseReportDashboardDismissedEntityBuilder() {
        return CommentAbuseReportDashboardEntity.builder()
                .postUlid(TEST_POST_ULID)
                .path(TEST_COMM_COMMENT_PATH)
                .status(AbuseReportStatus.DISMISSED)
                .firstReportedAt(TEST_REPORT_CREATED_AT)
                .lastReportedAt(TEST_REPORT_DISMISSED_AT);
    }

    default CommentAbuseReportDashboardEntityBuilder createCommentAbuseReportDashboardBlindedEntityBuilder() {
        return CommentAbuseReportDashboardEntity.builder()
                .postUlid(TEST_POST_ULID)
                .path(TEST_COMM_COMMENT_PATH)
                .status(AbuseReportStatus.BLINDED)
                .firstReportedAt(TEST_REPORT_CREATED_AT)
                .lastReportedAt(TEST_REPORT_BLINDED_AT);
    }
}
