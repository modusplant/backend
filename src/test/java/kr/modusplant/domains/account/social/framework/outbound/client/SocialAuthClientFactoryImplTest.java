package kr.modusplant.domains.account.social.framework.outbound.client;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SocialAuthClientFactoryImplTest {

    @Mock
    private KakaoAuthClient kakaoAuthClient;

    @Mock
    private GoogleAuthClient googleAuthClient;

    private SocialAuthClientFactoryImpl socialAuthClientFactory;

    @BeforeEach
    void setUp() {
        // 생성자 시점에 stream 조립 로직이 돌기 때문에 Mock 객체들이 어떤 Provider인지 미리 알려주어야 함
        given(kakaoAuthClient.getProvider()).willReturn(SocialProvider.KAKAO);
        given(googleAuthClient.getProvider()).willReturn(SocialProvider.GOOGLE);

        // List에 담아서 수동으로 팩토리를 주입,생성 (@InjectMocks 제거)
        socialAuthClientFactory = new SocialAuthClientFactoryImpl(List.of(kakaoAuthClient, googleAuthClient));
    }

    @Test
    @DisplayName("KAKAO provider로 KakaoAuthClient를 반환한다")
    void testGetClient_givenKakaoProvider_willReturnKakaoAuthClient() {
        // when
        SocialAuthClient client = socialAuthClientFactory.getClient(SocialProvider.KAKAO);

        // then
        assertNotNull(client);
        assertEquals(kakaoAuthClient, client);
    }

    @Test
    @DisplayName("GOOGLE provider로 GoogleAuthClient를 반환한다")
    void testGetClient_givenGoogleProvider_willReturnGoogleAuthClient() {
        // when
        SocialAuthClient client = socialAuthClientFactory.getClient(SocialProvider.GOOGLE);

        // then
        assertNotNull(client);
        assertEquals(googleAuthClient, client);
    }

}