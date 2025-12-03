package kr.modusplant.framework.aws.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3FileService {
    private final S3Client s3Client;

    @Value("${cloud.wasabi.s3.endpoint}")
    private String endpoint;

    @Value("${cloud.wasabi.s3.bucket}")
    private String bucket;

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

        return s3Client.getObject(request).readAllBytes();
    }


    public void deleteFiles(String fileKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        s3Client.deleteObject(request);
    }

    public String generateS3SrcUrl(String fileKey) {
        return String.format("%s/%s/%s",endpoint,bucket,fileKey);
    }
}
