package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.utils.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.EmptyAuthorIdException;
import kr.modusplant.domains.post.domain.exception.InvalidAuthorIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.post.common.constant.PostStringConstant.TEST_UUID_STRING;
import static kr.modusplant.domains.post.common.constant.PostUuidConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthorIdTest implements PostTestUtils {

    @Nested
    @DisplayName("AuthorId UUID 생성 테스트")
    class FromUuidTests {

        @Test
        @DisplayName("유효한 UUID로 AuthorId를 생성한다")
        void testFromUuid_givenUuid_willReturnAuthorId() {
            assertNotNull(testAuthorId);
            assertEquals(TEST_POST_UUID, testAuthorId.getValue());
        }

        @Test
        @DisplayName("null UUID로 AuthorId 생성 시 EmptyAuthorIdException을 발생시킨다")
        void testFromUuid_givenNullParameter_willThrowException() {
            // when & then
            assertThrows(EmptyAuthorIdException.class, () -> AuthorId.fromUuid(null));
        }
    }

    @Nested
    @DisplayName("AuthorId String 생성 테스트")
    class FromStringTests {

        @Test
        @DisplayName("유효한 UUID 문자열로 AuthorId를 생성한다")
        void testFromString_givenValidString_willReturnAuthorId() {
            // when
            AuthorId authorId = AuthorId.fromString(TEST_UUID_STRING);

            // then
            assertNotNull(authorId);
            assertEquals(UUID.fromString(TEST_UUID_STRING), authorId.getValue());
        }

        @Test
        @DisplayName("null 이나 빈 문자열로 AuthorId 생성 시 EmptyAuthorIdException을 발생시킨다")
        void testFromString_givenNullOrEmptyParameter_willThrowException() {
            // when & then
            assertThrows(EmptyAuthorIdException.class, () -> AuthorId.fromString(null));
            assertThrows(EmptyAuthorIdException.class, () -> AuthorId.fromString(""));
            assertThrows(EmptyAuthorIdException.class, () -> AuthorId.fromString("   "));
        }

        @Test
        @DisplayName("유효하지 않은 UUID 형식으로 AuthorId 생성 시 InvalidAuthorIdException을 발생시킨다")
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
                assertThrows(InvalidAuthorIdException.class, () -> AuthorId.fromString(invalidUuid));
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
            assertEquals(testAuthorId, testAuthorId);
            assertEquals(testAuthorId.hashCode(), testAuthorId.hashCode());
        }

        @Test
        @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testAuthorId,testPostId);
        }

        @Test
        @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testAuthorId, testAuthorId2);
        }

    }

}