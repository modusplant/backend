package kr.modusplant.domains.member.usecase.model.read;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public record PostAbuseReportDashboardReadModel(
        String ulid,
        String title,
        JsonNode content,
        Integer reportCount,
        String status,
        LocalDateTime firstReportedAt,
        LocalDateTime lastReportedAt,
        LocalDateTime displayTimestamp,
        String nickname) {
}