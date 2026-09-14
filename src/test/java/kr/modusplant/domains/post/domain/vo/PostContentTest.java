package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_TITLE;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_THUMBNAIL_KEY;
import static org.junit.jupiter.api.Assertions.*;

class PostContentTest implements PostTestUtils {

    private static final String MAX_LENGTH_TITLE = "a".repeat(60); // 60자
    private static final String OVER_MAX_LENGTH_TITLE = "a".repeat(61); // 61자

    @Nested
    @DisplayName("PostContent 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("유효한 제목·내용으로 PostContent 반환")
        void testCreate_givenTitleAndContent_willReturnPostContent() {
            // when
            PostContent postContent = PostContent.create(MAX_LENGTH_TITLE, TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY);

            // then
            assertNotNull(testPostContent);
            assertEquals(TEST_POST_TITLE, testPostContent.getTitle());
            assertEquals(TEST_POST_CONTENT, testPostContent.getContent());
            assertNotNull(postContent);
            assertEquals(MAX_LENGTH_TITLE, postContent.getTitle());
            assertEquals(60, postContent.getTitle().length());
        }

        @Test
        @DisplayName("공백 포함 제목으로 PostContent 반환")
        void testCreate_givenTrimmedTitle_willReturnPostContent() {
            // given
            String titleWithSpaces = "  게시글 제목  ";

            // when
            PostContent postContent = PostContent.create(titleWithSpaces, TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY);

            // then
            assertNotNull(postContent);
            assertEquals(titleWithSpaces, postContent.getTitle());
        }

        @Test
        @DisplayName("null이나 빈 제목일 때 예외 반환")
        void testCreate_givenNullOrEmptyTitle_willThrowException() {
            // when & then
            EmptyValueException exception1 = assertThrows(EmptyValueException.class,
                    () -> PostContent.create(null, TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY));
            assertEquals(PostErrorCode.EMPTY_POST_CONTENT, exception1.getErrorCode());

            EmptyValueException exception2 = assertThrows(EmptyValueException.class,
                    () -> PostContent.create("", TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY));
            assertEquals(PostErrorCode.EMPTY_POST_CONTENT, exception2.getErrorCode());

            EmptyValueException exception3 = assertThrows(EmptyValueException.class,
                    () -> PostContent.create("   ", TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY));
            assertEquals(PostErrorCode.EMPTY_POST_CONTENT, exception3.getErrorCode());
        }

        @Test
        @DisplayName("제목이 60자 초과일 때 예외 반환")
        void testCreate_givenOverMaxLengthTitle_willThrowException() {
            // when & then
            InvalidValueException exception = assertThrows(InvalidValueException.class,
                    () -> PostContent.create(OVER_MAX_LENGTH_TITLE, TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY));
            assertEquals(PostErrorCode.INVALID_POST_CONTENT, exception.getErrorCode());
        }

        @Test
        @DisplayName("내용이 null일 때 예외 반환")
        void testCreate_givenNullContent_willThrowException() {
            // when & then
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> PostContent.create(TEST_POST_TITLE, null,null));
            assertEquals(PostErrorCode.EMPTY_POST_CONTENT, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("PostContent Draft 생성 테스트")
    class CreateDraftTests {

        @Test
        @DisplayName("유효한 제목·내용으로 PostContent 반환")
        void testCreateDraft_givenTitleAndContent_willReturnPostContent() {
            // when
            PostContent postContent = PostContent.createDraft(MAX_LENGTH_TITLE, null,null);

            // then
            assertNotNull(postContent);
            assertEquals(MAX_LENGTH_TITLE, postContent.getTitle());
            assertEquals(60, postContent.getTitle().length());
            assertNull(postContent.getContent());
        }

        @Test
        @DisplayName("null이나 빈 제목일 때 예외 반환")
        void testCreateDraft_givenNullOrEmptyTitle_willThrowException() {
            // when & then
            EmptyValueException exception1 = assertThrows(EmptyValueException.class,
                    () -> PostContent.createDraft(null, null,null));
            assertEquals(PostErrorCode.EMPTY_POST_CONTENT, exception1.getErrorCode());

            EmptyValueException exception2 = assertThrows(EmptyValueException.class,
                    () -> PostContent.createDraft("", null,null));
            assertEquals(PostErrorCode.EMPTY_POST_CONTENT, exception2.getErrorCode());

            EmptyValueException exception3 = assertThrows(EmptyValueException.class,
                    () -> PostContent.createDraft("   ", null, null));
            assertEquals(PostErrorCode.EMPTY_POST_CONTENT, exception3.getErrorCode());
        }

        @Test
        @DisplayName("제목이 60자 초과일 때 예외 반환")
        void testCreateDraft_givenOverMaxLengthTitle_willThrowException() {
            // when & then
            InvalidValueException exception = assertThrows(InvalidValueException.class,
                    () -> PostContent.createDraft(OVER_MAX_LENGTH_TITLE, TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY));
            assertEquals(PostErrorCode.INVALID_POST_CONTENT, exception.getErrorCode());
        }
    }


    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체로 참 반환")
        void testEquals_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testPostContent, testPostContent);
            assertEquals(testPostContent.hashCode(), testPostContent.hashCode());
        }

        @Test
        @DisplayName("다른 클래스 인스턴스로 거짓 반환")
        void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            //noinspection AssertBetweenInconvertibleTypes
            assertNotEquals(testPostContent, testPostId);
        }

        @Test
        @DisplayName("다른 프로퍼티 인스턴스로 거짓 반환")
        void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testPostContent, PostContent.create("title",TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY));
        }

    }

}