package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import kr.modusplant.domains.member.domain.vo.ReportImagePath;
import kr.modusplant.infrastructure.file.service.PendingFileService;
import kr.modusplant.shared.framework.aws.exception.NotFoundFileKeyOnS3Exception;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
