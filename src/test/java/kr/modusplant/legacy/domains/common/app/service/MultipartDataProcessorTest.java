package kr.modusplant.legacy.domains.common.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.framework.outbound.cloud.service.S3FileService;
import kr.modusplant.legacy.domains.common.enums.FileType;
import kr.modusplant.legacy.domains.common.error.UnsupportedFileException;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommPostRequestTestUtils;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static kr.modusplant.global.vo.CamelCaseWord.*;
import static kr.modusplant.global.vo.FileSystem.FILENAME;
import static kr.modusplant.global.vo.FileSystem.SRC;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultipartDataProcessorTest implements CommPostRequestTestUtils {
    @Mock
    private S3FileService s3FileService;

    @InjectMocks
    private MultipartDataProcessor multipartDataProcessor;

    @Test
    @DisplayName("멀티파트 데이터를 저장하고 Json 반환값 받기")
    void saveFilesAndGenerateContentJsonTestSuccess() throws IOException {
        // given
        String regex = "post/[a-zA-Z0-9]{26}/";
        doNothing().when(s3FileService).uploadFile(eq(imageFile), anyString());
        doNothing().when(s3FileService).uploadFile(eq(videoFile), anyString());
        doNothing().when(s3FileService).uploadFile(eq(audioFile), anyString());
        doNothing().when(s3FileService).uploadFile(eq(applicationFile), anyString());

        // when
        JsonNode result = multipartDataProcessor.saveFilesAndGenerateContentJson(allMediaFiles);

        // then
        assertThat(result.size()).isEqualTo(allMediaFiles.size());

        JsonNode textNode = result.get(0);
        assertThat(textNode.get(ORDER).asInt()).isEqualTo(1);
        assertThat(textNode.get(TYPE).asText()).isEqualTo(FileType.TEXT.getValue());
        assertThat(textNode.get(FILENAME).asText()).isEqualTo(textFile0.getOriginalFilename());
        assertThat(textNode.get(DATA).asText()).isEqualTo(new String(textFile0.getBytes(), StandardCharsets.UTF_8));

        JsonNode imageNode = result.get(1);
        assertThat(imageNode.get(ORDER).asInt()).isEqualTo(2);
        assertThat(imageNode.get(TYPE).asText()).isEqualTo(FileType.IMAGE.getValue());
        assertThat(imageNode.get(FILENAME).asText()).isEqualTo(imageFile.getOriginalFilename());
        assertThat(imageNode.get(SRC).asText()).matches(regex+FileType.IMAGE.getValue()+"/.*");

        JsonNode videoNode = result.get(2);
        assertThat(videoNode.get(ORDER).asInt()).isEqualTo(3);
        assertThat(videoNode.get(TYPE).asText()).isEqualTo(FileType.VIDEO.getValue());
        assertThat(videoNode.get(FILENAME).asText()).isEqualTo(videoFile.getOriginalFilename());
        assertThat(videoNode.get(SRC).asText()).matches(regex+FileType.VIDEO.getValue()+"/.*");

        JsonNode audioNode = result.get(3);
        assertThat(audioNode.get(ORDER).asInt()).isEqualTo(4);
        assertThat(audioNode.get(TYPE).asText()).isEqualTo(FileType.AUDIO.getValue());
        assertThat(audioNode.get(FILENAME).asText()).isEqualTo(audioFile.getOriginalFilename());
        assertThat(audioNode.get(SRC).asText()).matches(regex+FileType.AUDIO.getValue()+"/.*");

        JsonNode fileNode = result.get(4);
        assertThat(fileNode.get(ORDER).asInt()).isEqualTo(5);
        assertThat(fileNode.get(TYPE).asText()).isEqualTo(FileType.FILE.getValue());
        assertThat(fileNode.get(FILENAME).asText()).isEqualTo(applicationFile.getOriginalFilename());
        assertThat(fileNode.get(SRC).asText()).matches(regex+FileType.FILE.getValue()+"/.*");
    }

    @Test
    @DisplayName("지원하지 않는 멀티파트 데이터 저장 시, 예외 발생")
    void saveFilesAndGenerateContentJsonTestFail() {
        // given
        MultipartFile fontFile = new MockMultipartFile("content","font_0.ttf","font/ttf",new byte[] {1,2,3});
        List<MultipartFile> fontFiles = List.of(fontFile);

        // when
        UnsupportedFileException exception = assertThrows(UnsupportedFileException.class,
                () -> multipartDataProcessor.saveFilesAndGenerateContentJson(fontFiles));
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.UNSUPPORTED_FILE.getMessage());
    }

    @Test
    @DisplayName("저장된 파일 경로로 파일 바이너리 데이터 읽기")
    void convertFileSrcToBinaryDataTest() throws IOException {
        // given
        List<MultipartFile> imageFiles = List.of(imageFile);
        JsonNode content = multipartDataProcessor.saveFilesAndGenerateContentJson(imageFiles);
        given(s3FileService.downloadFile(content.get(0).get(SRC).asText())).willReturn(jpegData);

        // when
        JsonNode result = multipartDataProcessor.convertFileSrcToBinaryData(content);

        // then
        assertTrue(result.isArray());
        assertThat(result.size()).isEqualTo(1);

        JsonNode imageNode = result.get(0);
        assertFalse(imageNode.has(SRC));
        assertThat(imageNode.get(TYPE).asText()).isEqualTo(FileType.IMAGE.getValue());
        assertTrue(imageNode.has(DATA));
        assertThat(imageNode.get(DATA).asText()).isEqualTo(Base64.getEncoder().encodeToString(jpegData));
    }

    @Test
    @DisplayName("저장된 파일 경로로 파일 삭제")
    void deleteFilesTest() throws IOException {
        // given
        JsonNode content = multipartDataProcessor.saveFilesAndGenerateContentJson(textImageFiles);
        doNothing().when(s3FileService).deleteFiles(content.get(1).get(SRC).asText());

        // when
        multipartDataProcessor.deleteFiles(content);

        // then
        verify(s3FileService,times(1)).deleteFiles(content.get(1).get(SRC).asText());
    }
}
