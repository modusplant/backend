package kr.modusplant.infrastructure.security.component;

import jakarta.servlet.http.Cookie;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NormalLogoutFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private final TokenService tokenService = Mockito.mock(TokenService.class);

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
