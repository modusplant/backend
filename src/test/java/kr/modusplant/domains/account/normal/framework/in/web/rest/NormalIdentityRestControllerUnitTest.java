package kr.modusplant.domains.account.normal.framework.in.web.rest;

import kr.modusplant.domains.account.normal.adapter.controller.NormalIdentityController;
import kr.modusplant.domains.account.normal.common.util.usecase.request.EmailModificationRequestTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.PasswordModificationRequestTestUtils;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static kr.modusplant.infrastructure.jwt.constant.CookieName.REFRESH_TOKEN_COOKIE_NAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityRestControllerUnitTest implements
        RefreshTokenEntityTestUtils, NormalSignUpRequestTestUtils,
        EmailModificationRequestTestUtils, PasswordModificationRequestTestUtils {

    private final NormalIdentityController controller = Mockito.mock(NormalIdentityController.class);
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    private final NormalIdentityRestController restController = new NormalIdentityRestController(controller, jwtTokenProvider);

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshDuration", 604800000L);
    }

    @Test
    @DisplayName("유효한 요청을 받으면 일반 회원가입 후 성공 응답 반환")
    public void testRegisterNormalMember_givenValidRequest_willReturnSuccess() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = restController.registerNormalMember(testNormalSignUpRequest);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("유효한 요청을 받으면 사용자의 이메일 갱신 후 성공 응답 반환")
    public void testModifyEmail_givenValidRequest_willReturnSuccess() {
        // given & when
        ResponseEntity<DataResponse<Void>> response =
                restController.modifyEmail(MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID, testEmailModificationRequest);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("유효한 요청을 받으면 사용자의 비밀번호 갱신 후 성공 응답 반환")
    public void testModifyPassword_givenValidRequest_willReturnSuccess() {
        // given & when
        ResponseEntity<DataResponse<Void>> response =
                restController.modifyPassword(MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID, testPasswordModificationRequest);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("유효한 토큰을 받으면 일반 로그인 응답 반환")
    public void testModifyPassword_givenValidToken_willReturnSuccess() {
        // given
        String testAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwianRpIjoiYWJjMTIzeHl6NDU2IiwiZXhwIjoxNjM4NzY4MDIyLCJpYXQiOjE2MzYxNzYwMjJ9.7Qm6ZxQz3XW6J8KvY1lTn4RfG2HsPpLq1DwYb5Nv0eE";
        String testRefreshToken = createRefreshTokenBasicEntityBuilder().build().getRefreshToken();

        // when
        ResponseEntity<DataResponse<Map<String, Object>>> response = restController.respondToNormalLoginSuccess(testAccessToken, testRefreshToken);

        String refreshTokenCookie = response.getHeaders().get(HttpHeaders.SET_COOKIE).getFirst();
        Map<String, String> cookieResult = new HashMap<>();

        for(String part: refreshTokenCookie.split(";")) {
            if(part.contains("=")) {
                String[] parts = part.trim().split("=");
                cookieResult.put(parts[0], parts[1]);
            } else{
                cookieResult.put(part.trim(), part.trim());
            }
        }

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).getData().get("accessToken"))
                .isEqualTo(testAccessToken);
        assertThat(refreshTokenCookie).isNotNull();
        assertThat(cookieResult.get(REFRESH_TOKEN_COOKIE_NAME)).isEqualTo(testRefreshToken);
        assertThat(cookieResult.get("Path")).isEqualTo("/");
        assertThat(cookieResult.get("Secure")).isEqualTo("Secure");
        assertThat(cookieResult.get("HttpOnly")).isEqualTo("HttpOnly");
        assertThat(cookieResult.get("SameSite")).isEqualTo("Lax");

    }
}
