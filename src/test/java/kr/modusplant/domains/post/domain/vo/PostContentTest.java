package kr.modusplant.domains.post.domain.vo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.EmptyPostContentException;
import kr.modusplant.domains.post.domain.exception.InvalidPostContentException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostStringConstant.TEST_POST_TITLE;
import static org.junit.jupiter.api.Assertions.*;

class PostContentTest implements PostTestUtils {

    private static final String MAX_LENGTH_TITLE = "a".repeat(60); // 60자
    private static final String OVER_MAX_LENGTH_TITLE = "a".repeat(61); // 61자
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Nested
    @DisplayName("PostContent 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("유효한 제목과 내용으로 PostContent를 생성한다")
        void testCreate_givenTitleAndContent_willReturnPostContent() {
            // when
            PostContent postContent = PostContent.create(MAX_LENGTH_TITLE, TEST_POST_CONTENT);

            // then
            assertNotNull(testPostContent);
            assertEquals(TEST_POST_TITLE, testPostContent.getTitle());
            assertEquals(TEST_POST_CONTENT, testPostContent.getContent());
            assertNotNull(postContent);
            assertEquals(MAX_LENGTH_TITLE, postContent.getTitle());
            assertEquals(60, postContent.getTitle().length());
        }

        @Test
        @DisplayName("공백이 포함된 제목을 trim하여 PostContent를 생성한다")
        void testCreate_givenTrimmedTitle_willReturnPostContent() {
            // given
            String titleWithSpaces = "  게시글 제목  ";

            // when
            PostContent postContent = PostContent.create(titleWithSpaces, TEST_POST_CONTENT);

            // then
            assertNotNull(postContent);
            assertEquals(titleWithSpaces, postContent.getTitle());
        }

        @Test
        @DisplayName("빈 JsonNode 객체로 PostContent를 생성한다")
        void testCreate_givenEmptyJsonNode_willReturnPostContent() {
            // given
            JsonNode emptyContent = objectMapper.createObjectNode();

            // when
            PostContent postContent = PostContent.create(TEST_POST_TITLE, emptyContent);

            // then
            assertNotNull(postContent);
            assertEquals(TEST_POST_TITLE, postContent.getTitle());
            assertEquals(emptyContent, postContent.getContent());
        }

        @Test
        @DisplayName("null 이나 빈 문자열 제목으로 PostContent 생성 시 EmptyPostContentException을 발생시킨다")
        void testCreate_givenNullOrEmptyTitle_willThrowException() {
            // when & then
            EmptyPostContentException exception1 = assertThrows(EmptyPostContentException.class,
                    () -> PostContent.create(null, TEST_POST_CONTENT));
            assertEquals(exception1.getErrorCode(), PostErrorCode.EMPTY_POST_CONTENT);

            EmptyPostContentException exception2 = assertThrows(EmptyPostContentException.class,
                    () -> PostContent.create("", TEST_POST_CONTENT));
            assertEquals(exception2.getErrorCode(), PostErrorCode.EMPTY_POST_CONTENT);

            EmptyPostContentException exception3 = assertThrows(EmptyPostContentException.class,
                    () -> PostContent.create("   ", TEST_POST_CONTENT));
            assertEquals(exception3.getErrorCode(), PostErrorCode.EMPTY_POST_CONTENT);
        }

        @Test
        @DisplayName("제목 길이가 60자를 초과할 때 InvalidPostContentException을 발생시킨다")
        void testCreate_givenOverMaxLengthTitle_willThrowException() {
            // when & then
            InvalidPostContentException exception = assertThrows(InvalidPostContentException.class,
                    () -> PostContent.create(OVER_MAX_LENGTH_TITLE, TEST_POST_CONTENT));
            assertEquals(exception.getErrorCode(), PostErrorCode.INVALID_POST_CONTENT);
        }

        @Test
        @DisplayName("null 내용으로 PostContent 생성 시 EmptyPostContentException을 발생시킨다")
        void testCreate_givenNullContent_willThrowException() {
            // when & then
            EmptyPostContentException exception = assertThrows(EmptyPostContentException.class,
                    () -> PostContent.create(TEST_POST_TITLE, null));
            assertEquals(exception.getErrorCode(), PostErrorCode.EMPTY_POST_CONTENT);
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체에 대한 equals 호출")
        void useEqual_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testPostContent, testPostContent);
            assertEquals(testPostContent.hashCode(), testPostContent.hashCode());
        }

        @Test
        @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testPostContent, testPostId);
        }

        @Test
        @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testPostContent, PostContent.create("title",TEST_POST_CONTENT));
        }

    }

}