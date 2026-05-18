package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.usecase.record.MemberProfileOverrideRecord;
import kr.modusplant.framework.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberImageIOHelper {
    private final AmazonS3Service amazonS3Service;

    public String uploadImage(MemberId memberId, MemberProfileOverrideRecord record) throws IOException {
        String filename = record.image().getOriginalFilename();
        String imagePath = String.format("member/%s/profile/%s", memberId.getValue(), filename);
        amazonS3Service.uploadFile(record.image(), imagePath);
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
            amazonS3Service.uploadFile(image, imagePath);
            imagePaths.add(imagePath);
        }
        return imagePaths;
    }

    public void deleteImage(MemberProfileImage image) {
        String imagePath = image.getMemberProfileImagePath().getValue();
        if (imagePath != null) {
            amazonS3Service.deleteFiles(imagePath);
        }
    }
}
