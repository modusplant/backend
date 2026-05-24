package kr.modusplant.domains.member.usecase.record;

import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;

public record ProposalOrBugReportGetRecord(ProposalOrBugReportStatus status, String lastReportUlid, Integer size) {
}
