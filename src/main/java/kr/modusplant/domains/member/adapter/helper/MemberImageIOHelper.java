package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.usecase.record.MemberProfileOverrideRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRecord;
import kr.modusplant.framework.aws.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MemberImageIOHelper {
    private final S3FileService s3FileService;

    public String uploadImage(MemberId memberId, MemberProfileOverrideRecord record) throws IOException {
        String filename = record.image().getOriginalFilename();
        String imagePath = String.format("member/%s/profile/%s", memberId.getValue(), filename);
        s3FileService.uploadFile(record.image(), imagePath);
        return imagePath;
    }

    public String uploadImage(MemberId memberId, ProposalOrBugReportRecord record) throws IOException {
        String filename = record.image().getOriginalFilename();
        String imagePath = String.format("member/%s/report/%s", memberId.getValue(), filename);
        s3FileService.uploadFile(record.image(), imagePath);
        return imagePath;
    }
}
