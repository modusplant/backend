package kr.modusplant.modules.auth.social.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.modules.auth.social.app.dto.GoogleUserInfo;
import kr.modusplant.modules.auth.social.error.OAuthException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(GoogleAuthClient.class)
class GoogleAuthClientTest {
    @Autowired
    private GoogleAuthClient googleAuthClient;
    @Autowired
    private MockRestServiceServer mockServer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleAuthClient, "GOOGLE_API_KEY", "test-api-key");
        ReflectionTestUtils.setField(googleAuthClient, "GOOGLE_SECRET", "test-secret");
        ReflectionTestUtils.setField(googleAuthClient, "GOOGLE_REDIRECT_URI", "http://localhost:8080/callback");
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    @DisplayName("구글 access token 발급 성공 테스트")
    void getAccessTokenSuccessTest(){
        // Given
        String code = "test-auth-code";
        String expectedToken = "test-access-token";
        String responseBody = "{\"access_token\":\"" + expectedToken + "\"}";
        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        Map.of(
                "code", code,
                "client_id", "test-api-key",
                "client_secret", "test-secret",
                "redirect_uri", "http://localhost:8080/callback",
                "grant_type", "authorization_code"
        ).forEach(formData::add);

        mockServer.expect(requestTo("https://oauth2.googleapis.com/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andExpect(content().formData(formData))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // When
        String accessToken = googleAuthClient.getAccessToken(code);

        // Then
        assertThat(accessToken).isEqualTo(expectedToken);
    }


    @Test
    @DisplayName("구글 access token 발급 실패 시 예외 발생 테스트")
    void getAccessTokenWhenErrorResponseThrowsOAuthExceptionTest() {
        // Given
        String authCode = "fake-auth-code";
        
        mockServer.expect(requestTo("https://oauth2.googleapis.com/token"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // When & Then
        assertThrows(OAuthException.class, () -> googleAuthClient.getAccessToken(authCode));
    }

    @Test
    @DisplayName("구글 사용자 정보 가져오기 성공 테스트")
    void getUserInfoSuccessTest() throws Exception {
        // Given
        String accessToken = "test-access-token";
        GoogleUserInfo expectedUserInfo = mock(GoogleUserInfo.class);
        given(expectedUserInfo.getId()).willReturn("1234567890");
        given(expectedUserInfo.getEmail()).willReturn("test@gmail.com");
        given(expectedUserInfo.getNickname()).willReturn("google-nickname");

        mockServer.expect(requestTo("https://www.googleapis.com/userinfo/v2/me"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andRespond(withSuccess(objectMapper.writeValueAsString(expectedUserInfo), MediaType.APPLICATION_JSON));

        // When
        GoogleUserInfo userInfo = googleAuthClient.getUserInfo(accessToken);

        // Then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getId()).isEqualTo(expectedUserInfo.getId());
        assertThat(userInfo.getEmail()).isEqualTo(expectedUserInfo.getEmail());
        assertThat(userInfo.getNickname()).isEqualTo(expectedUserInfo.getNickname());
    }

    @Test
    @DisplayName("구글 사용자 정보 가져오기 실패 시 예외 발생 테스트")
    void getUserInfoWhenErrorResponseThrowsOAuthExceptionTest() {
        // Given
        String accessToken = "invalid-token";
        
        mockServer.expect(requestTo("https://www.googleapis.com/userinfo/v2/me"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // When & Then
        assertThrows(OAuthException.class, () -> googleAuthClient.getUserInfo(accessToken));
    }
}