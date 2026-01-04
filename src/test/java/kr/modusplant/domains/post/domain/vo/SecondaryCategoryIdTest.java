package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecondaryCategoryIdTest implements PostTestUtils {
    @Nested
    @DisplayName("SecondaryCategoryId  UUID 생성 테스트")
    class createTests {

        @Test
        @DisplayName("유효한 UUID로 SecondaryCategoryId 생성한다")
        void testCreate_givenId_willReturnSecondaryCategoryId() {
            assertNotNull(testSecondaryCategoryId);
            assertEquals(1, testSecondaryCategoryId.getValue());
        }

        @Test
        @DisplayName("null UUID로 SecondaryCategoryId 생성 시 EmptyCategoryIdException을 발생시킨다")
        void testCreate_givenNullParameter_willThrowException() {
            // when & then
            assertThrows(EmptyCategoryIdException.class, () -> SecondaryCategoryId.create(null));
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체에 대한 equals 호출")
        void useEqual_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testSecondaryCategoryId, testSecondaryCategoryId);
            assertEquals(testSecondaryCategoryId.hashCode(), testSecondaryCategoryId.hashCode());
        }

        @Test
        @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testSecondaryCategoryId,testPostId);
        }

        @Test
        @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testSecondaryCategoryId, testSecondaryCategoryId2);
        }

    }

}