package kr.modusplant.domains.account.social.framework.in.web.rest;

import kr.modusplant.domains.account.social.adapter.SocialIdentityTokenHelper;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityController;
import kr.modusplant.domains.account.social.common.util.usecase.record.TempTokenInfoTestUtils;
import kr.modusplant.domains.account.social.common.util.usecase.request.SocialAuthRequestTestUtils;
import kr.modusplant.domains.account.social.common.util.usecase.request.SocialSignUpRequestTestUtils;
import kr.modusplant.domains.account.social.common.util.usecase.response.SocialLoginResultTestUtils;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.enums.OAuthType;
import kr.modusplant.domains.account.social.usecase.record.TempTokenInfo;
import kr.modusplant.domains.account.social.usecase.request.SocialAuthRequest;
import kr.modusplant.domains.account.social.usecase.request.SocialSignUpRequest;
import kr.modusplant.domains.account.social.usecase.response.LoginResult;
import kr.modusplant.domains.account.social.usecase.response.NeedLinkResult;
import kr.modusplant.domains.account.social.usecase.response.NeedSignupResult;
import kr.modusplant.domains.account.social.usecase.response.SocialLoginResponse;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.provider.JwtCookieProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN;
import static kr.modusplant.infrastructure.config.jackson.TestJacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class SocialIdentityRestControllerTest implements SocialAuthRequestTestUtils, SocialLoginResultTestUtils, TempTokenInfoTestUtils, SocialSignUpRequestTestUtils {
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final SocialIdentityController socialIdentityController = mock(SocialIdentityController.class);
    private final TokenService tokenService = mock(TokenService.class);
    private final JwtCookieProvider jwtCookieProvider = mock(JwtCookieProvider.class);
    private final SocialIdentityTokenHelper tempTokenHelper = mock(SocialIdentityTokenHelper.class);
    private final SocialIdentityRestController socialIdentityRestController = new SocialIdentityRestController(
            socialIdentityController, tokenService, jwtCookieProvider, tempTokenHelper
    );

    private static final String TEMP_TOKEN = "temp_token";
    private static final String REFRESH_COOKIE = "refreshToken=eyJ...";
    private static final String TEMP_COOKIE = "tempToken=eyJ...";
    private static final String EXPIRED_TEMP_COOKIE = "tempToken=; Max-Age=0";
    private static final String ACCESS_TOKEN = "access_token";
    private static final TokenPair TOKEN_PAIR = new TokenPair(ACCESS_TOKEN, "refresh_token");

    @Test
    @DisplayName("소셜 로그인 시 LOGIN 결과를 받으면 refreshToken 쿠키와 accessToken을 반환한다")
    void testSocialLogin_givenLoginResult_willReturnRefreshCookieAndAccessToken() {
        // given
        SocialAuthRequest request = new SocialAuthRequest("auth_code");
        LoginResult loginResult = createKakaoLoginResult();

        given(socialIdentityController.issueSocialAccessToken(SocialProvider.KAKAO, request.code())).willReturn(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        given(socialIdentityController.handleSocialLogin(SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN)).willReturn(loginResult);
        given(tokenService.issueToken(loginResult.uuid(), loginResult.nickname(), loginResult.email(), loginResult.role())).willReturn(TOKEN_PAIR);
        given(jwtCookieProvider.generateRefreshTokenCookieAsString(TOKEN_PAIR.refreshToken())).willReturn(REFRESH_COOKIE);

        // when
        ResponseEntity<DataResponse<SocialLoginResponse>> response = socialIdentityRestController.socialLogin(request, SocialProvider.KAKAO);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).contains(REFRESH_COOKIE);
        assertThat(response.getBody().getData().type()).isEqualTo(OAuthType.LOGIN);
        assertThat(response.getBody().getData().accessToken()).isEqualTo(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("소셜 로그인 시 NEED_SIGNUP 결과를 받으면 tempToken 쿠키와 email, nickname을 반환한다")
    void testSocialLogin_givenNeedSignupResult_willReturnTempCookieAndEmailAndNickname() {
        // given
        SocialAuthRequest request = new SocialAuthRequest("auth_code");
        NeedSignupResult needSignupResult = createKakaoNeedSignupResult();

        given(socialIdentityController.issueSocialAccessToken(SocialProvider.KAKAO, request.code())).willReturn(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        given(socialIdentityController.handleSocialLogin(SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN))
                .willReturn(needSignupResult);
        given(tempTokenHelper.generateTempToken(
                eq(needSignupResult.email()),
                eq(needSignupResult.providerId()),
                eq(needSignupResult.socialProvider()),
                eq(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN),
                anyLong()
        )).willReturn(TEMP_TOKEN);
        given(jwtCookieProvider.generateTempTokenCookieAsString(eq(TEMP_TOKEN), anyLong()))
                .willReturn(TEMP_COOKIE);

        // when
        ResponseEntity<DataResponse<SocialLoginResponse>> response =
                socialIdentityRestController.socialLogin(request, SocialProvider.KAKAO);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).contains(TEMP_COOKIE);
        assertThat(response.getBody().getData().type()).isEqualTo(OAuthType.NEED_SIGNUP);
        assertThat(response.getBody().getData().email()).isEqualTo(needSignupResult.email());
        assertThat(response.getBody().getData().nickname()).isEqualTo(needSignupResult.nickname());
    }

    @Test
    @DisplayName("소셜 로그인 시 NEED_LINK 결과를 받으면 tempToken 쿠키와 email, nickname을 반환한다")
    void testSocialLogin_givenNeedLinkResult_willReturnTempCookieAndEmailAndNickname() {
        // given
        SocialAuthRequest request = new SocialAuthRequest("auth_code");
        NeedLinkResult needLinkResult = createKakaoNeedLinkResult();

        given(socialIdentityController.issueSocialAccessToken(SocialProvider.KAKAO, request.code()))
                .willReturn(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        given(socialIdentityController.handleSocialLogin(SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN))
                .willReturn(needLinkResult);
        given(tempTokenHelper.generateTempToken(
                eq(needLinkResult.email()),
                eq(needLinkResult.providerId()),
                eq(needLinkResult.socialProvider()),
                eq(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN),
                anyLong()
        )).willReturn(TEMP_TOKEN);
        given(jwtCookieProvider.generateTempTokenCookieAsString(eq(TEMP_TOKEN), anyLong()))
                .willReturn(TEMP_COOKIE);

        // when
        ResponseEntity<DataResponse<SocialLoginResponse>> response =
                socialIdentityRestController.socialLogin(request, SocialProvider.KAKAO);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).contains(TEMP_COOKIE);
        assertThat(response.getBody().getData().type()).isEqualTo(OAuthType.NEED_LINK);
        assertThat(response.getBody().getData().email()).isEqualTo(needLinkResult.email());
        assertThat(response.getBody().getData().nickname()).isEqualTo(needLinkResult.nickname());
    }


    @Test
    @DisplayName("소셜 회원가입 완료 시 tempToken 쿠키를 삭제하고 refreshToken 쿠키와 accessToken을 반환한다")
    void testSocialSignUp_givenValidTempTokenAndRequest_willReturnRefreshCookieAndAccessToken() {
        // given
        SocialSignUpRequest signUpRequest = createTestSocialSignUpRequest();
        TempTokenInfo tempTokenInfo = createKakaoTempTokenInfoWithBasicEmail();
        LoginResult loginResult = createKakaoLoginResult();

        given(tempTokenHelper.getTempTokenInfoFromClaims(TEMP_TOKEN))
                .willReturn(tempTokenInfo);
        given(socialIdentityController.createNewMember(signUpRequest, tempTokenInfo))
                .willReturn(loginResult);
        given(tokenService.issueToken(loginResult.uuid(), loginResult.nickname(), loginResult.email(), loginResult.role()))
                .willReturn(TOKEN_PAIR);
        given(jwtCookieProvider.generateRefreshTokenCookieAsString(TOKEN_PAIR.refreshToken()))
                .willReturn(REFRESH_COOKIE);
        given(jwtCookieProvider.deleteTempTokenCookieAsString())
                .willReturn(EXPIRED_TEMP_COOKIE);

        // when
        ResponseEntity<DataResponse<SocialLoginResponse>> response =
                socialIdentityRestController.socialSignUp(TEMP_TOKEN, signUpRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).contains(REFRESH_COOKIE, EXPIRED_TEMP_COOKIE);
        assertThat(response.getBody().getData().type()).isEqualTo(OAuthType.LOGIN);
        assertThat(response.getBody().getData().accessToken()).isEqualTo(ACCESS_TOKEN);
    }


    @Test
    @DisplayName("소셜 계정 연동 완료 시 tempToken 쿠키를 삭제하고 refreshToken 쿠키와 accessToken을 반환한다")
    void testSocialLink_givenValidTempToken_willReturnRefreshCookieAndAccessToken() {
        // given
        TempTokenInfo tempTokenInfo = createKakaoTempTokenInfoWithBasicEmail();
        LoginResult loginResult = createKakaoLoginResult();

        given(tempTokenHelper.getTempTokenInfoFromClaims(TEMP_TOKEN))
                .willReturn(tempTokenInfo);
        given(socialIdentityController.linkBasicSocialMember(tempTokenInfo))
                .willReturn(loginResult);
        given(tokenService.issueToken(loginResult.uuid(), loginResult.nickname(), loginResult.email(), loginResult.role()))
                .willReturn(TOKEN_PAIR);
        given(jwtCookieProvider.generateRefreshTokenCookieAsString(TOKEN_PAIR.refreshToken()))
                .willReturn(REFRESH_COOKIE);
        given(jwtCookieProvider.deleteTempTokenCookieAsString())
                .willReturn(EXPIRED_TEMP_COOKIE);

        // when
        ResponseEntity<DataResponse<SocialLoginResponse>> response =
                socialIdentityRestController.socialLink(TEMP_TOKEN);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).contains(REFRESH_COOKIE, EXPIRED_TEMP_COOKIE);
        assertThat(response.getBody().getData().type()).isEqualTo(OAuthType.LOGIN);
        assertThat(response.getBody().getData().accessToken()).isEqualTo(ACCESS_TOKEN);
    }


    @Test
    @DisplayName("소셜 연결 해제 시 tempToken 쿠키를 삭제하고 응답을 반환한다")
    void testUnlinkSocialAccount_givenValidTempToken_willReturnExpiredTempCookie() {
        // given
        given(tempTokenHelper.getSocialProviderFromClaims(TEMP_TOKEN))
                .willReturn(SocialProvider.KAKAO);
        given(tempTokenHelper.getSocialAccessTokenFromClaims(TEMP_TOKEN))
                .willReturn(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        willDoNothing().given(socialIdentityController)
                .unlinkSocialAccount(SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        given(jwtCookieProvider.deleteTempTokenCookieAsString())
                .willReturn(EXPIRED_TEMP_COOKIE);

        // when
        ResponseEntity<DataResponse<Void>> response =
                socialIdentityRestController.unlinkSocialAccount(TEMP_TOKEN);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).contains(EXPIRED_TEMP_COOKIE);
        assertThat(response.getBody().toString()).isEqualTo(DataResponse.ok().toString());
        verify(socialIdentityController).unlinkSocialAccount(SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
    }
}