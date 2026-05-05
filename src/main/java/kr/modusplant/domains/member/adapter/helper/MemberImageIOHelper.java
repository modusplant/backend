package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.usecase.record.MemberProfileOverrideRecord;
import kr.modusplant.framework.aws.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> uploadImage(MemberId memberId,
                                    ReportId reportId,
                                    List<MultipartFile> images) throws IOException {
        List<String> imagePaths = new ArrayList<>();
        for (MultipartFile image : images) {
            String filename = image.getOriginalFilename();
            String imagePath = String.format("member/%s/report/proposal-or-bug/%s/%s",
                    memberId.getValue(), reportId.getValue(), filename);
            s3FileService.uploadFile(image, imagePath);
            imagePaths.add(imagePath);
        }
        return imagePaths;
    }

    public void deleteImage(MemberProfileImage image) {
        String imagePath = image.getMemberProfileImagePath().getValue();
        if (imagePath != null) {
            s3FileService.deleteFiles(imagePath);
        }
    }
}
