package kr.modusplant.infrastructure.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.DefaultUserDetailsService;
import kr.modusplant.infrastructure.security.common.util.SiteMemberUserDetailsTestUtils;
import kr.modusplant.infrastructure.security.context.SecurityOnlyContext;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.legacy.modules.auth.normal.login.common.util.app.http.request.NormalLoginRequestTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
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

@SecurityOnlyContext
public class NormalLoginAuthenticationFlowTest implements
        SiteMemberUserDetailsTestUtils, NormalLoginRequestTestUtils, SiteMemberEntityTestUtils {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final FilterChainProxy filterChainProxy;
    private final DefaultUserDetailsService defaultUserDetailsService;
    private final TokenService tokenService;
    private final SiteMemberJpaRepository memberRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public NormalLoginAuthenticationFlowTest(MockMvc mockMvc, ObjectMapper objectMapper, FilterChainProxy filterChainProxy, DefaultUserDetailsService defaultUserDetailsService, TokenService tokenService, SiteMemberJpaRepository memberRepository, PasswordEncoder bCryptPasswordEncoder) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.filterChainProxy = filterChainProxy;
        this.defaultUserDetailsService = defaultUserDetailsService;
        this.tokenService = tokenService;
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Test
    public void givenValidSiteMemberUserDetails_willCallSuccessHandler() throws Exception {
        // given
        when(bCryptPasswordEncoder.encode("userPw2!"))
                .thenReturn(testDefaultMemberUserDetailsBuilder.build().getPassword());
        when(bCryptPasswordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        DefaultUserDetails validDefaultUserDetails = testDefaultMemberUserDetailsBuilder
                .isActive(true)
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .build();

        given(defaultUserDetailsService.loadUserByUsername(testLoginRequest.email()))
                .willReturn(validDefaultUserDetails);
        given(memberRepository.existsByUuid(validDefaultUserDetails.getActiveUuid())).willReturn(false);
        given(memberRepository.findByUuid(validDefaultUserDetails.getActiveUuid()))
                .willReturn(Optional.ofNullable(createMemberBasicUserEntityWithUuid()));
        given(memberRepository.save(any())).willReturn(null);
        given(tokenService.issueToken(any(), any(), any()))
                .willReturn(new TokenPair("TEST_ACCESS_TOKEN", "TEST_REFRESH_TOKEN"));

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
        when(bCryptPasswordEncoder.encode("userPw2!"))
                .thenReturn(testDefaultMemberUserDetailsBuilder.build().getPassword());
        when(bCryptPasswordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

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
