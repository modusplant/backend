package kr.modusplant.domains.post.framework.outbound.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.post.common.util.usecase.request.PostFileUploadRequestTestUtils;
import kr.modusplant.domains.post.common.util.usecase.request.PostRequestTestUtils;
import kr.modusplant.domains.post.common.util.usecase.response.PostFileUploadUrlResponseTestUtils;
import kr.modusplant.domains.post.framework.outbound.processor.enums.PostFileType;
import kr.modusplant.domains.post.framework.outbound.processor.exception.EmptyThumbnailException;
import kr.modusplant.domains.post.framework.outbound.processor.exception.InvalidThumbnailException;
import kr.modusplant.domains.post.framework.outbound.processor.exception.TextOverLengthException;
import kr.modusplant.domains.post.framework.outbound.processor.exception.ThumbnailNotAllowedException;
import kr.modusplant.domains.post.usecase.record.ContentProcessRecord;
import kr.modusplant.domains.post.usecase.request.FileOrder;
import kr.modusplant.domains.post.usecase.request.PostFileUploadRequest;
import kr.modusplant.domains.post.usecase.response.PostFileUploadUrlResponse;
import kr.modusplant.infrastructure.config.jackson.JacksonConfig;
import kr.modusplant.infrastructure.file.service.PendingFileService;
import kr.modusplant.shared.exception.FileLimitExceededException;
import kr.modusplant.shared.exception.InvalidFileInputException;
import kr.modusplant.shared.exception.UnsupportedFileException;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.generator.UlidGeneratorHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static kr.modusplant.domains.post.common.constant.PostFileConstant.*;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_CONTENT_TEXT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PostContentDataProcessorTest implements PostRequestTestUtils, PostFileUploadRequestTestUtils, PostFileUploadUrlResponseTestUtils {
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final PendingFileService pendingFileService = Mockito.mock(PendingFileService.class);
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(JacksonConfig.objectMapper());
    private final UlidGeneratorHolder ulidGeneratorHolder = new UlidGeneratorHolder(new UlidIdGenerator());
    private final PostContentDataProcessor postContentDataProcessor = new PostContentDataProcessor(amazonS3Service, pendingFileService, objectMapperHolder, ulidGeneratorHolder);

    @BeforeEach
    void setUpPendingFileService() {
        // 기본적으로 검증 대상 fileKey 전부를 발급된(pending) 상태로 취급한다.
        given(pendingFileService.findTrackedFileKeys(anyList()))
                .willAnswer(invocation -> invocation.getArgument(0));
    }

    private static final String DATA = "data";
    private static final String FILENAME = "filename";
    private static final String FILE_KEY = "fileKey";
    private static final String ORDER = "order";
    private static final String SRC = "src";
    private static final String TYPE = "type";
    private static final String BASIC_PATH = "https://mocked-url.com/mock-bucket/";
    private static final String FILE_KEY_REGEX = "^post/[0-9A-HJKMNP-TV-Z]{26}/(image|video)/[^/]+\\.[^/]+$";

    @Nested
    @DisplayName("getMultipleUploadUrls 메서드 테스트")
    class testGetMultipleUploadUrls {

        @Test
        @DisplayName("단일 이미지 업로드 요청 시 presigned URL과 fileKey 반환")
        void testGetMultipleUploadUrls_givenSingleImageRequest_willReturnUploadUrlResponse() {
            // given
            List<PostFileUploadRequest> requests = List.of(testImageJpgFileUploadRequest);
            given(amazonS3Service.generatePutPresignedUrl(anyString(), anyString()))
                    .willReturn(TEST_IMAGE_JPG_PRESIGNED_URL);

            // when
            List<PostFileUploadUrlResponse> result = postContentDataProcessor.getMultipleUploadUrls(requests);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).filename()).isEqualTo(TEST_IMAGE_JPG_FILENAME);
            assertThat(result.get(0).uploadUrl()).isEqualTo(TEST_IMAGE_JPG_PRESIGNED_URL);
            assertThat(result.get(0).fileKey()).matches(FILE_KEY_REGEX);
            assertThat(result.get(0).fileKey()).contains("/image/");
        }

        @Test
        @DisplayName("이미지와 비디오 혼합 요청 시 각 파일에 대한 presigned URL과 fileKey 반환")
        void testGetMultipleUploadUrls_givenMultipleFiles_willReturnMultipleUploadUrlResponses() {
            // given
            List<PostFileUploadRequest> requests = List.of(testImageJpgFileUploadRequest, testVideoMp4FileUploadRequest);
            given(amazonS3Service.generatePutPresignedUrl(anyString(), anyString()))
                    .willReturn(TEST_IMAGE_JPG_PRESIGNED_URL)
                    .willReturn(TEST_VIDEO_MP4_PRESIGNED_URL);

            // when
            List<PostFileUploadUrlResponse> result = postContentDataProcessor.getMultipleUploadUrls(requests);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).filename()).isEqualTo(TEST_IMAGE_JPG_FILENAME);
            assertThat(result.get(0).fileKey()).contains("/image/");
            assertThat(result.get(1).filename()).isEqualTo(TEST_VIDEO_MP4_FILENAME);
            assertThat(result.get(1).fileKey()).contains("/video/");
        }

        @Test
        @DisplayName("파일명 중복 요청 시 예외 발생")
        void testGetMultipleUploadUrls_givenDuplicateFilename_willThrowException() {
            // given
            List<PostFileUploadRequest> dupRequests = List.of(
                    testImageJpgFileUploadRequest,
                    testImageJpgFileUploadRequest
            );

            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.getMultipleUploadUrls(dupRequests))
                    .isInstanceOf(InvalidFileInputException.class);
        }

        @Test
        @DisplayName("지원하지 않는 파일 확장자 요청 시 예외 발생")
        void testGetMultipleUploadUrls_givenUnsupportedFileExtension_willThrowException() {
            // given
            List<PostFileUploadRequest> unsupportedRequests = List.of(
                    new PostFileUploadRequest("font_0.ttf", "font/ttf")
            );

            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.getMultipleUploadUrls(unsupportedRequests))
                    .isInstanceOf(UnsupportedFileException.class);
        }

        @Test
        @DisplayName("이미지 파일 개수 초과 시 예외 발생")
        void testGetMultipleUploadUrls_givenTooManyImageFiles_willThrowException() {
            // given - MAX_IMAGE_FILES = 10, 11개면 초과
            List<PostFileUploadRequest> tooManyImages = new ArrayList<>();
            for (int i = 0; i <= 10; i++) {
                tooManyImages.add(new PostFileUploadRequest("image_" + i + ".jpg", TEST_IMAGE_JPG_CONTENT_TYPE));
            }

            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.getMultipleUploadUrls(tooManyImages))
                    .isInstanceOf(FileLimitExceededException.class);
        }
    }

    @Nested
    @DisplayName("generateContentJson 메서드 테스트")
    class testGenerateContentJson {

        @Test
        @DisplayName("텍스트와 파일을 순서대로 처리")
        void testGenerateContentJson_givenTextAndFiles_willReturnJsonContentWithCorrectOrder() throws IOException {
            // given
            List<FileOrder> files = allMediaFilesOrder; // [imageJpg(1), videoMp4(2)]

            // when
            ContentProcessRecord record = postContentDataProcessor.generateContentJson(TEST_POST_CONTENT_TEXT, files, TEST_IMAGE_JPG_FILENAME);
            JsonNode result = record.content();

            // then - text(order=0), image(order=1), video(order=2)
            assertThat(result.size()).isEqualTo(3);

            JsonNode textNode = result.get(0);
            assertThat(textNode.get(TYPE).asText()).isEqualTo(PostFileType.TEXT.getValue());
            assertThat(textNode.get(ORDER).asInt()).isEqualTo(0);
            assertThat(textNode.get(DATA).asText()).isEqualTo(TEST_POST_CONTENT_TEXT);

            JsonNode imageNode = result.get(1);
            assertThat(imageNode.get(TYPE).asText()).isEqualTo(PostFileType.IMAGE.getValue());
            assertThat(imageNode.get(ORDER).asInt()).isEqualTo(1);
            assertThat(imageNode.get(FILENAME).asText()).isEqualTo(TEST_IMAGE_JPG_FILENAME);
            assertThat(imageNode.get(SRC).asText()).isEqualTo(TEST_IMAGE_JPG_FILE_KEY);

            JsonNode videoNode = result.get(2);
            assertThat(videoNode.get(TYPE).asText()).isEqualTo(PostFileType.VIDEO.getValue());
            assertThat(videoNode.get(ORDER).asInt()).isEqualTo(2);
            assertThat(videoNode.get(FILENAME).asText()).isEqualTo(TEST_VIDEO_MP4_FILENAME);
            assertThat(videoNode.get(SRC).asText()).isEqualTo(TEST_VIDEO_MP4_FILE_KEY);

            assertThat(record.thumbnailPath()).isEqualTo(TEST_IMAGE_JPG_FILE_KEY);
        }

        @Test
        @DisplayName("순서 정보가 섞여 들어왔을 때, FileOrder.order 기준으로 정렬하여 처리")
        void testGenerateContentJson_givenFilesWithMixedOrder_willReturnJsonSortedByOrder() throws IOException {
            // when
            ContentProcessRecord record = postContentDataProcessor.generateContentJson(null, mixedOrder, TEST_IMAGE_JPG_FILENAME);
            JsonNode result = record.content();

            // then - image(order=1) 먼저, video(order=2) 나중
            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(0).get(ORDER).asInt()).isEqualTo(1);
            assertThat(result.get(0).get(TYPE).asText()).isEqualTo(PostFileType.IMAGE.getValue());
            assertThat(result.get(1).get(ORDER).asInt()).isEqualTo(2);
            assertThat(result.get(1).get(TYPE).asText()).isEqualTo(PostFileType.VIDEO.getValue());
        }

        @Test
        @DisplayName("이미지가 없는 경우, thumbnailPath null로 반환")
        void testGenerateContentJson_givenNoImageFiles_willReturnNullThumbnailPath() throws IOException {
            // when
            ContentProcessRecord record = postContentDataProcessor.generateContentJson(TEST_POST_CONTENT_TEXT, onlyVideoFileOrder, null);

            // then
            assertThat(record.thumbnailPath()).isNull();
        }

        @Test
        @DisplayName("텍스트만 있고 파일이 없는 경우, 텍스트 노드만 반환")
        void testGenerateContentJson_givenTextOnly_willReturnOnlyTextNode() throws IOException {
            // when
            ContentProcessRecord record = postContentDataProcessor.generateContentJson(TEST_POST_CONTENT_TEXT, null, null);

            // then
            assertThat(record.content().size()).isEqualTo(1);
            assertThat(record.content().get(0).get(TYPE).asText()).isEqualTo(PostFileType.TEXT.getValue());
            assertThat(record.content().get(0).get(DATA).asText()).isEqualTo(TEST_POST_CONTENT_TEXT);
            assertThat(record.thumbnailPath()).isNull();
        }

        @Test
        @DisplayName("텍스트 글자수 5000자 정확히 입력 시 정상 처리")
        void testGenerateContentJson_givenTextExactly5000Chars_willSuccess() throws IOException {
            // given
            String exactText = "a".repeat(5000);

            // when & then
            assertThat(postContentDataProcessor.generateContentJson(exactText, null, null)).isNotNull();
        }

        @Test
        @DisplayName("텍스트 글자수 5000자 초과 시 예외 발생")
        void testGenerateContentJson_givenTextExceeds5000Chars_willThrowException() {
            // given
            String longText = "a".repeat(5001);

            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(longText, null, null))
                    .isInstanceOf(TextOverLengthException.class);
        }

        @Test
        @DisplayName("파일명 중복 시 예외 발생")
        void testGenerateContentJson_givenDuplicateFilenames_willThrowException() {
            // given
            List<FileOrder> dupFiles = List.of(
                    new FileOrder(1, TEST_IMAGE_JPG_FILENAME, TEST_IMAGE_JPG_FILE_KEY),
                    new FileOrder(2, TEST_IMAGE_JPG_FILENAME, TEST_IMAGE_PNG_FILE_KEY)
            );

            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(null, dupFiles, TEST_IMAGE_JPG_FILENAME))
                    .isInstanceOf(InvalidFileInputException.class);
        }

        @Test
        @DisplayName("지원하지 않는 파일 확장자 사용 시 예외 발생")
        void testGenerateContentJson_givenUnsupportedFileExtension_willThrowException() {
            // given
            List<FileOrder> unsupportedFiles = List.of(
                    new FileOrder(1, "font_0.ttf", "post/01KM6B54J6G4DSGHT22NJ8Z1WC/image/font_0_1.ttf")
            );

            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(null, unsupportedFiles, null))
                    .isInstanceOf(UnsupportedFileException.class);
        }

        @Test
        @DisplayName("유효하지 않은 fileKey 형식 사용 시 예외 발생")
        void testGenerateContentJson_givenInvalidFileKey_willThrowException() {
            // given
            List<FileOrder> badKeyFiles = List.of(
                    new FileOrder(1, TEST_IMAGE_JPG_FILENAME, "invalid/key/image_0.jpg")
            );

            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(null, badKeyFiles, TEST_IMAGE_JPG_FILENAME))
                    .isInstanceOf(InvalidFileInputException.class);
        }

        @Test
        @DisplayName("발급(pending)된 적 없는 fileKey 사용 시 예외 발생")
        void testGenerateContentJson_givenUntrackedFileKey_willThrowException() {
            // given - 우리가 발급하지 않은 fileKey를 사용하는 상황을 흉내낸다
            given(pendingFileService.findTrackedFileKeys(anyList())).willReturn(List.of());
            List<FileOrder> untrackedFiles = List.of(
                    new FileOrder(1, TEST_IMAGE_JPG_FILENAME, TEST_IMAGE_JPG_FILE_KEY)
            );

            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(null, untrackedFiles, TEST_IMAGE_JPG_FILENAME))
                    .isInstanceOf(InvalidFileInputException.class);
        }

        @Test
        @DisplayName("파일 순서가 1부터 시작하지 않거나 연속적이지 않을 때 예외 발생")
        void testGenerateContentJson_givenNonSequentialOrder_willThrowException() {
            // given
            List<FileOrder> order0Files = List.of(
                    new FileOrder(0, TEST_IMAGE_JPG_FILENAME, TEST_IMAGE_JPG_FILE_KEY)
            );
            // order 간격이 있는 경우 (1, 3)
            List<FileOrder> gapOrders = List.of(
                    new FileOrder(1, TEST_IMAGE_JPG_FILENAME, TEST_IMAGE_JPG_FILE_KEY),
                    new FileOrder(3, TEST_VIDEO_MP4_FILENAME, TEST_VIDEO_MP4_FILE_KEY)
            );

            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(null, order0Files, TEST_IMAGE_JPG_FILENAME))
                    .isInstanceOf(InvalidFileInputException.class);
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(null, gapOrders, TEST_IMAGE_JPG_FILENAME))
                    .isInstanceOf(InvalidFileInputException.class);
        }

        @Test
        @DisplayName("이미지 존재 시 thumbnailFilename이 null이면 EmptyThumbnailException 발생")
        void testGenerateContentJson_givenImageFilesWithNullThumbnailFilename_willThrowEmptyThumbnailException() {
            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(null, onlyImageFilesOrder, null))
                    .isInstanceOf(EmptyThumbnailException.class);
        }

        @Test
        @DisplayName("이미지 존재 시 thumbnailFilename이 이미지가 아닌 파일이거나 목록에 없으면 InvalidThumbnailException 발생")
        void testGenerateContentJson_givenImageFilesWithInvalidThumbnailFilename_willThrowInvalidThumbnailException() {
            // when & then
            // thumbnail이 비디오 파일명인 경우
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(null, allMediaFilesOrder, TEST_VIDEO_MP4_FILENAME))
                    .isInstanceOf(InvalidThumbnailException.class);
            // thumbnail이 목록에 없는 파일명인 경우
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(null, onlyImageFilesOrder, "nonexistent.jpg"))
                    .isInstanceOf(InvalidThumbnailException.class);
        }

        @Test
        @DisplayName("이미지 미존재 시 thumbnailFilename이 null이 아니면 ThumbnailNotAllowedException 발생")
        void testGenerateContentJson_givenNoImageFilesWithNonNullThumbnailFilename_willThrowThumbnailNotAllowedException() {
            // when & then
            assertThatThrownBy(() -> postContentDataProcessor.generateContentJson(TEST_POST_CONTENT_TEXT, onlyVideoFileOrder, TEST_IMAGE_JPG_FILENAME))
                    .isInstanceOf(ThumbnailNotAllowedException.class);
        }
    }

    @Nested
    @DisplayName("convertFileSrcToFullFileSrc 메서드 테스트")
    class testConvertFileSrcToFullFileSrc {
        @Test
        @DisplayName("저장된 파일 경로를 전체 파일 경로로 변환")
        void testConvertFileSrcToFullFileSrc_givenJsonContent_willReturnArrayNodeContent() throws IOException {
            // given
            JsonNode content = postContentDataProcessor.generateContentJson(null, onlyImageFilesOrder, TEST_IMAGE_JPG_FILENAME).content();
            String fullSrcUrl = BASIC_PATH + TEST_IMAGE_JPG_FILE_KEY;
            given(amazonS3Service.generateS3SrcUrl(anyString())).willReturn(fullSrcUrl);

            // when
            JsonNode result = postContentDataProcessor.convertFileSrcToFullFileSrc(content);

            // then
            assertThat(result.isArray()).isTrue();
            assertThat(result.size()).isEqualTo(1);

            JsonNode imageNode = result.get(0);
            assertThat(imageNode.has(SRC)).isTrue();
            assertThat(imageNode.has(DATA)).isFalse();
            assertThat(imageNode.has(FILE_KEY)).isFalse();
            assertThat(imageNode.get(TYPE).asText()).isEqualTo(PostFileType.IMAGE.getValue());
            assertThat(imageNode.get(SRC).asText()).isEqualTo(fullSrcUrl);
        }
    }

    @Nested
    @DisplayName("convertFileSrcToFullFileSrcWithFileKey 메서드 테스트")
    class testConvertFileSrcToFullFileSrcWithFileKey {
        @Test
        @DisplayName("저장된 파일 경로를 전체 파일 경로로 변환하면서 fileKey도 함께 반환")
        void testConvertFileSrcToFullFileSrcWithFileKey_givenJsonContent_willReturnArrayNodeContentWithFileKey() throws IOException {
            // given
            JsonNode content = postContentDataProcessor.generateContentJson(null, onlyImageFilesOrder, TEST_IMAGE_JPG_FILENAME).content();
            String fullSrcUrl = BASIC_PATH + TEST_IMAGE_JPG_FILE_KEY;
            given(amazonS3Service.generateS3SrcUrl(anyString())).willReturn(fullSrcUrl);

            // when
            JsonNode result = postContentDataProcessor.convertFileSrcToFullFileSrcWithFileKey(content);

            // then
            assertThat(result.isArray()).isTrue();
            assertThat(result.size()).isEqualTo(1);

            JsonNode imageNode = result.get(0);
            assertThat(imageNode.has(SRC)).isTrue();
            assertThat(imageNode.has(DATA)).isFalse();
            assertThat(imageNode.has(FILE_KEY)).isTrue();
            assertThat(imageNode.get(TYPE).asText()).isEqualTo(PostFileType.IMAGE.getValue());
            assertThat(imageNode.get(SRC).asText()).isEqualTo(fullSrcUrl);
            assertThat(imageNode.get(FILE_KEY).asText()).isEqualTo(TEST_IMAGE_JPG_FILE_KEY);
        }
    }

    @Nested
    @DisplayName("convertToPreview 메서드 테스트")
    class testConvertToPreview {
        @Test
        @DisplayName("텍스트와 이미지 썸네일이 모두 있을 때, 텍스트와 이미지 미리보기 반환")
        void testConvertToPreview_givenTextAndThumbnail_willReturnTextAndImagePreview() {
            // given
            String fullSrcUrl = BASIC_PATH + TEST_IMAGE_JPG_FILE_KEY;
            given(amazonS3Service.generateS3SrcUrl(anyString())).willReturn(fullSrcUrl);

            // when
            ArrayNode result = postContentDataProcessor.convertToPreview(TEST_POST_CONTENT_TEXT_AND_IMAGE, TEST_POST_CONTENT_TEXT_AND_IMAGE_THUMBNAIL_KEY);

            // then
            assertThat(result).hasSize(2);

            JsonNode textNode = result.get(0);
            assertThat(textNode.get(TYPE).asText()).isEqualTo(PostFileType.TEXT.getValue());
            assertThat(textNode.has(DATA)).isTrue();

            JsonNode imageNode = result.get(1);
            assertThat(imageNode.get(TYPE).asText()).isEqualTo(PostFileType.IMAGE.getValue());
            assertThat(imageNode.has(SRC)).isTrue();
            assertThat(imageNode.has(DATA)).isFalse();
            assertThat(imageNode.get(SRC).asText()).isEqualTo(fullSrcUrl);

            verify(amazonS3Service).generateS3SrcUrl(anyString());
        }

        @Test
        @DisplayName("텍스트만 있고 썸네일이 없을 때, 텍스트 미리보기만 반환")
        void testConvertToPreview_givenTextOnlyContent_willReturnTextPreview() {
            // when
            ArrayNode result = postContentDataProcessor.convertToPreview(TEST_POST_CONTENT_TEXT_AND_VIDEO, null);

            // then
            assertThat(result).hasSize(1);

            JsonNode textNode = result.get(0);
            assertThat(textNode.get(TYPE).asText()).isEqualTo(PostFileType.TEXT.getValue());
            assertThat(textNode.has(DATA)).isTrue();

            verify(amazonS3Service, never()).generateS3SrcUrl(anyString());
        }

        @Test
        @DisplayName("텍스트 없이 이미지 썸네일만 있을 때, 이미지 미리보기만 반환")
        void testConvertToPreview_givenThumbnailOnlyContent_willReturnImagePreview() {
            // given
            String fullSrcUrl = BASIC_PATH + TEST_IMAGE_JPG_FILE_KEY;
            given(amazonS3Service.generateS3SrcUrl(anyString())).willReturn(fullSrcUrl);

            // when
            ArrayNode result = postContentDataProcessor.convertToPreview(TEST_POST_CONTENT_IMAGE_AND_VIDEO, TEST_POST_CONTENT_IMAGE_AND_VIDEO_THUMBNAIL_KEY);

            // then
            assertThat(result).hasSize(1);

            JsonNode imageNode = result.get(0);
            assertThat(imageNode.get(TYPE).asText()).isEqualTo(PostFileType.IMAGE.getValue());
            assertThat(imageNode.has(SRC)).isTrue();
            assertThat(imageNode.get(SRC).asText()).isEqualTo(fullSrcUrl);
            assertThat(imageNode.has(DATA)).isFalse();

            verify(amazonS3Service).generateS3SrcUrl(anyString());
        }

        @Test
        @DisplayName("텍스트와 이미지 썸네일이 모두 없을 때, 빈 배열 반환")
        void testConvertToPreview_givenNoTextAndNoThumbnail_willReturnEmpty() {
            // when
            ArrayNode result = postContentDataProcessor.convertToPreview(TEST_POST_CONTENT_VIDEO_AND_FILE, null);

            // then
            assertThat(result).isEmpty();
            verify(amazonS3Service, never()).generateS3SrcUrl(anyString());
        }
    }

    @Nested
    @DisplayName("deleteFiles 메서드 테스트")
    class testDeleteFiles {
        @Test
        @DisplayName("저장된 파일 경로로 파일 삭제")
        void testDeleteFiles_givenJsonContent_willDeleteFiles() throws IOException {
            // given
            JsonNode content = postContentDataProcessor.generateContentJson(null, onlyImageFilesOrder, TEST_IMAGE_JPG_FILENAME).content();
            doNothing().when(amazonS3Service).deleteFile(anyString());

            // when
            postContentDataProcessor.deleteFiles(content);

            // then
            verify(amazonS3Service, times(1)).deleteFile(TEST_IMAGE_JPG_FILE_KEY);
        }

        @Test
        @DisplayName("content가 null인 경우, 아무 동작도 하지 않음")
        void testDeleteFiles_givenNullContent_willDoNothing() {
            // when
            postContentDataProcessor.deleteFiles(null);

            // then
            verify(amazonS3Service, never()).deleteFile(anyString());
        }
    }

    @Nested
    @DisplayName("extractOriginalFilenameFromFileKey 메서드 테스트")
    class testExtractOriginalFilenameFromFileKey {
        @Test
        @DisplayName("fileKey에서 원본 파일명 추출")
        void testExtractOriginalFilenameFromFileKey_givenFileKey_willReturnFilename() {
            assertThat(postContentDataProcessor.extractOriginalFilenameFromFileKey("post/01HV6ABCDEF/image/image_0_1.jpg")).isEqualTo("image_0.jpg");
            assertThat(postContentDataProcessor.extractOriginalFilenameFromFileKey("post/01HV6ABCDEF/image/cat_0_10.jpeg")).isEqualTo("cat_0.jpeg");
            assertThat(postContentDataProcessor.extractOriginalFilenameFromFileKey("post/01HV6ABCDEF/image/photo_2023_2.png")).isEqualTo("photo_2023.png");
            assertThat(postContentDataProcessor.extractOriginalFilenameFromFileKey(null)).isNull();
        }
    }
}