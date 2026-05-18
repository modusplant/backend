package kr.modusplant.framework.aws.service;

import kr.modusplant.framework.aws.exception.NotFoundFileKeyOnS3Exception;
import kr.modusplant.framework.aws.exception.enums.AWSErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class AmazonS3ServiceTest {
    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private AmazonS3Service amazonS3Service;

    private static final String ENDPOINT = System.getenv("DEV_PUBLIC_ENDPOINT") != null ? System.getenv("DEV_PUBLIC_ENDPOINT") : "https://test-endpoint";
    private static final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        s3Presigner = mock(S3Presigner.class);
        amazonS3Service = new AmazonS3Service(s3Client, s3Presigner);
        ReflectionTestUtils.setField(amazonS3Service, "bucket", BUCKET_NAME);
        if (System.getenv("DEV_PUBLIC_ENDPOINT") != null) {
            ReflectionTestUtils.setField(amazonS3Service, "profile", "dev");
            ReflectionTestUtils.setField(amazonS3Service, "devPublicEndpoint", ENDPOINT);
        } else {
            ReflectionTestUtils.setField(amazonS3Service, "profile", "prod");
        }
    }

    @Test
    @DisplayName("파일 업로드")
    void uploadFile_givenValidFile_returnActualRequest() throws IOException {
        // given
        MultipartFile multipartFile = mock(MultipartFile.class);
        String fileKey = "test-file-key";
        byte[] fileContent = "test-content".getBytes();

        given(multipartFile.getContentType()).willReturn("image/jpeg");
        given(multipartFile.getSize()).willReturn((long) fileContent.length);
        given(multipartFile.getBytes()).willReturn(fileContent);

        // when
        amazonS3Service.uploadFile(multipartFile, fileKey);

        // then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));
        PutObjectRequest actualRequest = requestCaptor.getValue();

        assertThat(actualRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(actualRequest.key()).isEqualTo(fileKey);
        assertThat(actualRequest.contentType()).isEqualTo("image/jpeg");
        assertThat(actualRequest.contentLength()).isEqualTo(fileContent.length);
    }

    @Test
    @DisplayName("파일 다운로드")
    void downloadFile_givenValidFile_returnFileContent() throws IOException {
        // given
        String fileKey = "test-file-key";
        byte[] fileContent = "test-download-content".getBytes();
        GetObjectResponse response = GetObjectResponse.builder()
                .contentType("application/json")
                .contentLength(100L)
                .build();
        ResponseBytes<GetObjectResponse> objectBytes = ResponseBytes.fromByteArray(response, fileContent);
        given(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).willReturn(objectBytes);

        // when
        byte[] result = amazonS3Service.downloadFile(fileKey);

        // then
        assertThat(result).isEqualTo(fileContent);
        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("파일 다운로드 시 해당하는 파일 키가 없어 오류 발생")
    void downloadFile_givenNotFoundFileKey_willThrowException() {
        // given
        String fileKey = "test-file-key";

        given(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).willThrow(NoSuchKeyException.class);

        // when
        NotFoundFileKeyOnS3Exception exception = assertThrows(NotFoundFileKeyOnS3Exception.class, () -> {
            amazonS3Service.downloadFile(fileKey);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(AWSErrorCode.NOT_FOUND_FILE_KEY_ON_S3);
    }

    @Test
    @DisplayName("파일 삭제")
    void deleteFile_givenValidFile_returnActualRequest() {
        // given
        String fileKey = "test-file-key";

        // when
        amazonS3Service.deleteFiles(fileKey);

        // then
        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(1)).deleteObject(requestCaptor.capture());

        DeleteObjectRequest actualRequest = requestCaptor.getValue();
        assertThat(actualRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(actualRequest.key()).isEqualTo(fileKey);
    }

    @Test
    @DisplayName("파일 src url 변환")
    void testGenerateS3SrcUrl_givenFileKey_willReturnS3Url() throws Exception {
        String fileKey = "test-file-key";
        String expected = ENDPOINT + "/" + BUCKET_NAME + "/" + fileKey;
        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        given(mockPresignedRequest.url()).willReturn(URI.create(expected).toURL());
        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(mockPresignedRequest);

        String result = amazonS3Service.generateS3SrcUrl(fileKey);

        assertThat(result).isEqualTo(expected);
        if (!ReflectionTestUtils.getField(amazonS3Service, "profile").equals("dev")) {
            verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
        }
    }
}
