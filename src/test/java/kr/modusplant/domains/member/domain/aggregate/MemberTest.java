package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.common.utils.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.exception.EmptyMemberBirthDateException;
import kr.modusplant.domains.member.domain.exception.EmptyMemberIdException;
import kr.modusplant.domains.member.domain.exception.EmptyMemberNicknameException;
import kr.modusplant.domains.member.domain.exception.EmptyMemberStatusException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest implements MemberTestUtils {
    @DisplayName("null 값으로 create(MemberId id, MemberStatus status, MemberNickname nickname, MemberBirthDate birthDate) 호출")
    @Test
    void testCreate_givenNullToOneOfFourParameters_willThrowException() {
        // MemberId가 null일 때
        // given
        EmptyMemberIdException memberIdException = assertThrows(EmptyMemberIdException.class, () -> Member.create(null, testMemberActiveStatus, testMemberNickname, testMemberBirthDate));

        // when & then
        assertThat(memberIdException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_ID);

        // MemberStatus가 null일 때
        // given
        EmptyMemberStatusException memberStatusException = assertThrows(EmptyMemberStatusException.class, () -> Member.create(testMemberId, null, testMemberNickname, testMemberBirthDate));

        // when & then
        assertThat(memberStatusException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_STATUS);

        // MemberNickname이 null일 때
        // given
        EmptyMemberNicknameException memberNicknameException = assertThrows(EmptyMemberNicknameException.class, () -> Member.create(testMemberId, testMemberActiveStatus, null, testMemberBirthDate));

        // when & then
        assertThat(memberNicknameException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_NICKNAME);

        // MemberBirthDate가 null일 때
        // given
        EmptyMemberBirthDateException memberBirthDateException = assertThrows(EmptyMemberBirthDateException.class, () -> Member.create(testMemberId, testMemberActiveStatus, testMemberNickname, null));

        // when & then
        assertThat(memberBirthDateException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_BIRTH_DATE);

    }

    @DisplayName("null 값으로 create(MemberId id, MemberStatus status, MemberNickname nickname) 호출")
    @Test
    void testCreate_givenNullToOneOfThreeParameters_willThrowException() {
        // MemberId가 null일 때
        // given
        EmptyMemberIdException memberIdException = assertThrows(EmptyMemberIdException.class, () -> Member.create(null, testMemberNickname));

        // when & then
        assertThat(memberIdException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_ID);

        // MemberNickname이 null일 때
        // given
        EmptyMemberNicknameException memberNicknameException = assertThrows(EmptyMemberNicknameException.class, () -> Member.create(testMemberId, null));

        // when & then
        assertThat(memberNicknameException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_NICKNAME);
    }

    @DisplayName("null 값으로 create(MemberNickname memberNickname) 호출")
    @Test
    void testCreate_givenNullToOneParameter_willThrowException() {
        // given
        EmptyMemberNicknameException exception = assertThrows(EmptyMemberNicknameException.class, () -> Member.create(null));

        // when & then
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_NICKNAME);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        // given
        Member member = createMember();

        // when & then
        //noinspection EqualsWithItself
        assertEquals(member, member);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(createMember(), testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        Member member = createMember();
        assertNotEquals(member, Member.create(testMemberNickname));
    }
}