package kr.modusplant.domains.account.social.framework.outbound.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.outbound.client.dto.IdTokenInfo;
import kr.modusplant.domains.account.social.framework.outbound.exception.OAuthRequestFailException;
import kr.modusplant.domains.account.social.usecase.record.SocialUserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(KakaoAuthClient.class)
class KakaoAuthClientTest {
    @Autowired
    private KakaoAuthClient kakaoAuthClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private SocialIdTokenParser idTokenParser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kakaoAuthClient, "KAKAO_API_KEY", "test-api-key");
        ReflectionTestUtils.setField(kakaoAuthClient, "KAKAO_REDIRECT_URI", "http://localhost:8080/callback");
        ReflectionTestUtils.setField(kakaoAuthClient, "KAKAO_LOCAL_REDIRECT_URI", "http://localhost:8080/callback/local");
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    @DisplayName("카카오 access token 발급 성공 테스트")
    void testGetToken_givenCode_willReturnToken() {
        // given
        String code = "test-auth-code";
        String expectedAccessToken = "test-access-token";
        String expectedIdToken = "test-id-token";
        String expectedId = "12345";
        String expectedEmail = "test@kakao.com";
        String expectedNickname = "testUser";
        String responseBody = "{\"access_token\":\"" + expectedAccessToken + "\", \"id_token\":\"" + expectedIdToken + "\"}";

        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        Map.of(
                "code",code,
                "client_id","test-api-key",
                "redirect_uri", "http://localhost:8080/callback",
                "grant_type", "authorization_code"
        ).forEach(formData::add);

        mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andExpect(content().formData(formData))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        IdTokenInfo mockIdTokenInfo = new IdTokenInfo(expectedId, expectedEmail, expectedNickname);
        given(idTokenParser.parse(expectedIdToken, SocialProvider.KAKAO)).willReturn(mockIdTokenInfo);

        // when
        SocialUserInfo userInfo = kakaoAuthClient.getToken(code, false);

        // then
        assertThat(userInfo.socialAccessToken()).isEqualTo(expectedAccessToken);
        assertThat(userInfo.id()).isEqualTo(expectedId);
        assertThat(userInfo.email()).isEqualTo(expectedEmail);
        assertThat(userInfo.nickname()).isEqualTo(expectedNickname);
    }

    @Test
    @DisplayName("카카오 access token 발급 실패 시 예외 발생 테스트")
    void testGetToken_givenInvalidCode_willThrowException() {
        // given
        String authCode = "fake-auth-code";
        String errorResponseBody = "{\"error\":\"invalid_grant\", \"error_description\":\"authorization code not found\"}";

        mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponseBody));

        // when & then
        assertThrows(OAuthRequestFailException.class, () -> kakaoAuthClient.getToken(authCode, false));
    }

    @Test
    @DisplayName("카카오 연결 해제 성공 테스트")
    void testRevokeAccess_givenAccessToken_willSuccess() {
        // given
        String accessToken = "test-access-token";
        mockServer.expect(requestTo("https://kapi.kakao.com/v1/user/unlink"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION,"Bearer " + accessToken))
                .andRespond(withSuccess());

        // when
        kakaoAuthClient.revokeAccess(accessToken);

        // then
        mockServer.verify();
    }

    @Test
    @DisplayName("카카오 연결 해제 실패 시 예외 발생 테스트")
    void testRevokeAccess_givenInvalidAccessToken_willThrowException() {
        // given
        String accessToken = "invalid-token";
        String errorResponseBody = "{\"error\":\"not_registered_user\", \"error_description\":\"user not found\"}";
        mockServer.expect(requestTo("https://kapi.kakao.com/v1/user/unlink"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponseBody));

        // when & then
        assertThrows(OAuthRequestFailException.class,() -> kakaoAuthClient.revokeAccess(accessToken));
    }
}