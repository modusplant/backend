package kr.modusplant.domains.member.usecase.record;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record ProposalOrBugReportRecord(UUID memberId, String title, String content, MultipartFile image) {
}
