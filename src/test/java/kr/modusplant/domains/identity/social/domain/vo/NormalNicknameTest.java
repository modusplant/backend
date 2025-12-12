package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.domains.identity.social.common.util.domain.vo.NicknameTestUtils;
import kr.modusplant.domains.identity.social.domain.exception.EmptyNicknameException;
import kr.modusplant.domains.identity.social.domain.exception.InvalidNicknameException;
import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_NICKNAME_STRING;
import static kr.modusplant.shared.kernel.common.util.EmailTestUtils.testKakaoUserEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NormalNicknameTest implements NicknameTestUtils {

    @Test
    @DisplayName("유효한 닉네임으로 Nickname을 생성")
    void testCreate_givenValidNickname_willReturnNickname() {
        // when
        Nickname nickname = Nickname.create(TEST_SOCIAL_KAKAO_NICKNAME_STRING);

        // then
        assertNotNull(nickname);
        assertEquals(TEST_SOCIAL_KAKAO_NICKNAME_STRING, nickname.getNickname());
    }

    @Test
    @DisplayName("null이나 빈 문자열 닉네임으로 생성 시 예외 발생")
    void testCreate_givenNullOrEmptyNickname_willThrowException() {
        // when & then
        EmptyNicknameException exception1 = assertThrows(EmptyNicknameException.class, () -> Nickname.create(null));
        EmptyNicknameException exception2 = assertThrows(EmptyNicknameException.class, () -> Nickname.create(""));
        EmptyNicknameException exception3 = assertThrows(EmptyNicknameException.class, () -> Nickname.create("   "));
        assertThat(exception1.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_NICKNAME);
        assertThat(exception2.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_NICKNAME);
        assertThat(exception3.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_NICKNAME);
    }

    @Test
    @DisplayName("유효하지 않은 닉네임 형식으로 생성 시 InvalidNicknameException을 발생시킨다")
    void testCreate_givenInvalidNicknameFormat_willThrowException() {
        // when & then
        InvalidNicknameException exception1 = assertThrows(InvalidNicknameException.class, () -> Nickname.create("invalid@nickname"));
        InvalidNicknameException exception2 = assertThrows(InvalidNicknameException.class, () -> Nickname.create("닉네임!"));
        assertThat(exception1.getErrorCode()).isEqualTo(SocialIdentityErrorCode.INVALID_NICKNAME);
        assertThat(exception2.getErrorCode()).isEqualTo(SocialIdentityErrorCode.INVALID_NICKNAME);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        assertEquals(testSocialKakaoNickname,testSocialKakaoNickname);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        assertNotEquals(testKakaoUserEmail,testSocialKakaoNickname);
    }

    @Test
    @DisplayName("다른 프로퍼티를 가진 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSocialKakaoNickname, testSocialGoogleNickname);
    }
}