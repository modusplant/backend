package kr.modusplant.domains.member.usecase.record;

import java.util.UUID;

public record ProposalOrBugReportRemoveRecord(UUID memberId, String reportUlid) {
}
