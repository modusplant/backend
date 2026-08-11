package kr.modusplant.domains.member.usecase.record;

import java.util.List;
import java.util.UUID;

public record ProposalOrBugReportRecord_V2(UUID memberId, String title, String content, List<String> fileKeys) {
}
