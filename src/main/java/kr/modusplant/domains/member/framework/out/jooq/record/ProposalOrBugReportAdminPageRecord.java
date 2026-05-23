package kr.modusplant.domains.member.framework.out.jooq.record;

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
        LocalDateTime displayTime,
        String email,
        String nickname,
        String status) {
}
