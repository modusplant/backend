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

@RestClientTest(GoogleAuthClient.class)
class GoogleAuthClientTest {
    @Autowired
    private GoogleAuthClient googleAuthClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private SocialIdTokenParser idTokenParser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleAuthClient, "GOOGLE_API_KEY", "test-api-key");
        ReflectionTestUtils.setField(googleAuthClient, "GOOGLE_SECRET", "test-secret");
        ReflectionTestUtils.setField(googleAuthClient, "GOOGLE_REDIRECT_URI", "http://localhost:8080/callback");
        ReflectionTestUtils.setField(googleAuthClient, "GOOGLE_LOCAL_REDIRECT_URI", "http://localhost:8080/callback/local");

    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    @DisplayName("구글 access token 발급 성공 테스트")
    void testGetAccessToken_givenCode_willReturnToken(){
        // Given
        String code = "test-auth-code";
        String expectedAccessToken = "test-access-token";
        String expectedIdToken = "test-id-token";
        String expectedId = "12345";
        String expectedEmail = "test@google.com";
        String expectedNickname = "testUser";
        String responseBody = "{\"access_token\":\"" + expectedAccessToken + "\", \"id_token\":\"" + expectedIdToken + "\"}";

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

        IdTokenInfo mockIdTokenInfo = new IdTokenInfo(expectedId, expectedEmail, expectedNickname);
        given(idTokenParser.parse(expectedIdToken, SocialProvider.GOOGLE)).willReturn(mockIdTokenInfo);

        // When
        SocialUserInfo userInfo = googleAuthClient.getTokenInfo(code, false);

        // Then
        assertThat(userInfo.socialAccessToken()).isEqualTo(expectedAccessToken);
        assertThat(userInfo.id()).isEqualTo(expectedId);
        assertThat(userInfo.email()).isEqualTo(expectedEmail);
        assertThat(userInfo.nickname()).isEqualTo(expectedNickname);
    }


    @Test
    @DisplayName("구글 access token 발급 실패 시 예외 발생 테스트")
    void testGetTokenInfo_givenInvalidCode_willThrowException() {
        // Given
        String authCode = "fake-auth-code";
        String errorBody = "{\"error\":\"invalid_grant\", \"error_description\":\"Bad Request\"}";

        mockServer.expect(requestTo("https://oauth2.googleapis.com/token"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorBody));

        // When & Then
        assertThrows(OAuthRequestFailException.class, () -> googleAuthClient.getTokenInfo(authCode, false));
    }

    @Test
    @DisplayName("구글 연결 해제 성공 테스트")
    void testRevokeAccess_givenAccessToken_willSuccess() {
        // given
        String accessToken = "test-access-token";
        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        formData.add("token", accessToken);
        mockServer.expect(requestTo("https://oauth2.googleapis.com/revoke"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andExpect(content().formData(formData))
                .andRespond(withSuccess());

        // when
        googleAuthClient.revokeAccess(accessToken);

        // then
        mockServer.verify();
    }

    @Test
    @DisplayName("구글 연결 해제 실패 시 예외 발생 테스트")
    void testRevokeAccess_givenInvalidAccessToken_willThrowException() {
        // given
        String accessToken = "invalid-token";
        String errorBody = "{\"error\":\"invalid_token\", \"error_description\":\"Revoke failed\"}";
        mockServer.expect(requestTo("https://oauth2.googleapis.com/revoke"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorBody));

        // when & then
        assertThrows(OAuthRequestFailException.class, () -> googleAuthClient.revokeAccess(accessToken));

    }

}