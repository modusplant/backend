package kr.modusplant.modules.auth.normal.login.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.global.middleware.security.SiteMemberUserDetailsService;
import kr.modusplant.global.middleware.security.common.util.SiteMemberUserDetailsTestUtils;
import kr.modusplant.global.middleware.security.config.SecurityConfig;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import kr.modusplant.modules.auth.normal.login.app.http.NormalLoginRequest;
import kr.modusplant.modules.jwt.app.service.RefreshTokenApplicationService;
import kr.modusplant.modules.jwt.domain.service.TokenValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class NormalLoginControllerIntegrationTest implements SiteMemberUserDetailsTestUtils {

    private NormalLoginRequest testLoginRequest;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy filterChainProxy;

    @MockitoBean
    private SiteMemberUserDetailsService memberUserDetailsService;

    @MockitoBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockitoBean
    private TokenValidationService tokenValidationService;

    @MockitoBean
    private RefreshTokenApplicationService refreshTokenApplicationService;

    @BeforeEach
    void setUp() {
        testLoginRequest = new NormalLoginRequest("akdnjs0308@gmail.com", "userPw2!");

        when(bCryptPasswordEncoder.encode("userPw2!"))
                .thenReturn("$2a$10$N9qo8uLOickgx2ZMRZoMy.M3qaW7D3oEfgL7iPTGgR7P92FpZ9X1e");

//        when(bCryptPasswordEncoder.matches("userPw2!", "$2a$10$N9qo8uLOickgx2ZMRZoMy.M3qaW7D3oEfgL7iPTGgR7P92FpZ9X1e"))
//                .thenReturn(true);
        when(bCryptPasswordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        SiteMemberUserDetails testUserDetails = testSiteMemberUserDetails;

        when(memberUserDetailsService.loadUserByUsername(testLoginRequest.email()))
                .thenReturn(testUserDetails);
    }

    @Test
    public void processLogin_givenValidUser_thenReturnDataResponse() throws Exception {

        doNothing().when(tokenValidationService).validateNotFoundMemberUuid(any(), any());
        doNothing().when(tokenValidationService).validateExistedDeviceId(any());
        given(refreshTokenApplicationService.insert(any())).willReturn(any());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginRequest)).characterEncoding("UTF-8"))

                .andExpect(status().isOk());
    }

    @Test
    public void verifyFilterChain() {
        filterChainProxy.getFilterChains().forEach(filter -> {
            System.out.println("Filter being chained: " + filter);
        });
    }
}
