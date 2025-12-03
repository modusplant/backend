package kr.modusplant.infrastructure.security.component;

import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.context.SecurityOnlyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@SecurityOnlyContext
public class NormalLogoutFlowTest {

    private final MockMvc mockMvc;
    private final TokenService tokenService;

    @Autowired
    public NormalLogoutFlowTest(MockMvc mockMvc, TokenService tokenService) {
        this.mockMvc = mockMvc;
        this.tokenService = tokenService;
    }

//    @Test
//    public void givenRefreshToken_willCallSuccessHandler() throws Exception {
//
//        // given
//        String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
//        doNothing().when(tokenService).removeToken(anyString());
//
//        // when
//        mockMvc.perform(post("/api/auth/logout")
//                        .header("Cookie", refreshToken))
//
//                // then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(200));
//    }
}
