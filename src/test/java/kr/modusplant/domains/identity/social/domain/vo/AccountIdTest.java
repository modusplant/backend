package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.domains.identity.shared.exception.EmptyAccountIdException;
import kr.modusplant.domains.identity.shared.exception.InvalidAccountIdException;
import kr.modusplant.domains.identity.shared.exception.enums.AccountErrorCode;
import kr.modusplant.domains.identity.shared.kernel.AccountId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.identity.shared.kernel.common.util.AccountIdTestUtils.testGoogleAccountId;
import static kr.modusplant.domains.identity.shared.kernel.common.util.AccountIdTestUtils.testKakaoAccountId;
import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_MEMBER_UUID_STRING;
import static kr.modusplant.domains.identity.social.common.constant.SocialUuidConstant.TEST_SOCIAL_KAKAO_MEMBER_ID_UUID;
import static kr.modusplant.shared.kernel.common.util.EmailTestUtils.testKakaoUserEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AccountIdTest {

    @Test
    @DisplayName("유효한 UUID로 MemberId를 생성한다")
    void testFromUuid_givenValidUuid_willReturnMemberId() {
        // when & then
        assertNotNull(testKakaoAccountId);
        assertEquals(TEST_SOCIAL_KAKAO_MEMBER_ID_UUID, testKakaoAccountId.getValue());
    }

    @Test
    @DisplayName("null UUID로 생성 시 예외 발생")
    void testFromUuid_givenNull_willThrowException() {
        // when & then
        EmptyAccountIdException exception = assertThrows(EmptyAccountIdException.class, () -> AccountId.fromUuid(null));
        assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.EMPTY_ACCOUNT_ID);
    }

    @Test
    @DisplayName("유효한 UUID 문자열로 MemberId를 생성")
    void testFromString_givenValidUuidString_willReturnMemberId() {
        // when
        AccountId accountId = AccountId.fromString(TEST_SOCIAL_KAKAO_MEMBER_UUID_STRING);

        // then
        assertNotNull(accountId);
        assertEquals(UUID.fromString(TEST_SOCIAL_KAKAO_MEMBER_UUID_STRING), accountId.getValue());
    }

    @Test
    @DisplayName("null이나 빈 문자열로 생성 시 예외 발생")
    void testFromString_givenNullOrEmpty_willThrowException() {
        // when & then
        EmptyAccountIdException exception1 = assertThrows(EmptyAccountIdException.class, () -> AccountId.fromString(null));
        EmptyAccountIdException exception2 = assertThrows(EmptyAccountIdException.class, () -> AccountId.fromString(""));
        EmptyAccountIdException exception3 = assertThrows(EmptyAccountIdException.class, () -> AccountId.fromString("   "));
        assertThat(exception1.getErrorCode()).isEqualTo(AccountErrorCode.EMPTY_ACCOUNT_ID);
        assertThat(exception2.getErrorCode()).isEqualTo(AccountErrorCode.EMPTY_ACCOUNT_ID);
        assertThat(exception3.getErrorCode()).isEqualTo(AccountErrorCode.EMPTY_ACCOUNT_ID);
    }

    @Test
    @DisplayName("유효하지 않은 UUID 형식으로 생성 시 InvalidMemberIdException을 발생시킨다")
    void testFromString_givenInvalidUuidFormat_willThrowException() {
        // when & then
        InvalidAccountIdException exception1 = assertThrows(InvalidAccountIdException.class, () -> AccountId.fromString("invalid-uuid"));
        InvalidAccountIdException exception2 = assertThrows(InvalidAccountIdException.class, () -> AccountId.fromString("12345"));
        assertThat(exception1.getErrorCode()).isEqualTo(AccountErrorCode.INVALID_ACCOUNT_ID);
        assertThat(exception2.getErrorCode()).isEqualTo(AccountErrorCode.INVALID_ACCOUNT_ID);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        assertEquals(testKakaoAccountId, testKakaoAccountId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        assertNotEquals(testKakaoUserEmail, testKakaoAccountId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 가진 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testKakaoAccountId, testGoogleAccountId);
    }

}