package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberBirthDateTestUtils.testMemberBirthDate;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberStatusTestUtils.testMemberActiveStatus;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest implements MemberTestUtils {
    @DisplayName("null 값으로 create 호출")
    @Test
    void testCreate_givenNullToOneOfFourParameters_willThrowException() {
        // MemberId가 null일 때
        // given
        EmptyValueException memberIdException = assertThrows(EmptyValueException.class, () -> Member.create(null, testMemberActiveStatus, testNormalUserNickname, testMemberBirthDate));

        // when & then
        assertThat(memberIdException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_ID);

        // MemberStatus가 null일 때
        // given
        EmptyValueException memberStatusException = assertThrows(EmptyValueException.class, () -> Member.create(testMemberId, null, testNormalUserNickname, testMemberBirthDate));

        // when & then
        assertThat(memberStatusException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_STATUS);

        // Nickname이 null일 때
        // given
        EmptyValueException nicknameException = assertThrows(EmptyValueException.class, () -> Member.create(testMemberId, testMemberActiveStatus, null, testMemberBirthDate));

        // when & then
        assertThat(nicknameException.getErrorCode()).isEqualTo(KernelErrorCode.EMPTY_NICKNAME);

        // MemberBirthDate가 null일 때
        // given
        EmptyValueException memberBirthDateException = assertThrows(EmptyValueException.class, () -> Member.create(testMemberId, testMemberActiveStatus, testNormalUserNickname, null));

        // when & then
        assertThat(memberBirthDateException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_BIRTH_DATE);
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
        assertNotEquals(member, Member.create(MemberId.generate(), testMemberActiveStatus, testNormalUserNickname, testMemberBirthDate));
    }
}