package kr.modusplant.domains.member.usecase.record;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record ProposalOrBugReportRecord(
        UUID memberId, String title, String content, List<MultipartFile> images, Integer imageNumber) {
}
