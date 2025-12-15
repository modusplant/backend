package kr.modusplant.domains.post.framework.out.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.post.common.util.usecase.request.PostRequestTestUtils;
import kr.modusplant.domains.post.framework.out.processor.enums.FileType;
import kr.modusplant.domains.post.framework.out.processor.exception.FileLimitExceededException;
import kr.modusplant.domains.post.framework.out.processor.exception.InvalidFileInputException;
import kr.modusplant.domains.post.framework.out.processor.exception.UnsupportedFileException;
import kr.modusplant.domains.post.usecase.request.FileOrder;
import kr.modusplant.framework.aws.service.S3FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultipartDataProcessorTest implements PostRequestTestUtils {
    @Mock
    private S3FileService s3FileService;

    @InjectMocks
    private MultipartDataProcessor multipartDataProcessor;

    private static final String DATA = "data";
    private static final String FILENAME = "filename";
    private static final String ORDER = "order";
    private static final String SRC = "src";
    private static final String TYPE = "type";
    private static final String BASIC_PATH = "https://mocked-url.com/mock-bucket/";
    private static final String FILE_KEY = "post/test-ulid/image/test-file.jpg";

    @Nested
    @DisplayName("saveFilesAndGenerateContentJson 메서드")
    class testSaveFilesAndGenerateContentJson {

        @Test
        @DisplayName("텍스트와 이미지 파일을 순서대로 처리")
        void testSaveFilesAndGenerateContentJson_givenMultipartFilesAndOrderInfoList_willReturnJsonContentWithCorrectOrder() throws Exception {
            // given
            String regex = "post/[a-zA-Z0-9]{26}/";
            doNothing().when(s3FileService).uploadFile(any(MultipartFile.class),anyString());

            // when
            JsonNode result = multipartDataProcessor.saveFilesAndGenerateContentJson(basicMediaFiles,basicMediaFilesOrder);

            // then
            assertThat(result.size()).isEqualTo(basicMediaFiles.size());

            JsonNode textNode = result.get(0);
            assertThat(textNode.get(ORDER).asInt()).isEqualTo(0);
            assertThat(textNode.get(TYPE).asText()).isEqualTo(FileType.TEXT.getValue());
            assertThat(textNode.get(FILENAME).asText()).isEqualTo(textFile0.getOriginalFilename());
            assertThat(textNode.get(DATA).asText()).isEqualTo(new String(textFile0.getBytes(), StandardCharsets.UTF_8));

            JsonNode imageNode = result.get(1);
            assertThat(imageNode.get(ORDER).asInt()).isEqualTo(1);
            assertThat(imageNode.get(TYPE).asText()).isEqualTo(FileType.IMAGE.getValue());
            assertThat(imageNode.get(FILENAME).asText()).isEqualTo(imageFile.getOriginalFilename());
            assertThat(imageNode.get(SRC).asText()).matches(regex+FileType.IMAGE.getValue()+"/.*");

            JsonNode videoNode = result.get(2);
            assertThat(videoNode.get(ORDER).asInt()).isEqualTo(2);
            assertThat(videoNode.get(TYPE).asText()).isEqualTo(FileType.VIDEO.getValue());
            assertThat(videoNode.get(FILENAME).asText()).isEqualTo(videoFile.getOriginalFilename());
            assertThat(videoNode.get(SRC).asText()).matches(regex+FileType.VIDEO.getValue()+"/.*");
        }

        @Test
        @DisplayName("validatePartsAndOrderInfo - null, 크기 불일치, order 검증 실패시 예외가 발생")
        void testSaveFilesAndGenerateContentJson_givenInvalidMultipartFilesAndOrderInfoList_willThrowException() {
            // given
            List<FileOrder> multipleZeroOrders = List.of(
                    new FileOrder(textFile0.getOriginalFilename(),0),
                    new FileOrder(imageFile.getOriginalFilename(),0),
                    new FileOrder(videoFile.getOriginalFilename(),2)
            );
            List<FileOrder> nonSequentialOrders = List.of(
                    new FileOrder(textFile0.getOriginalFilename(),0),
                    new FileOrder(imageFile.getOriginalFilename(),1),
                    new FileOrder(videoFile.getOriginalFilename(),3)
            );
            List<FileOrder> orderZeroWithImage = List.of(
                    new FileOrder("image_0.jpeg",0),
                    new FileOrder("video_0.mp4",1)
            );

            // when & then
            // null 검증
            assertThatThrownBy(() -> multipartDataProcessor.saveFilesAndGenerateContentJson(null,List.of()))
                    .isInstanceOf(InvalidFileInputException.class);
            assertThatThrownBy(() -> multipartDataProcessor.saveFilesAndGenerateContentJson(basicMediaFiles,null))
                    .isInstanceOf(InvalidFileInputException.class);

            // 크기 불일치
            assertThatThrownBy(() -> multipartDataProcessor.saveFilesAndGenerateContentJson(basicMediaFiles,textImageFilesOrder))
                    .isInstanceOf(InvalidFileInputException.class);

            // 유효하지 않은 order값 (order=0이 여러개 or 연속적이지 않은 order)
            assertThatThrownBy(() -> multipartDataProcessor.saveFilesAndGenerateContentJson(basicMediaFiles,multipleZeroOrders))
                    .isInstanceOf(InvalidFileInputException.class);
            assertThatThrownBy(() -> multipartDataProcessor.saveFilesAndGenerateContentJson(basicMediaFiles,nonSequentialOrders))
                    .isInstanceOf(InvalidFileInputException.class);
            assertThatThrownBy(() -> multipartDataProcessor.saveFilesAndGenerateContentJson(onlyMediaFiles,orderZeroWithImage))
                    .isInstanceOf(InvalidFileInputException.class);
        }

        @Test
        @DisplayName("validateFileConstraints - 확장자, 파일 개수, 파일 크기 제한 위반시 예외가 발생")
        void testSaveFilesAndGenerateContentJson_givenInvalidFileTypeOrCountOrSize_willThrowException() {
            // given
            MultipartFile fontFile = new MockMultipartFile("content","font_0.ttf","font/ttf",new byte[] {1,2,3});
            List<MultipartFile> fontFiles = List.of(fontFile);
            MultipartFile invalidImageFile = new MockMultipartFile("content", "image_0.bmp", "image/bmp", jpegData);
            List<MultipartFile> invalidImageFiles = List.of(invalidImageFile);
            List<MultipartFile> tooManyImages = new ArrayList<>();
            List<FileOrder> tooManyImageOrders = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                tooManyImages.add(new MockMultipartFile("content", "image" + i + ".jpg", "image/jpeg", "image".getBytes()));
                tooManyImageOrders.add(new FileOrder("image" + i + ".jpg", i + 1));
            }
            byte[] largeData = new byte[11 * 1024 * 1024];
            MockMultipartFile largeFile = new MockMultipartFile("content", "image_0.jpg", "image/jpeg", largeData);


            // when & then
            // 지원하지 않는 fileType 및 확장자
            assertThatThrownBy(() -> multipartDataProcessor.saveFilesAndGenerateContentJson(fontFiles,List.of(new FileOrder(fontFile.getOriginalFilename(),1))))
                    .isInstanceOf(UnsupportedFileException.class);
            assertThatThrownBy(() -> multipartDataProcessor.saveFilesAndGenerateContentJson(invalidImageFiles, List.of(new FileOrder(invalidImageFile.getOriginalFilename(),1))))
                    .isInstanceOf(UnsupportedFileException.class);

            // 파일 개수 초과
            assertThatThrownBy(() -> multipartDataProcessor.saveFilesAndGenerateContentJson(tooManyImages, tooManyImageOrders))
                    .isInstanceOf(FileLimitExceededException.class);

            // 파일 크기 초과
            assertThatThrownBy(() ->
                    multipartDataProcessor.saveFilesAndGenerateContentJson(
                            List.of(largeFile),List.of(new FileOrder(largeFile.getOriginalFilename(),1))
                    )
            ).isInstanceOf(FileLimitExceededException.class);
        }


    }

    @Nested
    @DisplayName("convertFileSrcToFullFileSrc 메서드 테스트")
    class testConvertFileSrcToFullFileSrc {
        @Test
        @DisplayName("저장된 파일 경로를 전체 파일 경로로 변환하기")
        void testConvertFileSrcToFullFileSrc_givenJsonContent_willReturnArrayNodeContent() throws IOException {
            // given
            JsonNode content = multipartDataProcessor.saveFilesAndGenerateContentJson(onlyImageFile,onlyImageFilesOrder);
            String fullSrcUrl = BASIC_PATH + FILE_KEY;
            given(s3FileService.generateS3SrcUrl(anyString())).willReturn(fullSrcUrl);

            // when
            JsonNode result = multipartDataProcessor.convertFileSrcToFullFileSrc(content);

            // then
            assertTrue(result.isArray());
            assertThat(result.size()).isEqualTo(1);

            JsonNode imageNode = result.get(0);
            assertTrue(imageNode.has(SRC));
            assertFalse(imageNode.has(DATA));
            assertThat(imageNode.get(TYPE).asText()).isEqualTo(FileType.IMAGE.getValue());
            assertThat(imageNode.get(SRC).asText()).isEqualTo(fullSrcUrl);
        }
    }

    @Nested
    @DisplayName("")
    class testConvertToPreview {
        @Test
        @DisplayName("저장된 텍스트와 이미지 파일 경로로 텍스트 및 이미지 미리보기 읽기")
        void testConvertToPreview_givenJsonContent_willReturnArrayNodePreviewContent() throws IOException {
            // given
            String fullSrcUrl = BASIC_PATH + FILE_KEY;
            given(s3FileService.generateS3SrcUrl(anyString())).willReturn(fullSrcUrl);

            // when
            ArrayNode result = multipartDataProcessor.convertToPreview(TEST_POST_CONTENT_TEXT_AND_IMAGE);

            // then
            assertThat(result).hasSize(2);

            JsonNode textNode = result.get(0);
            assertThat(textNode.get(TYPE).asText()).isEqualTo("text");
            assertThat(textNode.has(DATA)).isTrue();

            JsonNode imageNode = result.get(1);
            assertThat(imageNode.get(TYPE).asText()).isEqualTo("image");
            assertThat(imageNode.has(SRC)).isTrue();
            assertThat(imageNode.has(DATA)).isFalse();
            assertThat(imageNode.get(SRC).asText()).isEqualTo(fullSrcUrl);

            verify(s3FileService).generateS3SrcUrl(anyString());
        }

        @Test
        @DisplayName("저장된 텍스트만 있을 경우, 저장된 텍스트 미리보기 읽기")
        void testConvertToPreviewData_givenJsonContentText_willReturnArrayNodePreviewContent() throws IOException {
            // when
            ArrayNode result = multipartDataProcessor.convertToPreview(TEST_POST_CONTENT_TEXT_AND_VIDEO);

            // then
            assertThat(result).hasSize(1);

            JsonNode textNode = result.get(0);
            assertThat(textNode.get(TYPE).asText()).isEqualTo("text");
            assertThat(textNode.has(DATA)).isTrue();

            verify(s3FileService, never()).generateS3SrcUrl(anyString());
        }

        @Test
        @DisplayName("저장된 이미지만 있을 경우, 저장된 이미지 바이너리 데이터 미리보기 읽기")
        void testConvertToPreviewData_givenJsonContentImage_willReturnArrayNodePreviewContent() throws IOException {
            // given
            String fullSrcUrl = BASIC_PATH + FILE_KEY;
            given(s3FileService.generateS3SrcUrl(anyString())).willReturn(fullSrcUrl);

            // when
            ArrayNode result = multipartDataProcessor.convertToPreview(TEST_POST_CONTENT_IMAGE_AND_VIDEO);

            // then
            assertThat(result).hasSize(1);

            JsonNode imageNode = result.get(0);
            assertThat(imageNode.get(TYPE).asText()).isEqualTo("image");
            assertThat(imageNode.has(SRC)).isTrue();
            assertThat(imageNode.get(SRC).asText()).isEqualTo(fullSrcUrl);
            assertThat(imageNode.has(DATA)).isFalse();

            verify(s3FileService).generateS3SrcUrl(anyString());
        }

        @Test
        @DisplayName("저장된 텍스트와 이미지가 모두 없을 때, 빈 배열을 반환하기")
        void testConvertToPreview_givenJsonContentWithoutTextAndImage_willReturnEmptyArrayNode() throws IOException {
            // when
            ArrayNode result = multipartDataProcessor.convertToPreview(TEST_POST_CONTENT_VIDEO_AND_FILE);

            // then
            assertThat(result).isEmpty();
            verify(s3FileService, never()).generateS3SrcUrl(anyString());
        }

    }

    @Nested
    @DisplayName("deleteFiles 메서드 테스트")
    class testDeleteFiles {
        @Test
        @DisplayName("저장된 파일 경로로 파일 삭제")
        void testDeleteFiles_givenJsonContent_willDeleteFiles() throws IOException {
            // given
            JsonNode content = multipartDataProcessor.saveFilesAndGenerateContentJson(textImageFiles,textImageFilesOrder);
            doNothing().when(s3FileService).deleteFiles(content.get(1).get(SRC).asText());

            // when
            multipartDataProcessor.deleteFiles(content);

            // then
            verify(s3FileService,times(1)).deleteFiles(content.get(1).get(SRC).asText());
        }
    }
}
