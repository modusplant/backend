package kr.modusplant.framework.aws.service;

import kr.modusplant.framework.aws.exception.NotFoundFileKeyOnS3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3FileService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.wasabi.s3.bucket}")
    private String bucket;

    @Value("${spring.profiles.active:local}")
    private String profile;

    @Value("${minio.public-endpoint:#{null}}")
    private String devPublicEndpoint;

    public void uploadFile(MultipartFile file, String fileKey) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
    }

    public byte[] downloadFile(String fileKey) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        try {
            return s3Client.getObject(request).readAllBytes();
        } catch (NoSuchKeyException e) {
            throw new NotFoundFileKeyOnS3Exception();
        }
    }


    public void deleteFiles(String fileKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        s3Client.deleteObject(request);
    }

    public String generateS3SrcUrl(String fileKey) {
        return String.format("%s/%s/%s", endpoint, bucket, fileKey);
        if(profile.equals("dev")){
            return String.format("%s/%s/%s", devPublicEndpoint, bucket, fileKey);
        }
        return getPresignedUrl(fileKey);
    }

    private String getPresignedUrl(String fileKey) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(12))
                .getObjectRequest(req -> req
                        .bucket(bucket)
                        .key(fileKey))
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }
}
