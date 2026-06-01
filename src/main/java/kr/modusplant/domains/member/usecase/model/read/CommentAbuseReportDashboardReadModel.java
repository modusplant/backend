package kr.modusplant.domains.member.usecase.model.read;

import java.time.LocalDateTime;

public record CommentAbuseReportDashboardReadModel(
        String postUlid,
        String path,
        String content,
        Integer reportCount,
        String status,
        LocalDateTime firstReportedAt,
        LocalDateTime lastReportedAt,
        LocalDateTime displayTimestamp,
        String nickname) {
}
