package kr.modusplant.global.middleware.security.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.middleware.security.SiteMemberUserDetailsService;
import kr.modusplant.global.middleware.security.common.util.SiteMemberUserDetailsTestUtils;
import kr.modusplant.global.middleware.security.config.SecurityConfig;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import kr.modusplant.modules.auth.normal.login.common.util.app.http.request.NormalLoginRequestTestUtils;
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

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class NormalLoginAuthenticationFlowTest implements
        SiteMemberUserDetailsTestUtils, NormalLoginRequestTestUtils, SiteMemberEntityTestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy filterChainProxy;

    @MockitoBean
    private SiteMemberUserDetailsService memberUserDetailsService;

    @MockitoBean
    private SiteMemberValidationService memberValidationService;

    @MockitoBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockitoBean
    private TokenValidationService tokenValidationService;

    @MockitoBean
    private RefreshTokenApplicationService refreshTokenApplicationService;

    @MockitoBean
    private SiteMemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        when(bCryptPasswordEncoder.encode("userPw2!"))
                .thenReturn(testSiteMemberUserDetailsBuilder.build().getPassword());
        when(bCryptPasswordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);
    }

    @Test
    public void givenValidSiteMemberUserDetails_willCallSuccessHandler() throws Exception {
        // given
        SiteMemberUserDetails validSiteMemberUserDetails = testSiteMemberUserDetailsBuilder
                .isActive(true)
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .build();

        given(memberUserDetailsService.loadUserByUsername(testLoginRequest.email()))
                .willReturn(validSiteMemberUserDetails);
        doNothing().when(tokenValidationService).validateNotFoundMemberUuid(null);
        given(refreshTokenApplicationService.insert(any())).willReturn(null);
        doNothing().when(memberValidationService).validateNotFoundUuid(validSiteMemberUserDetails.getActiveUuid());
        given(memberRepository.findByUuid(validSiteMemberUserDetails.getActiveUuid()))
                .willReturn(Optional.ofNullable(createMemberBasicUserEntityWithUuid()));
        given(memberRepository.save(any())).willReturn(null);

        // when
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginRequest)).characterEncoding("UTF-8"))

                // then
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/api/auth/login-success"))
                .andExpect(request().attribute("accessToken", notNullValue()))
                .andExpect(request().attribute("refreshToken", notNullValue()));
    }
    
    @Test
    public void givenInvalidSiteMemberUserDetails_thenCallFailureHandler() throws Exception {
        // given
        SiteMemberUserDetails invalidSiteMemberUserDetails = testSiteMemberUserDetailsBuilder
                .isActive(false)
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .build();

        when(memberUserDetailsService.loadUserByUsername(testLoginRequest.email()))
                .thenReturn(invalidSiteMemberUserDetails);

        // when
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginRequest)).characterEncoding("UTF-8"))

                // then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    public void verifyFilterChain() {
        filterChainProxy.getFilterChains()
                .forEach(filter -> System.out.println("Filter being chained: " + filter));
    }
}
