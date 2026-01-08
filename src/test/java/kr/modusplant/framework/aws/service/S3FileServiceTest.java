package kr.modusplant.framework.aws.service;

import kr.modusplant.framework.aws.exception.NotFoundFileKeyOnS3Exception;
import kr.modusplant.framework.aws.exception.enums.AWSErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class S3FileServiceTest {
    private S3Client s3Client;
    private S3FileService s3FileService;

    private static final String ENDPOINT = System.getenv("DEV_PUBLIC_ENDPOINT") != null ? System.getenv("DEV_PUBLIC_ENDPOINT") : "test-endpoint";
    private static final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        s3FileService = new S3FileService(s3Client);
        ReflectionTestUtils.setField(s3FileService, "endpoint", ENDPOINT);
        ReflectionTestUtils.setField(s3FileService, "bucket", BUCKET_NAME);
        if(System.getenv("DEV_PUBLIC_ENDPOINT") != null){
            ReflectionTestUtils.setField(s3FileService, "profile", "dev");
            ReflectionTestUtils.setField(s3FileService, "devPublicEndpoint", ENDPOINT);
        } else {
            ReflectionTestUtils.setField(s3FileService, "profile", "prod");
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
        s3FileService.uploadFile(multipartFile, fileKey);

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

        ResponseInputStream<GetObjectResponse> responseInputStream =
                new ResponseInputStream<>(GetObjectResponse.builder().build(),
                        new ByteArrayInputStream(fileContent));

        given(s3Client.getObject(any(GetObjectRequest.class))).willReturn(responseInputStream);

        // when
        byte[] result = s3FileService.downloadFile(fileKey);

        // then
        assertThat(result).isEqualTo(fileContent);
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("파일 다운로드 시 해당하는 파일 키가 없어 오류 발생")
    void downloadFile_givenNotFoundFileKey_willThrowException() {
        // given
        String fileKey = "test-file-key";

        given(s3Client.getObject(any(GetObjectRequest.class))).willThrow(NoSuchKeyException.class);

        // when
        NotFoundFileKeyOnS3Exception exception = assertThrows(NotFoundFileKeyOnS3Exception.class, () -> {
            byte[] result = s3FileService.downloadFile(fileKey);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo(AWSErrorCode.NOT_FOUND_FILE_KEY_ON_S3.getMessage());
    }

    @Test
    @DisplayName("파일 삭제")
    void deleteFile_givenValidFile_returnActualRequest() {
        // given
        String fileKey = "test-file-key";

        // when
        s3FileService.deleteFiles(fileKey);

        // then
        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(1)).deleteObject(requestCaptor.capture());

        DeleteObjectRequest actualRequest = requestCaptor.getValue();
        assertThat(actualRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(actualRequest.key()).isEqualTo(fileKey);
    }

    @Test
    @DisplayName("파일 src url 변환")
    void testGenerateS3SrcUrl_givenFileKey_willReturnS3Url() {
        String fileKey = "test-file-key";
        String expected = ENDPOINT + "/" + BUCKET_NAME + "/" +fileKey;

        String result = s3FileService.generateS3SrcUrl(fileKey);

        assertThat(result).isEqualTo(expected);
    }
}
