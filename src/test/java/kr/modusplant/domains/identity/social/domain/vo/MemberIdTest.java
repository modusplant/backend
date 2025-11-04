package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.domains.identity.social.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.identity.social.domain.exception.EmptyMemberIdException;
import kr.modusplant.domains.identity.social.domain.exception.InvalidMemberIdException;
import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_MEMBER_UUID_STRING;
import static kr.modusplant.domains.identity.social.common.constant.SocialUuidConstant.TEST_SOCIAL_KAKAO_MEMBER_ID_UUID;
import static kr.modusplant.domains.identity.social.common.util.domain.vo.EmailTestUtils.testSocialKakaoEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberIdTest implements MemberIdTestUtils {

    @Test
    @DisplayName("유효한 UUID로 MemberId를 생성한다")
    void testFromUuid_givenValidUuid_willReturnMemberId() {
        // when & then
        assertNotNull(testSocialKakaoMemberId);
        assertEquals(TEST_SOCIAL_KAKAO_MEMBER_ID_UUID, testSocialKakaoMemberId.getValue());
    }

    @Test
    @DisplayName("null UUID로 생성 시 예외 발생")
    void testFromUuid_givenNull_willThrowException() {
        // when & then
        EmptyMemberIdException exception = assertThrows(EmptyMemberIdException.class, () -> MemberId.fromUuid(null));
        assertThat(exception.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_MEMBER_ID);
    }

    @Test
    @DisplayName("유효한 UUID 문자열로 MemberId를 생성")
    void testFromString_givenValidUuidString_willReturnMemberId() {
        // when
        MemberId memberId = MemberId.fromString(TEST_SOCIAL_KAKAO_MEMBER_UUID_STRING);

        // then
        assertNotNull(memberId);
        assertEquals(UUID.fromString(TEST_SOCIAL_KAKAO_MEMBER_UUID_STRING), memberId.getValue());
    }

    @Test
    @DisplayName("null이나 빈 문자열로 생성 시 예외 발생")
    void testFromString_givenNullOrEmpty_willThrowException() {
        // when & then
        EmptyMemberIdException exception1 = assertThrows(EmptyMemberIdException.class, () -> MemberId.fromString(null));
        EmptyMemberIdException exception2 = assertThrows(EmptyMemberIdException.class, () -> MemberId.fromString(""));
        EmptyMemberIdException exception3 = assertThrows(EmptyMemberIdException.class, () -> MemberId.fromString("   "));
        assertThat(exception1.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_MEMBER_ID);
        assertThat(exception2.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_MEMBER_ID);
        assertThat(exception3.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_MEMBER_ID);
    }

    @Test
    @DisplayName("유효하지 않은 UUID 형식으로 생성 시 InvalidMemberIdException을 발생시킨다")
    void testFromString_givenInvalidUuidFormat_willThrowException() {
        // when & then
        InvalidMemberIdException exception1 = assertThrows(InvalidMemberIdException.class, () -> MemberId.fromString("invalid-uuid"));
        InvalidMemberIdException exception2 = assertThrows(InvalidMemberIdException.class, () -> MemberId.fromString("12345"));
        assertThat(exception1.getErrorCode()).isEqualTo(SocialIdentityErrorCode.INVALID_MEMBER_ID);
        assertThat(exception2.getErrorCode()).isEqualTo(SocialIdentityErrorCode.INVALID_MEMBER_ID);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        assertEquals(testSocialKakaoMemberId,testSocialKakaoMemberId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        assertNotEquals(testSocialKakaoEmail,testSocialKakaoMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 가진 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSocialKakaoMemberId, testSocialGoogleMemberId);
    }

}