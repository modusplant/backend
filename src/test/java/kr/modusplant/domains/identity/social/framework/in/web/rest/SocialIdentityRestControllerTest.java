package kr.modusplant.domains.identity.social.framework.in.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.identity.social.adapter.controller.SocialIdentityController;
import kr.modusplant.domains.identity.social.common.util.domain.vo.UserPayloadTestUtils;
import kr.modusplant.domains.identity.social.common.util.usecase.request.SocialLoginRequestTestUtils;
import kr.modusplant.domains.identity.social.usecase.request.SocialLoginRequest;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SocialIdentityRestControllerTest implements SocialLoginRequestTestUtils, UserPayloadTestUtils {
    private MockMvc mockMvc;

    @Mock
    private SocialIdentityController socialIdentityController;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SocialIdentityRestController socialIdentityRestController;

    private final String TEST_ACCESS_TOKEN = "test-access-token";
    private final String TEST_REFRESH_TOKEN = "test-refresh-token";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(socialIdentityRestController).build();
    }

    @Test
    @DisplayName("카카오 로그인 요청")
    void testKakaoSocialLogin_givenCode_willReturnToken() throws Exception {
        // given
        SocialLoginRequest socialLoginRequest = createTestKakaoLoginRequest();
        TokenPair mockTokenPair = new TokenPair(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);

        given(socialIdentityController.handleSocialLogin(eq(AuthProvider.KAKAO), eq(socialLoginRequest.getCode()))).willReturn(testSocialKakaoUserPayload);
        given(tokenService.issueToken(
                eq(testSocialKakaoUserPayload.getMemberId().getValue()),
                eq(testSocialKakaoUserPayload.getNickname().getNickname()),
                eq(testSocialKakaoUserPayload.getRole())
        )).willReturn(mockTokenPair);

        // when & then
        mockMvc.perform(post("/api/auth/kakao/social-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socialLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.accessToken").value(TEST_ACCESS_TOKEN))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    @DisplayName("구글 로그인 요청")
    void testGoogleSocialLogin_givenCode_willReturnToken() throws Exception {
        // given
        SocialLoginRequest socialLoginRequest = createTestGoogleLoginRequest();
        TokenPair mockTokenPair = new TokenPair(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);

        given(socialIdentityController.handleSocialLogin(eq(AuthProvider.GOOGLE), eq(socialLoginRequest.getCode()))).willReturn(testSocialGoogleUserPayload);
        given(tokenService.issueToken(
                eq(testSocialGoogleUserPayload.getMemberId().getValue()),
                eq(testSocialGoogleUserPayload.getNickname().getNickname()),
                eq(testSocialGoogleUserPayload.getRole())
        )).willReturn(mockTokenPair);

        // when & then
        mockMvc.perform(post("/api/auth/google/social-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socialLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.accessToken").value(TEST_ACCESS_TOKEN))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(cookie().exists("refresh_token"));
    }


}