package kr.modusplant.domains.member.framework.outbound.jooq.record;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public record PostAbuseReportDashboardRecord(
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
