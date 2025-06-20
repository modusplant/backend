package kr.modusplant.global.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class S3FileServiceTest {
    private S3Client s3Client;
    private S3FileService s3FileService;

    private static final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        s3FileService = new S3FileService(s3Client);
        ReflectionTestUtils.setField(s3FileService, "bucket", BUCKET_NAME);
    }

    @Test
    @DisplayName("파일 업로드 테스트")
    void uploadFileTest() throws IOException {
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
    @DisplayName("파일 다운로드 테스트")
    void downloadFileTest() throws IOException {
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
    void testDeleteFile() {
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
}
