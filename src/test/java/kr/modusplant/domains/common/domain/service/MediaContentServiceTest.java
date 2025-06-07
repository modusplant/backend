package kr.modusplant.domains.common.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipPostRequestTestUtils;
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
import java.util.Base64;
import java.util.List;

import static kr.modusplant.global.vo.CamelCaseWord.DATA;
import static kr.modusplant.global.vo.CamelCaseWord.ORDER;
import static kr.modusplant.global.vo.FileSystem.FILENAME;
import static kr.modusplant.global.vo.FileSystem.SRC;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MediaContentServiceTest implements TipPostRequestTestUtils {
    @Autowired
    private MediaContentService mediaContentService;

    @Test
    @DisplayName("멀티파트 데이터를 파일에 저장하고 Json 반환값 받기")
    void saveFilesAndGenerateContentJsonTestSuccess() throws IOException {
        // given & when
        JsonNode result = mediaContentService.saveFilesAndGenerateContentJson(allMediaFiles);

        // then
        assertThat(result.isArray()).isTrue();
        assertThat(result.size()).isEqualTo(5);

        JsonNode textNode = result.get(0);
        assertThat(textNode.get(ORDER).asInt()).isEqualTo(1);
        assertThat(textNode.get("type").asText()).isEqualTo("text");
        assertThat(textNode.get(FILENAME).asText()).isEqualTo(allMediaFiles.getFirst().getOriginalFilename());
        assertThat(textNode.get(DATA).asText()).isEqualTo(new String(allMediaFiles.getFirst().getBytes(), StandardCharsets.UTF_8));

        JsonNode imageNode = result.get(1);
        File savedImage = new File(imageNode.get(SRC).asText());
        assertThat(imageNode.get(ORDER).asInt()).isEqualTo(2);
        assertThat(imageNode.get("type").asText()).isEqualTo("image");
        assertThat(imageNode.get(FILENAME).asText()).isEqualTo(allMediaFiles.get(1).getOriginalFilename());
        assertThat(imageNode.get(SRC).asText()).contains("uploads/images/image_");
        assertTrue(savedImage.exists());

        JsonNode videoNode = result.get(2);
        File savedVideo = new File(imageNode.get(SRC).asText());
        assertThat(videoNode.get(ORDER).asInt()).isEqualTo(3);
        assertThat(videoNode.get("type").asText()).isEqualTo("video");
        assertThat(videoNode.get(FILENAME).asText()).isEqualTo(allMediaFiles.get(2).getOriginalFilename());
        assertThat(videoNode.get(SRC).asText()).contains("uploads/video/video_");
        assertTrue(savedVideo.exists());

        JsonNode audioNode = result.get(3);
        File savedAudio = new File(imageNode.get(SRC).asText());
        assertThat(audioNode.get(ORDER).asInt()).isEqualTo(4);
        assertThat(audioNode.get("type").asText()).isEqualTo("audio");
        assertThat(audioNode.get(FILENAME).asText()).isEqualTo(allMediaFiles.get(3).getOriginalFilename());
        assertThat(audioNode.get(SRC).asText()).contains("uploads/audio/audio_");
        assertTrue(savedAudio.exists());

        JsonNode fileNode = result.get(4);
        File savedFile = new File(imageNode.get(SRC).asText());
        assertThat(fileNode.get(ORDER).asInt()).isEqualTo(5);
        assertThat(fileNode.get("type").asText()).isEqualTo("file");
        assertThat(fileNode.get(FILENAME).asText()).isEqualTo(allMediaFiles.get(4).getOriginalFilename());
        assertThat(fileNode.get(SRC).asText()).contains("uploads/files/file_");
        assertTrue(savedFile.exists());

        mediaContentService.deleteFiles(result);
    }

    @Test
    @DisplayName("지원하지 않는 멀티파트 데이터 저장 시, 예외 발생")
    void saveFilesAndGenerateContentJsonTestFail() {
        // given
        MultipartFile fontFile = new MockMultipartFile("content","font_0.ttf","font/ttf",new byte[] {1,2,3});
        List<MultipartFile> fontFiles = List.of(fontFile);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mediaContentService.saveFilesAndGenerateContentJson(fontFiles));
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
        assertFalse(firstNode.has(SRC));
        assertTrue(firstNode.has(DATA));
        assertThat(firstNode.get(DATA).asText()).isEqualTo(Base64.getEncoder().encodeToString(jpegData));

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
            if (node.has(SRC)) {
                String src = node.get(SRC).asText();
                Path path = Path.of(src);
                assertThat(Files.exists(path)).isEqualTo(false);
            }
        }
    }


}