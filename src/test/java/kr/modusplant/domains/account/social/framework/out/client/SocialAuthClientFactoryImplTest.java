package kr.modusplant.domains.account.social.framework.out.client;

import kr.modusplant.domains.account.social.framework.out.exception.UnsupportedSocialProviderException;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClient;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SocialAuthClientFactoryImplTest {

    @Mock
    private KakaoAuthClient kakaoAuthClient;

    @Mock
    private GoogleAuthClient googleAuthClient;

    @InjectMocks
    private SocialAuthClientFactoryImpl socialAuthClientFactory;

    @Test
    @DisplayName("KAKAO provider로 KakaoAuthClient를 반환한다")
    void testGetClient_givenKakaoProvider_willReturnKakaoAuthClient() {
        // when
        SocialAuthClient client = socialAuthClientFactory.getClient(AuthProvider.KAKAO);

        // then
        assertNotNull(client);
        assertEquals(kakaoAuthClient, client);
    }

    @Test
    @DisplayName("GOOGLE provider로 GoogleAuthClient를 반환한다")
    void testGetClient_givenGoogleProvider_willReturnGoogleAuthClient() {
        // when
        SocialAuthClient client = socialAuthClientFactory.getClient(AuthProvider.GOOGLE);

        // then
        assertNotNull(client);
        assertEquals(googleAuthClient, client);
    }

    @Test
    @DisplayName("BASIC provider로 호출 시 UnsupportedSocialProviderException을 발생시킨다")
    void testGetClient_givenBasicProvider_willThrowException() {
        // when & then
        UnsupportedSocialProviderException exception = assertThrows(UnsupportedSocialProviderException.class, () -> socialAuthClientFactory.getClient(AuthProvider.BASIC));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }


}