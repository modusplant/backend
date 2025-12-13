package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.domains.identity.social.common.util.domain.vo.SocialAccountProfileTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SocialAccountProfileTest implements SocialAccountProfileTestUtils {

    @Test
    @DisplayName("유효한 SocialCredentials, Email, Nickname으로 SocialUserProfile 생성")
    void testCreate_givenValidParameters_willReturnSocialUserProfile() {
        // when
        SocialAccountProfile profile = SocialAccountProfile.create(testKakaoSocialCredentials, testKakaoUserEmail, testKakaoUserNickname);

        // then
        assertNotNull(profile);
        assertEquals(testKakaoSocialCredentials, profile.getSocialCredentials());
        assertEquals(testKakaoUserEmail, profile.getEmail());
        assertEquals(testKakaoUserNickname, profile.getNickname());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        assertEquals(TEST_KAKAO_SOCIAL_ACCOUNT_PROFILE, TEST_KAKAO_SOCIAL_ACCOUNT_PROFILE);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        assertNotEquals(testKakaoUserEmail, TEST_KAKAO_SOCIAL_ACCOUNT_PROFILE);
    }

    @Test
    @DisplayName("다른 프로퍼티를 가진 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(TEST_KAKAO_SOCIAL_ACCOUNT_PROFILE, TEST_GOOGLE_SOCIAL_ACCOUNT_PROFILE);
    }

}