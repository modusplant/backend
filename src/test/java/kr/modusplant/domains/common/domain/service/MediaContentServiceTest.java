package kr.modusplant.domains.common.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.communication.tip.common.util.http.request.TipPostRequestTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MediaContentServiceTest implements TipPostRequestTestUtils {
    @Autowired
    private MediaContentService mediaContentService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("멀티파트 데이터를 파일에 저장하고 Json 반환값 받기")
    void saveFilesAndGenerateContentJsonTestSuccess() throws IOException {
        // given & when
        JsonNode result = mediaContentService.saveFilesAndGenerateContentJson(allMediaFiles);

        // then
        assertThat(result.isArray()).isTrue();
        assertThat(result.size()).isEqualTo(5);

        JsonNode textNode = result.get(0);
        assertThat(textNode.get("order").asInt()).isEqualTo(1);
        assertThat(textNode.get("type").asText()).isEqualTo("text");
        assertThat(textNode.get("filename").asText()).isEqualTo(allMediaFiles.get(0).getOriginalFilename());
        assertThat(textNode.get("data").asText()).isEqualTo(new String(allMediaFiles.get(0).getBytes(), StandardCharsets.UTF_8));

        JsonNode imageNode = result.get(1);
        File savedImage = new File(imageNode.get("src").asText());
        assertThat(imageNode.get("order").asInt()).isEqualTo(2);
        assertThat(imageNode.get("type").asText()).isEqualTo("image");
        assertThat(imageNode.get("filename").asText()).isEqualTo(allMediaFiles.get(1).getOriginalFilename());
        assertThat(imageNode.get("src").asText()).contains("uploads/images/image_");
        assertTrue(savedImage.exists());

        JsonNode videoNode = result.get(2);
        File savedVideo = new File(imageNode.get("src").asText());
        assertThat(videoNode.get("order").asInt()).isEqualTo(3);
        assertThat(videoNode.get("type").asText()).isEqualTo("video");
        assertThat(videoNode.get("filename").asText()).isEqualTo(allMediaFiles.get(2).getOriginalFilename());
        assertThat(videoNode.get("src").asText()).contains("uploads/video/video_");
        assertTrue(savedVideo.exists());

        JsonNode audioNode = result.get(3);
        File savedAudio = new File(imageNode.get("src").asText());
        assertThat(audioNode.get("order").asInt()).isEqualTo(4);
        assertThat(audioNode.get("type").asText()).isEqualTo("audio");
        assertThat(audioNode.get("filename").asText()).isEqualTo(allMediaFiles.get(3).getOriginalFilename());
        assertThat(audioNode.get("src").asText()).contains("uploads/audio/audio_");
        assertTrue(savedAudio.exists());

        JsonNode fileNode = result.get(4);
        File savedFile = new File(imageNode.get("src").asText());
        assertThat(fileNode.get("order").asInt()).isEqualTo(5);
        assertThat(fileNode.get("type").asText()).isEqualTo("file");
        assertThat(fileNode.get("filename").asText()).isEqualTo(allMediaFiles.get(4).getOriginalFilename());
        assertThat(fileNode.get("src").asText()).contains("uploads/files/file_");
        assertTrue(savedFile.exists());

        mediaContentService.deleteFiles(result);
    }

    @Test
    @DisplayName("지원하지 않는 멀티파트 데이터 저장 시, 예외 발생")
    void saveFilesAndGenerateContentJsonTestFail() throws IOException {
        // given
        MultipartFile fontFile = new MockMultipartFile("content","font_0.ttf","font/ttf",new byte[] {1,2,3});
        List<MultipartFile> fontFiles = Arrays.asList(fontFile);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mediaContentService.saveFilesAndGenerateContentJson(fontFiles);
        });
        assertThat(exception.getMessage()).isEqualTo("Unsupported file type: font/ttf");
    }

    @Test
    @DisplayName("저장된 파일 경로로 파일 바이너리 데이터 읽기")
    void convertFileSrcToBinaryDataTest() throws IOException {
        // given
        List<MultipartFile> imageFiles = List.of(imageFile);
        JsonNode content = mediaContentService.saveFilesAndGenerateContentJson(imageFiles);

        // when
        JsonNode result = mediaContentService.convertFileSrcToBinaryData(content);

        // then
        assertTrue(result.isArray());
        assertThat(result.size()).isEqualTo(1);
        JsonNode firstNode = result.get(0);
        assertFalse(firstNode.has("src"));
        assertTrue(firstNode.has("data"));
        assertThat(firstNode.get("data").asText()).isEqualTo(Base64.getEncoder().encodeToString(jpegData));

        mediaContentService.deleteFiles(content);
    }

    @Test
    @DisplayName("저장된 파일 경로로 로컬 파일 삭제")
    void deleteMediafilesTest() throws IOException {
        // given
        JsonNode contentJson = mediaContentService.saveFilesAndGenerateContentJson(allMediaFiles);

        // when
        mediaContentService.deleteFiles(contentJson);

        // then
        for (JsonNode node : contentJson) {
            if (node.has("src")) {
                String src = node.get("src").asText();
                Path path = Path.of(src);
                assertThat(Files.exists(path)).isEqualTo(false);
            }
        }
    }


}