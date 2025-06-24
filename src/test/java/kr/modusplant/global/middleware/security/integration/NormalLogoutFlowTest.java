package kr.modusplant.global.middleware.security.integration;

import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NormalLogoutFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenApplicationService tokenApplicationService;

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
