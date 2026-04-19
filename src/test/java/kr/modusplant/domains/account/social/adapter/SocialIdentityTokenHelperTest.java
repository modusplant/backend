package kr.modusplant.domains.account.social.adapter;

import io.jsonwebtoken.Claims;
import kr.modusplant.domains.account.social.common.util.usecase.record.TempTokenInfoTestUtils;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.record.TempTokenInfo;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SocialIdentityTokenHelperTest implements TempTokenInfoTestUtils {
    @InjectMocks
    private SocialIdentityTokenHelper socialIdentityTokenHelper;
    @Mock
    private JwtTokenProvider tokenProvider;

    @Test
    @DisplayName("임시 토큰 생성 성공")
    void testGenerateTempToken_givenTokenValue_willReturnTempToken() {
        // given
        String tempToken = "temp-token";
        String email = createKakaoTempTokenInfo().email();
        String providerId = createKakaoTempTokenInfo().providerId();
        SocialProvider provider = createKakaoTempTokenInfo().socialProvider();
        String socialAccessToken = "access-token";
        long durationMs = 1000L;
        given(tokenProvider.generateToken(anyString(),any(),anyLong())).willReturn(tempToken);

        // when
        String result = socialIdentityTokenHelper.generateTempToken(email,providerId,provider,socialAccessToken,durationMs);

        // then
        assertThat(result).isEqualTo(tempToken);
        then(tokenProvider).should().generateToken(
                eq(providerId),
                argThat(claims ->
                        claims.get("email").equals(email) &&
                                claims.get("providerId").equals(providerId) &&
                                claims.get("socialProvider").equals(provider.name()) &&
                                claims.get("socialAccessToken").equals(socialAccessToken)
                ),
                eq(durationMs)
        );
    }

    @Test
    @DisplayName("임시 토큰에서 값 추출하기")
    void testGetTempTokenInfoFromClaims_givenTempToken_willReturnTempTokenInfo() {
        // given
        TempTokenInfo tempTokenInfo = createKakaoTempTokenInfo();
        String tempToken = "temp-token";
        Claims claims = mock(Claims.class);
        given(tokenProvider.getClaimsFromToken(tempToken)).willReturn(claims);
        given(claims.get("email",String.class)).willReturn(tempTokenInfo.email());
        given(claims.get("providerId",String.class)).willReturn(tempTokenInfo.providerId());
        given(claims.get("socialProvider", String.class)).willReturn(tempTokenInfo.socialProvider().name());

        // when
        TempTokenInfo result = socialIdentityTokenHelper.getTempTokenInfoFromClaims(tempToken);

        // then
        assertEquals(result.email(), tempTokenInfo.email());
        assertEquals(result.providerId(), tempTokenInfo.providerId());
        assertEquals(result.socialProvider(), tempTokenInfo.socialProvider());
    }

    @Test
    @DisplayName("임시 토큰에서 소셜 접근 토큰 추출하기")
    void testGetSocialAccessTokenFromClaims_givenTempToken_willReturnSocialAccessToken() {
        // given
        String tempToken = "temp-token";
        String socialAccessToken = "kakao-access-token";
        Claims claims = mock(Claims.class);
        given(tokenProvider.getClaimsFromToken(tempToken)).willReturn(claims);
        given(claims.get("socialAccessToken",String.class)).willReturn(socialAccessToken);

        // when
        String result = socialIdentityTokenHelper.getSocialAccessTokenFromClaims(tempToken);

        // then
        assertEquals(result,socialAccessToken);
    }

    @Test
    @DisplayName("임시 토큰에서 소셜 provider 추출하기")
    void testGetSocialProviderFromClaims_givenTempToken_willReturnSocialProvider() {
        // given
        String tempToken = "temp-token";
        Claims claims = mock(Claims.class);
        given(tokenProvider.getClaimsFromToken(tempToken)).willReturn(claims);
        given(claims.get("socialProvider", String.class)).willReturn(SocialProvider.KAKAO.name());

        // when
        SocialProvider result = socialIdentityTokenHelper.getSocialProviderFromClaims(tempToken);

        // then
        assertEquals(result,SocialProvider.KAKAO);

    }

}