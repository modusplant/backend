package kr.modusplant.domains.member.usecase.record;

import org.springframework.web.multipart.MultipartFile;

public record ProposalOrBugReportRecord(String accessToken, String title, String content, MultipartFile image) {
}
