package kr.modusplant.domains.member.usecase.record;

import java.util.List;
import java.util.UUID;

public record ProposalOrBugReportImagePrepareRecord_V2(UUID memberId, List<String> filenames, List<String> contentTypes) {
}
