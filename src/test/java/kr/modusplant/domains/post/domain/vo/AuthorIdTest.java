package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_AUTHOR_ID_STRING;
import static org.junit.jupiter.api.Assertions.*;

class AuthorIdTest implements PostTestUtils {

    @Nested
    @DisplayName("AuthorId UUID 생성 테스트")
    class FromUuidTests {

        @Test
        @DisplayName("유효한 UUID로 AuthorId 반환")
        void testFromUuid_givenUuid_willReturnAuthorId() {
            assertNotNull(testAuthorId);
            assertEquals(MEMBER_BASIC_USER_UUID, testAuthorId.getValue());
        }

        @Test
        @DisplayName("null UUID일 때 예외 반환")
        void testFromUuid_givenNullParameter_willThrowException() {
            // when & then
            assertThrows(EmptyValueException.class, () -> AuthorId.fromUuid(null));
        }
    }

    @Nested
    @DisplayName("AuthorId String 생성 테스트")
    class FromStringTests {

        @Test
        @DisplayName("유효한 UUID 문자열로 AuthorId 반환")
        void testFromString_givenValidString_willReturnAuthorId() {
            // when
            AuthorId authorId = AuthorId.fromString(TEST_AUTHOR_ID_STRING);

            // then
            assertNotNull(authorId);
            assertEquals(UUID.fromString(TEST_AUTHOR_ID_STRING), authorId.getValue());
        }

        @Test
        @DisplayName("null이나 빈 문자열일 때 예외 반환")
        void testFromString_givenNullOrEmptyParameter_willThrowException() {
            // when & then
            assertThrows(EmptyValueException.class, () -> AuthorId.fromString(null));
            assertThrows(EmptyValueException.class, () -> AuthorId.fromString(""));
            assertThrows(EmptyValueException.class, () -> AuthorId.fromString("   "));
        }

        @Test
        @DisplayName("유효하지 않은 형식일 때 예외 반환")
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
                assertThrows(InvalidValueException.class, () -> AuthorId.fromString(invalidUuid));
            }
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체로 참 반환")
        void testEquals_givenSameObject_willReturnTrue() {
            // when & then
            //noinspection EqualsWithItself
            assertEquals(testAuthorId, testAuthorId);
            assertEquals(testAuthorId.hashCode(), testAuthorId.hashCode());
        }

        @Test
        @DisplayName("다른 클래스 인스턴스로 거짓 반환")
        void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testAuthorId,testPostId);
        }

        @Test
        @DisplayName("다른 프로퍼티 인스턴스로 거짓 반환")
        void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testAuthorId, testAuthorId2);
        }

    }

}