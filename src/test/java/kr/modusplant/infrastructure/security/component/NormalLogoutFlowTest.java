package kr.modusplant.infrastructure.security.component;

import jakarta.servlet.http.Cookie;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.context.SecurityOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecurityOnlyContext
public class NormalLogoutFlowTest {

    private final MockMvc mockMvc;
    private final TokenService tokenService;

    @Autowired
    public NormalLogoutFlowTest(MockMvc mockMvc, TokenService tokenService) {
        this.mockMvc = mockMvc;
        this.tokenService = tokenService;
    }

    @Test
    @DisplayName("refresh token으로 로그아웃 성공 핸들러 호출 활동 수행")
    public void testLogout_givenRefreshToken_willInvokeSuccessHandler() throws Exception {

        // given
        String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String accessToken = "test-access-token";
        willDoNothing().given(tokenService).removeToken(anyString());
        willDoNothing().given(tokenService).blacklistAccessToken(anyString());

        // when
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new Cookie("refreshToken", refreshToken))
                        .header("Authorization", "Bearer " + accessToken))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}
