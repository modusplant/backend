package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecondaryCategoryIdTest implements PostTestUtils {
    @Nested
    @DisplayName("SecondaryCategoryId UUID 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("유효한 UUID로 SecondaryCategoryId 반환")
        void testCreate_givenId_willReturnSecondaryCategoryId() {
            assertNotNull(testSecondaryCategoryId);
            assertEquals(1, testSecondaryCategoryId.getValue());
        }

        @Test
        @DisplayName("null UUID일 때 예외 반환")
        void testCreate_givenNullParameter_willThrowException() {
            // when & then
            assertThrows(EmptyValueException.class, () -> SecondaryCategoryId.create(null));
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체로 참 반환")
        void testEquals_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testSecondaryCategoryId, testSecondaryCategoryId);
            assertEquals(testSecondaryCategoryId.hashCode(), testSecondaryCategoryId.hashCode());
        }

        @Test
        @DisplayName("다른 클래스 인스턴스로 거짓 반환")
        void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testSecondaryCategoryId,testPostId);
        }

        @Test
        @DisplayName("다른 프로퍼티 인스턴스로 거짓 반환")
        void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testSecondaryCategoryId, testSecondaryCategoryId2);
        }

    }

}