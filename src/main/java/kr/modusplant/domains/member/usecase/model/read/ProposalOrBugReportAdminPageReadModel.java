package kr.modusplant.domains.member.usecase.model.read;

import java.time.LocalDateTime;
import java.util.List;

public record ProposalOrBugReportAdminPageReadModel(
        String ulid,
        String title,
        String content,
        List<byte[]> image,
        LocalDateTime checkedAt,
        LocalDateTime createdAt,
        LocalDateTime displayTimestamp,
        String nickname,
        String status) {
}
