package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.shared.exception.EmptyNicknameException;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.entity.MemberProfileImageTestUtils.testMemberProfileImage;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileIntroductionTestUtils.testMemberProfileIntroduction;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberProfileTest implements MemberProfileTestUtils {
    @DisplayName("null 값으로 create 호출")
    @Test
    void testCreate_givenNullToOneOfFourParameters_willThrowException() {
        // MemberId가 null일 때
        // given
        EmptyValueException memberIdException = assertThrows(EmptyValueException.class, () -> MemberProfile.create(null, testMemberProfileImage, testMemberProfileIntroduction, testNormalUserNickname));

        // when & then
        assertThat(memberIdException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_ID);

        // MemberProfileImage가 null일 때
        // given
        EmptyValueException emptyMemberProfileImageException = assertThrows(EmptyValueException.class, () -> MemberProfile.create(testMemberId, null, testMemberProfileIntroduction, testNormalUserNickname));

        // when & then
        assertThat(emptyMemberProfileImageException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE);

        // MemberProfileIntroduction이 null일 때
        // given
        EmptyValueException memberProfileIntroductionException = assertThrows(EmptyValueException.class, () -> MemberProfile.create(testMemberId, testMemberProfileImage, null, testNormalUserNickname));

        // when & then
        assertThat(memberProfileIntroductionException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_INTRODUCTION);

        // Nickname이 null일 때
        // given
        EmptyNicknameException nicknameException = assertThrows(EmptyNicknameException.class, () -> MemberProfile.create(testMemberId, testMemberProfileImage, testMemberProfileIntroduction, null));

        // when & then
        assertThat(nicknameException.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_EMPTY);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        // given
        MemberProfile memberProfile = createMemberProfile();

        // when & then
        //noinspection EqualsWithItself
        assertEquals(memberProfile, memberProfile);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(createMemberProfile(), testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        MemberProfile memberProfile = createMemberProfile();
        assertNotEquals(memberProfile, MemberProfile.create(MemberId.generate(), testMemberProfileImage, testMemberProfileIntroduction, testNormalUserNickname));
    }
}