package kr.modusplant.domains.identity.normal.framework.in.web.rest;

import kr.modusplant.domains.identity.normal.adapter.controller.NormalIdentityController;
import kr.modusplant.domains.identity.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityRestControllerUnitTest implements
        RefreshTokenEntityTestUtils, NormalSignUpRequestTestUtils {

    private final NormalIdentityController controller = Mockito.mock(NormalIdentityController.class);
    private final NormalIdentityRestController restController = new NormalIdentityRestController(controller);

    @Test
    @DisplayName("유효한 요청을 받으면 일반 회원가입 응답 반환")
    public void testRegisterNormalMember_givenValidRequest_willReturnSuccess() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = restController.registerNormalMember(testNormalSignUpRequest);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("유효한 토큰을 받았을 시 일반 로그인 응답 반환")
    public void testRespondToNormalLoginSuccess_givenValidToken_willReturnSuccess() {
        // given
        String testAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwianRpIjoiYWJjMTIzeHl6NDU2IiwiZXhwIjoxNjM4NzY4MDIyLCJpYXQiOjE2MzYxNzYwMjJ9.7Qm6ZxQz3XW6J8KvY1lTn4RfG2HsPpLq1DwYb5Nv0eE";
        String testRefreshToken = createRefreshTokenBasicEntityBuilder().build().getRefreshToken();

        // when
        ResponseEntity<DataResponse<Map<String, Object>>> response = restController.respondToNormalLoginSuccess(testAccessToken, testRefreshToken);

        String refreshTokenCookie = response.getHeaders().get("Set-Cookie").get(0);
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
        assertThat(cookieResult.get("refreshToken")).isEqualTo(testRefreshToken);
        assertThat(cookieResult.get("Path")).isEqualTo("/");
        assertThat(cookieResult.get("Secure")).isEqualTo("Secure");
        assertThat(cookieResult.get("HttpOnly")).isEqualTo("HttpOnly");
        assertThat(cookieResult.get("SameSite")).isEqualTo("Lax");

    }
}
