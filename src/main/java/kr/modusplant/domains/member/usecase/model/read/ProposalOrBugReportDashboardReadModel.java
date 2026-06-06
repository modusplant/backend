package kr.modusplant.domains.member.usecase.model.read;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public record ProposalOrBugReportDashboardReadModel(
        String ulid,
        String title,
        String content,
        JsonNode image,
        LocalDateTime checkedAt,
        LocalDateTime createdAt,
        LocalDateTime displayTimestamp,
        String nickname,
        String status,
        String statusValue) {
}
