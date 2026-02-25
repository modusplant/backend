package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberBirthDateTestUtils.testMemberBirthDate;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_TARGET_POST_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TargetPostIdTest {
    @Test
    @DisplayName("create으로 대상 게시글 아이디 반환")
    void testCreate_givenValidValue_willReturnTargetPostId() {
        assertNotNull(TargetPostId.create(TEST_COMM_POST_ULID).getValue());
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> TargetPostId.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_TARGET_POST_ID);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> TargetPostId.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_TARGET_POST_ID);
    }

    @Test
    @DisplayName("정규 표현식에 매칭되지 않는 값으로 create을 호출하여 오류 발생")
    void testCreate_givenInvalidId_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> TargetPostId.create("!유효하지않음!"));
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_TARGET_POST_ID);
        assertThat(exception.getValueName()).isEqualTo("targetPostId");
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testTargetPostId, testTargetPostId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testTargetPostId, testMemberBirthDate);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testTargetPostId, "1".repeat(16));
    }
}