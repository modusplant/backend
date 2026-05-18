package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.generator.UlidGeneratorHolder;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class PostIdTest implements PostTestUtils {
    @SuppressWarnings("unused")
    private final UlidGeneratorHolder ulidGeneratorHolder = new UlidGeneratorHolder(new UlidIdGenerator());

    @Nested
    @DisplayName("PostId generate 테스트")
    class GenerateTests {

        @Test
        @DisplayName("generate() 메서드로 유효한 PostId를 생성한다")
        void testGenerate_givenNoParameter_willReturnPostId() {
            // when
            PostId postId = PostId.generate();

            // then
            assertNotNull(postId);
            assertNotNull(postId.getValue());
            assertEquals(26, postId.getValue().length());
            assertTrue(postId.getValue().matches("^[0-9A-HJKMNP-TV-Z]{26}$"));
        }
    }

    @Nested
    @DisplayName("PostId create 테스트")
    class CreateTests {

        @Test
        @DisplayName("유효한 ULID 문자열로 PostId를 생성한다")
        void testCreate_givenUlid_willReturnPostId() {
            // then
            assertFalse(StringUtils.isBlank(testPostId.getValue()));
            assertFalse(testPostId.getValue().length() != 26);
            assertTrue(testPostId.getValue().matches("^[0-9A-HJKMNP-TV-Z]{26}$"));
            assertNotNull(testPostId);
            assertEquals(TEST_POST_ULID, testPostId.getValue());
        }

        @Test
        @DisplayName("null이나 빈 문자열 ULID로 PostId 생성 시 EmptyPostIdException을 발생시킨다")
        void testCreate_givenNullOrEmptyPostId_willThrowException() {
            // when & then
            assertThrows(EmptyValueException.class, () -> PostId.create(null));
            assertThrows(EmptyValueException.class, () -> PostId.create(""));
            assertThrows(EmptyValueException.class, () -> PostId.create("   "));
        }

        @Test
        @DisplayName("유효하지 않은 ULID로 PostId 생성 시 InvalidPostIdException을 발생시킨다")
        void shouldThrowInvalidPostIdExceptionWhenUlidLengthIsNot26() {
            // when & then
            assertThrows(InvalidValueException.class, () -> PostId.create(TEST_INVALID_POST_ULID)); // 25자
            assertThrows(InvalidValueException.class, () -> PostId.create(TEST_INVALID_POST_ULID2)); // 유효하지 않은 문자 @포함
        }

    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체에 대한 equals 호출")
        void useEqual_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testPostId, testPostId);
            assertEquals(testPostId.hashCode(), testPostId.hashCode());
        }

        @Test
        @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testPostId,testAuthorId);
        }

        @Test
        @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testPostId, PostId.generate());
        }

    }

}