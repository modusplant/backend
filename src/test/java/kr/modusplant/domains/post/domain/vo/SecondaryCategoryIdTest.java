package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.utils.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import kr.modusplant.domains.post.domain.exception.InvalidCategoryIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.post.common.constant.PostStringConstant.TEST_UUID_STRING;
import static kr.modusplant.domains.post.common.constant.PostUuidConstant.TEST_POST_UUID;
import static org.junit.jupiter.api.Assertions.*;

class SecondaryCategoryIdTest implements PostTestUtils {
    @Nested
    @DisplayName("SecondaryCategoryId  UUID 생성 테스트")
    class FromUuidTests {

        @Test
        @DisplayName("유효한 UUID로 SecondaryCategoryId 생성한다")
        void testFromUuid_givenUuid_willReturnSecondaryCategoryId() {
            assertNotNull(testSecondaryCategoryId);
            assertEquals(TEST_POST_UUID, testSecondaryCategoryId.getValue());
        }

        @Test
        @DisplayName("null UUID로 SecondaryCategoryId 생성 시 EmptyCategoryIdException을 발생시킨다")
        void testFromUuid_givenNullParameter_willThrowException() {
            // when & then
            assertThrows(EmptyCategoryIdException.class, () -> SecondaryCategoryId.fromUuid(null));
        }
    }

    @Nested
    @DisplayName("SecondaryCategoryId String 생성 테스트")
    class FromStringTests {

        @Test
        @DisplayName("유효한 UUID 문자열로 SecondaryCategoryId를 생성한다")
        void testFromString_givenValidString_willReturnSecondaryCategoryId() {
            // when
            SecondaryCategoryId secondaryCategoryId = SecondaryCategoryId.fromString(TEST_UUID_STRING);

            // then
            assertNotNull(secondaryCategoryId);
            assertEquals(UUID.fromString(TEST_UUID_STRING), secondaryCategoryId.getValue());
        }

        @Test
        @DisplayName("null 이나 빈 문자열로 SecondaryCategoryId 생성 시 EmptyCategoryIdException을 발생시킨다")
        void testFromString_givenNullOrEmptyParameter_willThrowException() {
            // when & then
            assertThrows(EmptyCategoryIdException.class, () -> SecondaryCategoryId.fromString(null));
            assertThrows(EmptyCategoryIdException.class, () -> SecondaryCategoryId.fromString(""));
            assertThrows(EmptyCategoryIdException.class, () -> SecondaryCategoryId.fromString("   "));
        }

        @Test
        @DisplayName("유효하지 않은 UUID 형식으로 SecondaryCategoryId 생성 시 InvalidCategoryIdException을 발생시킨다")
        void testFromString_givenInvalidParameter_willThrowException() {
            // given
            String[] invalidUuids = {
                    "550e8400-e29b-41d4-a716-44665544000", // 길이 부족
                    "550e8400-e29b-41d4-a716-4466554400000", // 길이 초과
                    "550e8400-e29b-41d4-a716", // 형식 불완전
                    "550e8400e29b41d4a716446655440000", // 하이픈 없음
                    "550g8400-e29b-41d4-a716-446655440000", // 유효하지 않은 문자 'g'
                    "550e8400-e29b-41d4-a716-44665544000z", // 유효하지 않은 문자 'z'
                    "not-a-uuid-at-all"
            };

            // when & then
            for (String invalidUuid : invalidUuids) {
                assertThrows(InvalidCategoryIdException.class, () -> SecondaryCategoryId.fromString(invalidUuid));
            }
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