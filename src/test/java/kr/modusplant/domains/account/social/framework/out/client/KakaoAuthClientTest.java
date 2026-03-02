package kr.modusplant.domains.account.social.framework.out.client;

import kr.modusplant.domains.account.social.framework.out.client.dto.KakaoUserInfo;
import kr.modusplant.domains.account.social.framework.out.exception.OAuthRequestFailException;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(KakaoAuthClient.class)
class KakaoAuthClientTest {
    @Autowired
    private KakaoAuthClient kakaoAuthClient;
    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kakaoAuthClient, "KAKAO_API_KEY", "test-api-key");
        ReflectionTestUtils.setField(kakaoAuthClient, "KAKAO_REDIRECT_URI", "http://localhost:8080/callback");
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    @DisplayName("카카오 access token 발급 성공 테스트")
    void testGetAccessToken_givenCode_willReturnAccessToken() {
        // given
        String code = "test-auth-code";
        String expectedToken = "test-access-token";
        String responseBody = "{\"access_token\":\"" + expectedToken + "\"}";

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

        // when
        String accessToken = kakaoAuthClient.getAccessToken(code);

        // then
        assertThat(accessToken).isEqualTo(expectedToken);
    }

    @Test
    @DisplayName("카카오 access token 발급 실패 시 예외 발생 테스트")
    void testGetAccessToken_givenInvalidCode_willThrowException() {
        // given
        String authCode = "fake-auth-code";

        mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // when & then
        assertThrows(OAuthRequestFailException.class, () -> kakaoAuthClient.getAccessToken(authCode));
    }

    @Test
    @DisplayName("카카오 사용자 정보 가져오기 성공 테스트")
    void testGetUserInfo_givenAccessToken_willReturnKakaoUserInfo() {
        // given
        String accessToken = "test-access-token";
        Long id = 1234567L;
        String email = "test@kakao.com";
        String nickname = "kakao-nickname";
        String responseJson = "{" +
                "\"id\": " + id + "," +
                "\"kakao_account\": {" +
                "  \"email\": \""+email+"\"," +
                "  \"isEmailVerified\": true," +
                "  \"profile\": {" +
                "    \"nickname\": \""+nickname+"\"" +
                "   }" +
                " }" +
                "}";

        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        // when
        KakaoUserInfo userInfo = kakaoAuthClient.getUserInfo(accessToken);

        // then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getId()).isEqualTo(String.valueOf(id));
        assertThat(userInfo.getEmail()).isEqualTo(email);
        assertThat(userInfo.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("카카오 사용자 정보 가져오기 실패 시 예외 발생 테스트")
    void testGetUserInfo_givenInvalidAccessToken_willThrowException() {
        // given
        String accessToken = "invalid-token";

        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // when & then
        assertThrows(OAuthRequestFailException.class, () -> kakaoAuthClient.getUserInfo(accessToken));
    }
}