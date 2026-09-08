package kr.modusplant.infrastructure.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.DefaultUserDetailsService;
import kr.modusplant.infrastructure.security.common.util.NormalLoginRequestTestUtils;
import kr.modusplant.infrastructure.security.common.util.SiteMemberUserDetailsTestUtils;
import kr.modusplant.infrastructure.security.context.SecurityOnlyContext;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecurityOnlyContext
@Slf4j
public class NormalLoginAuthenticationFlowTest implements
        SiteMemberUserDetailsTestUtils, NormalLoginRequestTestUtils, MemberEntityTestUtils {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final FilterChainProxy filterChainProxy;
    private final DefaultUserDetailsService defaultUserDetailsService;
    private final TokenService tokenService;
    private final MemberJpaRepository memberRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public NormalLoginAuthenticationFlowTest(MockMvc mockMvc, ObjectMapper objectMapper, FilterChainProxy filterChainProxy, DefaultUserDetailsService defaultUserDetailsService, TokenService tokenService, MemberJpaRepository memberRepository, @Qualifier("bcryptPasswordEncoder") PasswordEncoder bCryptPasswordEncoder) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.filterChainProxy = filterChainProxy;
        this.defaultUserDetailsService = defaultUserDetailsService;
        this.tokenService = tokenService;
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Test
    @DisplayName("보안 필터 체인 등록 활동 수행")
    public void testSecurityFilterChain_givenConfiguredContext_willRegisterFilters() {
        // given & when
        var filterChains = filterChainProxy.getFilterChains();
        filterChains.forEach(filter -> log.info("Filter being chained: {}", filter));

        // then
        assertThat(filterChains).isNotEmpty();
    }

    @Test
    @DisplayName("유효한 사용자 정보로 성공 핸들러 호출 활동 수행")
    public void testLogin_givenValidUserDetails_willInvokeSuccessHandler() throws Exception {
        // given
        DefaultUserDetails validDefaultUserDetails = testDefaultMemberUserDetailsBuilder
                .password(bCryptPasswordEncoder.encode(testLoginRequest.password()))
                .isActive(true)
                .isBanned(false)
                .build();

        given(defaultUserDetailsService.loadUserByUsername(testLoginRequest.email()))
                .willReturn(validDefaultUserDetails);
        given(memberRepository.existsByUuid(validDefaultUserDetails.getUuid())).willReturn(true);
        given(memberRepository.findByUuid(validDefaultUserDetails.getUuid()))
                .willReturn(Optional.ofNullable(createMemberBasicUserEntityWithUuid()));
        given(memberRepository.save(any())).willReturn(null);
        given(tokenService.issueToken(any(), any(), any(), any()))
                .willReturn(new TokenPair("TEST_ACCESS_TOKEN", "TEST_REFRESH_TOKEN"));

        // when
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginRequest)).characterEncoding("UTF-8"))

                // then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유효하지 않은 사용자 정보로 실패 핸들러 호출 활동 수행")
    public void testLogin_givenInvalidUserDetails_willInvokeFailureHandler() throws Exception {
        // given
        DefaultUserDetails invalidDefaultUserDetails = testDefaultMemberUserDetailsBuilder
                .password(bCryptPasswordEncoder.encode(testLoginRequest.password()))
                .isActive(false)
                .isBanned(false)
                .build();

        given(defaultUserDetailsService.loadUserByUsername(testLoginRequest.email()))
                .willReturn(invalidDefaultUserDetails);

        // when
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginRequest)).characterEncoding("UTF-8"))

                // then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
