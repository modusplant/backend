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

import static kr.modusplant.domains.post.common.constant.PostConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class PostIdTest implements PostTestUtils {
    @SuppressWarnings("unused")
    private final UlidGeneratorHolder ulidGeneratorHolder = new UlidGeneratorHolder(new UlidIdGenerator());

    @Nested
    @DisplayName("PostId generate 테스트")
    class GenerateTests {

        @Test
        @DisplayName("파라미터 없이 PostId 반환")
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
        @DisplayName("유효한 ULID 문자열로 PostId 반환")
        void testCreate_givenUlid_willReturnPostId() {
            // then
            assertFalse(StringUtils.isBlank(testPostId.getValue()));
            assertFalse(testPostId.getValue().length() != 26);
            assertTrue(testPostId.getValue().matches("^[0-9A-HJKMNP-TV-Z]{26}$"));
            assertNotNull(testPostId);
            assertEquals(TEST_POST_ULID, testPostId.getValue());
        }

        @Test
        @DisplayName("null이나 빈 ULID일 때 예외 반환")
        void testCreate_givenNullOrEmptyPostId_willThrowException() {
            // when & then
            assertThrows(EmptyValueException.class, () -> PostId.create(null));
            assertThrows(EmptyValueException.class, () -> PostId.create(""));
            assertThrows(EmptyValueException.class, () -> PostId.create("   "));
        }

        @Test
        @DisplayName("유효하지 않은 ULID일 때 예외 반환")
        void testCreate_givenInvalidUlid_willThrowException() {
            // when & then
            assertThrows(InvalidValueException.class, () -> PostId.create(TEST_INVALID_POST_ULID)); // 25자
            assertThrows(InvalidValueException.class, () -> PostId.create(TEST_INVALID_POST_ULID2)); // 유효하지 않은 문자 @포함
        }

    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체로 참 반환")
        void testEquals_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testPostId, testPostId);
            assertEquals(testPostId.hashCode(), testPostId.hashCode());
        }

        @Test
        @DisplayName("다른 클래스 인스턴스로 거짓 반환")
        void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testPostId,testAuthorId);
        }

        @Test
        @DisplayName("다른 프로퍼티 인스턴스로 거짓 반환")
        void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testPostId, PostId.generate());
        }

    }

}