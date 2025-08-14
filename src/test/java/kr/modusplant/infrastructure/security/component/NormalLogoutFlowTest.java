package kr.modusplant.infrastructure.security.component;

import kr.modusplant.global.context.SecurityOnlyContext;
import kr.modusplant.legacy.modules.jwt.app.service.TokenApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecurityOnlyContext
public class NormalLogoutFlowTest {

    private final MockMvc mockMvc;
    private final TokenApplicationService tokenApplicationService;

    @Autowired
    public NormalLogoutFlowTest(MockMvc mockMvc, TokenApplicationService tokenApplicationService) {
        this.mockMvc = mockMvc;
        this.tokenApplicationService = tokenApplicationService;
    }

    @Test
    public void givenRefreshToken_willCallSuccessHandler() throws Exception {

        // given
        String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        doNothing().when(tokenApplicationService).removeToken(anyString());

        // when
        mockMvc.perform(post("/api/auth/logout")
                        .header("Cookie", refreshToken))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}
