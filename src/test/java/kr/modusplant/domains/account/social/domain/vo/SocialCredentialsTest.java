package kr.modusplant.domains.account.social.domain.vo;

import kr.modusplant.domains.account.social.common.util.domain.vo.SocialCredentialsTestUtils;
import kr.modusplant.domains.account.social.domain.exception.EmptyValueException;
import kr.modusplant.domains.account.social.domain.exception.InvalidValueException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.kernel.common.util.EmailTestUtils.testKakaoUserEmail;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_KAKAO_USER_PROVIDER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SocialCredentialsTest implements SocialCredentialsTestUtils {

    @Test
    @DisplayName("유효한 provider와 providerId로 SocialCredentials를 생성한다")
    void testCreate_givenValidProviderAndProviderId_willReturnSocialCredentials() {
        // when & then
        SocialCredentials kakaoCredentials = SocialCredentials.create(AuthProvider.KAKAO, MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
        SocialCredentials googleCredentials = SocialCredentials.create(AuthProvider.GOOGLE, MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);

        assertNotNull(kakaoCredentials);
        assertThat(kakaoCredentials.getProvider()).isEqualTo(AuthProvider.KAKAO);
        assertThat(kakaoCredentials.getProviderId()).isEqualTo(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);

        assertNotNull(googleCredentials);
        assertThat(googleCredentials.getProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(googleCredentials.getProviderId()).isEqualTo(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
    }

    @Test
    @DisplayName("빈 provider와 providerId로 생성 시 예외 발생")
    void testCreate_givenNullProviderOrProviderId_willThrowException() {
        // when & then
        EmptyValueException providerException = assertThrows(EmptyValueException.class, () -> SocialCredentials.create(null, MEMBER_AUTH_KAKAO_USER_PROVIDER_ID));
        EmptyValueException providerIdException1 = assertThrows(EmptyValueException.class, () -> SocialCredentials.create(AuthProvider.KAKAO, null));
        EmptyValueException providerIdException2 = assertThrows(EmptyValueException.class, () -> SocialCredentials.create(AuthProvider.KAKAO, ""));
        EmptyValueException providerIdException3 = assertThrows(EmptyValueException.class, () -> SocialCredentials.create(AuthProvider.KAKAO, "   "));

        assertThat(providerException.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_PROVIDER);
        assertThat(providerIdException1.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_PROVIDER_ID);
        assertThat(providerIdException2.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_PROVIDER_ID);
        assertThat(providerIdException3.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_PROVIDER_ID);
    }

    @Test
    @DisplayName("BASIC provider로 생성 시 예외 발생")
    void testCreate_givenBasicProvider_willThrowException() {
        // when & then
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> SocialCredentials.create(AuthProvider.BASIC, "12345"));
        assertThat(exception.getErrorCode()).isEqualTo(SocialIdentityErrorCode.INVALID_PROVIDER_ID);
    }

    @Test
    @DisplayName("유효하지 않은 길이의 providerId로 생성 시 예외 발생")
    void testCreate_givenInvalidProviderIdLength_willThrowException() {
        // when & then
        InvalidValueException kakaoException = assertThrows(InvalidValueException.class, () -> SocialCredentials.create(AuthProvider.KAKAO, "123456789"));
        InvalidValueException googleException = assertThrows(InvalidValueException.class, () -> SocialCredentials.create(AuthProvider.GOOGLE, "12345678901234567890"));
        assertThat(kakaoException.getErrorCode()).isEqualTo(SocialIdentityErrorCode.INVALID_PROVIDER_ID);
        assertThat(googleException.getErrorCode()).isEqualTo(SocialIdentityErrorCode.INVALID_PROVIDER_ID);
    }

    @Test
    @DisplayName("유효한 Kakao providerId로 SocialCredentials를 생성한다")
    void testCreateKakao_givenValidProviderId_willReturnSocialCredentials() {
        // when
        SocialCredentials credentials = SocialCredentials.createKakao(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);

        // then
        assertNotNull(credentials);
        assertThat(AuthProvider.KAKAO).isEqualTo(credentials.getProvider());
        assertThat(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID).isEqualTo(credentials.getProviderId());
        assertTrue(credentials.isKakao());
    }

    @Test
    @DisplayName("유효한 Google providerId로 SocialCredentials를 생성한다")
    void testCreateGoogle_givenValidProviderId_willReturnSocialCredentials() {
        // when
        SocialCredentials credentials = SocialCredentials.createGoogle(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);

        // then
        assertNotNull(credentials);
        assertThat(AuthProvider.GOOGLE).isEqualTo(credentials.getProvider());
        assertThat(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID).isEqualTo(credentials.getProviderId());
        assertTrue(credentials.isGoogle());
    }

    @Test
    @DisplayName("유효한 providerId로 BasicKakao SocialCredentials를 생성한다")
    void testCreateBasicKakao_givenValidProviderId_willReturnSocialCredentials() {
        // when
        SocialCredentials credentials = SocialCredentials.createBasicKakao(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);

        // then
        assertNotNull(credentials);
        assertThat(credentials.getProvider()).isEqualTo(AuthProvider.BASIC_KAKAO);
        assertThat(credentials.getProviderId()).isEqualTo(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
        assertTrue(credentials.isKakao());
        assertTrue(credentials.isLinked());
    }

    @Test
    @DisplayName("유효한 providerId로 BasicGoogle SocialCredentials를 생성한다")
    void testCreateBasicGoogle_givenValidProviderId_willReturnSocialCredentials() {
        // when
        SocialCredentials credentials = SocialCredentials.createBasicGoogle(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);

        // then
        assertNotNull(credentials);
        assertThat(credentials.getProvider()).isEqualTo(AuthProvider.BASIC_GOOGLE);
        assertThat(credentials.getProviderId()).isEqualTo(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
        assertTrue(credentials.isGoogle());
        assertTrue(credentials.isLinked());
    }


    @Test
    @DisplayName("isKakao로 Kakao provider 확인")
    void testIsKakao_givenKakaoProvider_willReturnTrue() {
        // when & then
        assertTrue(testKakaoSocialCredentials.isKakao());
        assertFalse(testGoogleSocialCredentials.isKakao());
        assertTrue(testBasicKakaoSocialCredentials.isKakao());
        assertFalse(testBasicGoogleSocialCredentials.isKakao());
    }

    @Test
    @DisplayName("isGoogle로 Google provider 확인")
    void testIsGoogle_givenGoogleProvider_willReturnTrue() {
        // when & then
        assertTrue(testGoogleSocialCredentials.isGoogle());
        assertFalse(testKakaoSocialCredentials.isGoogle());
        assertTrue(testBasicGoogleSocialCredentials.isGoogle());
        assertFalse(testBasicKakaoSocialCredentials.isGoogle());
    }

    @Test
    @DisplayName("isLinked로 연동된 provider 확인")
    void testIsLinked_givenLinkedProvider_willReturnTrue() {
        // when & then
        assertTrue(testBasicKakaoSocialCredentials.isLinked());
        assertTrue(testBasicGoogleSocialCredentials.isLinked());
        assertFalse(testKakaoSocialCredentials.isLinked());
        assertFalse(testGoogleSocialCredentials.isLinked());
    }

    @Test
    @DisplayName("isPureBasic으로 BASIC provider 확인")
    void testIsPureBasic_givenBasicProvider_willReturnTrue() {
        // when & then
        assertTrue(testBasicSocialCredentials.isPureBasic());
        assertFalse(testKakaoSocialCredentials.isPureBasic());
        assertFalse(testGoogleSocialCredentials.isPureBasic());
        assertFalse(testBasicKakaoSocialCredentials.isPureBasic());
        assertFalse(testBasicGoogleSocialCredentials.isPureBasic());
    }

    @Test
    @DisplayName("isPureSocial로 Social provider 확인")
    void testIsPureSocial_givenSocialProvider_willReturnTrue() {
        // when & then
        assertFalse(testBasicSocialCredentials.isPureSocial());
        assertTrue(testKakaoSocialCredentials.isPureSocial());
        assertTrue(testGoogleSocialCredentials.isPureSocial());
        assertFalse(testBasicKakaoSocialCredentials.isPureSocial());
        assertFalse(testBasicGoogleSocialCredentials.isPureSocial());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        assertEquals(testGoogleSocialCredentials,testGoogleSocialCredentials);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        assertNotEquals(testKakaoUserEmail,testGoogleSocialCredentials);
    }

    @Test
    @DisplayName("다른 프로퍼티를 가진 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testKakaoSocialCredentials, testGoogleSocialCredentials);
    }


}