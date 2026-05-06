package kr.modusplant.domains.search.adapter.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.post.domain.exception.ContentProcessingException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_THUMBNAIL_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class SearchPostTranslatorTest {
    private final MultipartDataProcessorPort multipartDataProcessorPort = Mockito.mock(MultipartDataProcessorPort.class);
    private final SearchPostTranslator searchPostTranslator = new SearchPostTranslator(multipartDataProcessorPort);

    @Test
    @DisplayName("유효한 본문으로 getJsonNodeContentPreview으로 본문 미리보기 반환")
    void testGetJsonNodeContentPreview_withValidContent_willReturnContentPreview() throws IOException {
        // given
        ArrayNode expectedContentPreview = Mockito.mock(ArrayNode.class);
        given(multipartDataProcessorPort.convertToPreview(TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY))
                .willReturn(expectedContentPreview);

        // when
        JsonNode actualContentPreview = searchPostTranslator.getJsonNodeContentPreview(TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY);

        // then
        assertThat(actualContentPreview).isEqualTo(expectedContentPreview);
    }

    @Test
    @DisplayName("null인 본문으로 getJsonNodeContentPreview으로 null 반환")
    void testGetJsonNodeContentPreview_withNullContent_willReturnNull() {
        // given & when
        JsonNode actualContentPreview = searchPostTranslator.getJsonNodeContentPreview(null, TEST_POST_CONTENT_THUMBNAIL_KEY);

        // then
        assertThat(actualContentPreview).isNull();
    }

    @Test
    @DisplayName("미디어 데이터 처리 실패로 getJsonNodeContentPreview으로 예외 발생")
    void testGetJsonNodeContentPreview_withIOException_willThrowException() throws IOException {
        // given
        given(multipartDataProcessorPort.convertToPreview(TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY))
                .willThrow(new ContentProcessingException());

        // when
        ContentProcessingException exception = assertThrows(ContentProcessingException.class, () ->
                searchPostTranslator.getJsonNodeContentPreview(TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorCode.CONTENT_PROCESSING_FAILED);
    }
}