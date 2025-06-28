package kr.modusplant.global.middleware.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.middleware.security.DefaultUserDetailsService;
import kr.modusplant.global.middleware.security.common.util.SiteMemberUserDetailsTestUtils;
import kr.modusplant.global.middleware.security.config.SecurityConfig;
import kr.modusplant.global.middleware.security.models.DefaultUserDetails;
import kr.modusplant.modules.auth.normal.login.common.util.app.http.request.NormalLoginRequestTestUtils;
import kr.modusplant.modules.jwt.app.service.RefreshTokenApplicationService;
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
    private DefaultUserDetailsService defaultUserDetailsService;

    @MockitoBean
    private SiteMemberValidationService memberValidationService;

    @MockitoBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockitoBean
    private RefreshTokenApplicationService refreshTokenApplicationService;

    @MockitoBean
    private SiteMemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        when(bCryptPasswordEncoder.encode("userPw2!"))
                .thenReturn(testDefaultMemberUserDetailsBuilder.build().getPassword());
        when(bCryptPasswordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);
    }

    @Test
    public void givenValidSiteMemberUserDetails_willCallSuccessHandler() throws Exception {
        // given
        DefaultUserDetails validDefaultUserDetails = testDefaultMemberUserDetailsBuilder
                .isActive(true)
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .build();

        given(defaultUserDetailsService.loadUserByUsername(testLoginRequest.email()))
                .willReturn(validDefaultUserDetails);
        doNothing().when(memberValidationService).validateNotFoundUuid(null);
        given(refreshTokenApplicationService.insert(any())).willReturn(null);
        doNothing().when(memberValidationService).validateNotFoundUuid(validDefaultUserDetails.getActiveUuid());
        given(memberRepository.findByUuid(validDefaultUserDetails.getActiveUuid()))
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
        DefaultUserDetails invalidDefaultUserDetails = testDefaultMemberUserDetailsBuilder
                .isActive(false)
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .build();

        when(defaultUserDetailsService.loadUserByUsername(testLoginRequest.email()))
                .thenReturn(invalidDefaultUserDetails);

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
