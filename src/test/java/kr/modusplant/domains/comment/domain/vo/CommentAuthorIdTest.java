package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentAuthorIdTestUtils;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyCommentAuthorId;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommentAuthorIdTest implements CommentAuthorIdTestUtils {

    @Test
    @DisplayName("유효한 UUID로 작성자 id 생성 시 CommentAuthorId 반환")
    void testFromUuid_givenValidUuid_willReturnCommentAuthorId() {
        // when
        CommentAuthorId result = CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result.getValue()).isEqualTo(MEMBER_BASIC_USER_UUID);
    }

    @Test
    @DisplayName("null인 UUID로 작성자 id 생성 시 예외 반환")
    void testFromUuid_givenNull_willThrowException() {
        // when
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> CommentAuthorId.fromUuid(null));

        // then
        assertEquals(CommentErrorCode.EMPTY_COMMENT_AUTHOR, result.getErrorCode());
    }

    @Test
    @DisplayName("null인 UUID로 null 허용 작성자 id 생성 시 EmptyCommentAuthorId 반환")
    void testFromNullableUuid_givenNull_willReturnEmptyCommentAuthorId() {
        // when
        CommentAuthorId result = CommentAuthorId.fromNullableUuid(null);

        // then
        assertThat(result).isSameAs(EmptyCommentAuthorId.create());
    }

    @Test
    @DisplayName("유효한 UUID로 null 허용 작성자 id 생성 시 CommentAuthorId 반환")
    void testFromNullableUuid_givenValidUuid_willReturnCommentAuthorId() {
        // when
        CommentAuthorId result = CommentAuthorId.fromNullableUuid(MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isEqualTo(testCommentAuthorId);
    }

    @Test
    @DisplayName("유효한 문자열로 작성자 id 생성 시 CommentAuthorId 반환")
    void testFromString_givenValidValue_willReturnCommentAuthorId() {
        // when
        CommentAuthorId result = CommentAuthorId.fromString(MEMBER_BASIC_USER_UUID_STRING);

        // then
        assertThat(result).isEqualTo(testCommentAuthorId);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("비어 있는 문자열로 작성자 id 생성 시 예외 반환")
    void testFromString_givenBlankValue_willThrowException(String value) {
        // when
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> CommentAuthorId.fromString(value));

        // then
        assertEquals(CommentErrorCode.EMPTY_COMMENT_AUTHOR, result.getErrorCode());
    }

    @Test
    @DisplayName("UUID 형식이 아닌 문자열로 작성자 id 생성 시 예외 반환")
    void testFromString_givenInvalidFormat_willThrowException() {
        // when
        InvalidValueException result = assertThrows(InvalidValueException.class, () -> CommentAuthorId.fromString("invalid-uuid"));

        // then
        assertEquals(CommentErrorCode.INVALID_COMMENT_AUTHOR_ID, result.getErrorCode());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testCommentAuthorId, testCommentAuthorId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testCommentAuthorId, UUID.randomUUID());
    }

    @Test
    @DisplayName("다른 값을 가진 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testCommentAuthorId, CommentAuthorId.fromUuid(MEMBER_BASIC_ADMIN_UUID));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testCommentAuthorId.hashCode(), testCommentAuthorId.hashCode());
    }
}
