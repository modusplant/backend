package kr.modusplant.domains.member.framework.outbound.jooq.record;

import org.jooq.JSONB;

import java.time.LocalDateTime;

public record ProposalOrBugReportAdminPageRecord(
        String ulid,
        String title,
        String content,
        JSONB image,
        Integer imageNumber,
        LocalDateTime checkedAt,
        LocalDateTime createdAt,
        LocalDateTime displayTimestamp,
        String nickname,
        String status) {
}
