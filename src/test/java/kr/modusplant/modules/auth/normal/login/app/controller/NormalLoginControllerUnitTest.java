package kr.modusplant.modules.auth.normal.login.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NormalLoginController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class NormalLoginControllerUnitTest {

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    HttpServletRequest testRequest;

    @InjectMocks
    private NormalLoginController normalLoginController = new NormalLoginController();

    public static ResultMatcher matchCookie(String name, String value,
                                            String path, int maxAge,
                                            boolean secure, boolean httpOnly) {
        return result -> {
            cookie().exists(name).match(result);
            cookie().value(name, value).match(result);
            cookie().path(name, path).match(result);
            cookie().maxAge(name, maxAge).match(result);
            cookie().secure(name, secure).match(result);
            cookie().httpOnly(name, httpOnly).match(result);
        };
    }

    @Test
    public void sendLoginSuccessTest() throws Exception {
        // given
        String testAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwianRpIjoiYWJjMTIzeHl6NDU2IiwiZXhwIjoxNjM4NzY4MDIyLCJpYXQiOjE2MzYxNzYwMjJ9.7Qm6ZxQz3XW6J8KvY1lTn4RfG2HsPpLq1DwYb5Nv0eE";
        String testRefreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        long testAccessTokenExpirationTime = 1000L;
        long testRefreshTokenExpirationTime = 1300L;

        // when
        mockMvc.perform(post("/api/auth/login-success")
                        .requestAttr("accessToken", testAccessToken)
                        .requestAttr("refreshToken", testRefreshToken)
                        .requestAttr("accessTokenExpirationTime", testAccessTokenExpirationTime)
                        .requestAttr("refreshTokenExpirationTime", testRefreshTokenExpirationTime))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.accessToken").value(testAccessToken))
                .andExpect(matchCookie(
                        "refreshToken", testRefreshToken, "/",
                        (int) refreshDuration, true, true
                ))
                .andExpect(jsonPath("$.data.accessTokenExpirationTime").value(testAccessTokenExpirationTime))
                .andExpect(jsonPath("$.data.refreshTokenExpirationTime").value(testRefreshTokenExpirationTime));
    }
}
