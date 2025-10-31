package kr.modusplant.legacy.modules.auth.social.app.controller;

import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.legacy.modules.auth.social.app.dto.JwtUserPayload;
import kr.modusplant.legacy.modules.auth.social.app.service.SocialAuthApplicationService;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SocialAuthControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private SocialAuthApplicationService socialAuthApplicationService;
    
    @Mock
    private TokenService tokenService;
    
    @InjectMocks
    private SocialAuthController socialAuthController;
    
    private final String TEST_AUTH_CODE = "test-auth-code";
    private final String TEST_ACCESS_TOKEN = "test-access-token";
    private final String TEST_REFRESH_TOKEN = "test-refresh-token";
    private final UUID TEST_MEMBER_UUID = UUID.randomUUID();
    private final String TEST_NICKNAME = "test-user";
    private final Role TEST_ROLE = Role.USER;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(socialAuthController).build();
    }

    @Test
    void kakaoSocialLoginWithValidCodeShouldReturn200Test() throws Exception {
        // given
        String requestBody = String.format("{\"code\": \"%s\"}", TEST_AUTH_CODE);
        JwtUserPayload mockUser = new JwtUserPayload(TEST_MEMBER_UUID, TEST_NICKNAME, TEST_ROLE);
        TokenPair mockTokenPair = new TokenPair(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);

        given(socialAuthApplicationService.handleSocialLogin(eq(AuthProvider.KAKAO), eq(TEST_AUTH_CODE)))
                .willReturn(mockUser);
        given(tokenService.issueToken(eq(TEST_MEMBER_UUID), eq(TEST_NICKNAME), eq(TEST_ROLE)))
                .willReturn(mockTokenPair);

        // when & then
        mockMvc.perform(post("/api/auth/kakao/social-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.accessToken").value(TEST_ACCESS_TOKEN))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    void googleSocialLoginWithValidCodeShouldReturn200Test() throws Exception {
        // given
        String requestBody = String.format("{\"code\": \"%s\"}", TEST_AUTH_CODE);
        JwtUserPayload mockUser = new JwtUserPayload(TEST_MEMBER_UUID, TEST_NICKNAME, TEST_ROLE);
        TokenPair mockTokenPair = new TokenPair(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);

        given(socialAuthApplicationService.handleSocialLogin(eq(AuthProvider.GOOGLE), eq(TEST_AUTH_CODE)))
                .willReturn(mockUser);
        given(tokenService.issueToken(eq(TEST_MEMBER_UUID), eq(TEST_NICKNAME), eq(TEST_ROLE)))
                .willReturn(mockTokenPair);

        // when & then
        mockMvc.perform(post("/api/auth/google/social-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.accessToken").value(TEST_ACCESS_TOKEN))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(cookie().exists("refresh_token"));
    }
}