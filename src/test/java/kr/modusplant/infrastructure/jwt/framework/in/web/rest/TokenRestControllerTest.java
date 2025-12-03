package kr.modusplant.infrastructure.jwt.framework.in.web.rest;

import jakarta.servlet.http.Cookie;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TokenRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TokenService tokenService;

    @Spy
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private TokenRestController tokenRestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tokenRestController).build();
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshDuration", 604800000L);
    }

    @Test
    @DisplayName("Refresh Token을 갱신하기")
    void testRefreshToken_givenRefreshTokenAndAccessToken_willReturnResponseEntity() throws Exception{
        // given
        String accessToken = "access_token";
        String refreshToken = "refresh_token";
        String authorizationHeader = "Bearer " + accessToken;

        TokenPair newTokenPair = new TokenPair("new_access_token","new_refresh_token");

        given(tokenService.verifyAndReissueToken(accessToken, refreshToken)).willReturn(newTokenPair);

        // when & then
        mockMvc.perform(post("/api/auth/token/refresh")
                        .header("Authorization", authorizationHeader)
                        .cookie(new Cookie("refresh_token", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", containsString("refresh_token")))
                .andExpect(header().string("Set-Cookie", containsString("HttpOnly")))
                .andExpect(header().string("Set-Cookie", containsString("Secure")))
                .andExpect(header().string("Set-Cookie", containsString("SameSite=Lax")))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("new_access_token"));
    }

}