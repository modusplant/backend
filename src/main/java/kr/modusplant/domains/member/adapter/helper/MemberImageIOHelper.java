package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportImagePath;
import kr.modusplant.domains.member.usecase.record.MemberProfileOverrideRecord_V1;
import kr.modusplant.infrastructure.file.service.PendingFileService;
import kr.modusplant.shared.framework.aws.exception.NotFoundFileKeyOnS3Exception;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static kr.modusplant.domains.member.domain.vo.MemberProfileImagePath.MEMBER_PROFILE_IMAGE_PATH_FORMAT;
import static kr.modusplant.domains.member.domain.vo.ReportImagePath.PROPOSAL_OR_BUG_REPORT_IMAGE_PATH_FORMAT;

@Component
@RequiredArgsConstructor
public class MemberImageIOHelper {
    private final AmazonS3Service amazonS3Service;
    private final PendingFileService pendingFileService;

    public String issueStorageUrl(MemberProfileImagePath imagePath, String contentType) {
        String fileKey = imagePath.getValue();
        if (fileKey != null) {
            String storageUrl = amazonS3Service.generatePutPresignedUrl(fileKey, contentType);
            pendingFileService.trackPendingFiles(List.of(fileKey));
            return storageUrl;
        } else {
            return null;
        }
    }

    public String issueStorageUrl(ReportImagePath imagePath, String contentType) {
        String fileKey = imagePath.getValue();
        if (fileKey != null) {
            String storageUrl = amazonS3Service.generatePutPresignedUrl(fileKey, contentType);
            pendingFileService.trackPendingFiles(List.of(fileKey));
            return storageUrl;
        } else {
            return null;
        }
    }

    public String uploadImage(MemberId memberId, MemberProfileOverrideRecord_V1 record) throws IOException {
        MultipartFile image = record.image();
        if (image != null) {
            String filename = image.getOriginalFilename();
            String imagePath = String.format(MEMBER_PROFILE_IMAGE_PATH_FORMAT, memberId.getValue(), filename);
            amazonS3Service.uploadFile(image, imagePath);
            return imagePath;
        } else {
            return null;
        }
    }

    public List<String> uploadImage(MemberId memberId,
                                    ReportId reportId,
                                    List<MultipartFile> images) throws IOException {
        List<String> imagePaths = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image != null) {
                String filename = image.getOriginalFilename();
                String imagePath = String.format(
                        PROPOSAL_OR_BUG_REPORT_IMAGE_PATH_FORMAT, memberId.getValue(), reportId.getValue(), filename);
                amazonS3Service.uploadFile(image, imagePath);
                imagePaths.add(imagePath);
            }
        }
        return imagePaths;
    }

    public void deleteImage(MemberProfileImage image) {
        String imagePath = image.getMemberProfileImagePath().getValue();
        if (imagePath != null) {
            amazonS3Service.deleteFile(imagePath);
        }
    }

    public void validateIfImageExistsInStorage(MemberProfileImagePath memberProfileImagePath) {
        String imagePath = memberProfileImagePath.getValue();
        if (imagePath != null && !amazonS3Service.checkIfFileExists(imagePath)) {
            throw new NotFoundFileKeyOnS3Exception();
        }
    }
}
