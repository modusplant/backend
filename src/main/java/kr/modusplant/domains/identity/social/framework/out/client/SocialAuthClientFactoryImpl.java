package kr.modusplant.domains.identity.social.framework.out.client;

import kr.modusplant.domains.identity.social.framework.out.exception.UnsupportedSocialProviderException;
import kr.modusplant.domains.identity.social.usecase.port.client.SocialAuthClient;
import kr.modusplant.domains.identity.social.usecase.port.client.SocialAuthClientFactory;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialAuthClientFactoryImpl implements SocialAuthClientFactory {
    private final KakaoAuthClient kakaoAuthClient;
    private final GoogleAuthClient googleAuthClient;

    @Override
    public SocialAuthClient getClient(AuthProvider provider) {
        return switch (provider) {
            case KAKAO -> kakaoAuthClient;
            case GOOGLE -> googleAuthClient;
            default -> throw new UnsupportedSocialProviderException();
        };
    }
}
