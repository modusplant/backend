package kr.modusplant.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.provider.JwtCookieProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.exception.AccountStateException;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class WriteResponseLoginSuccessHandlerTest {

    private final MemberJpaRepository memberRepository = Mockito.mock(MemberJpaRepository.class);
    private final TokenService tokenService = Mockito.mock(TokenService.class);
    private final JwtCookieProvider cookieProvider = Mockito.mock(JwtCookieProvider.class);
    private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
    private final WriteResponseLoginSuccessHandler handler =
            new WriteResponseLoginSuccessHandler(memberRepository, tokenService, cookieProvider, objectMapper);

    @Test
    @DisplayName("기존 회원으로 토큰 발급 및 응답 작성 활동 수행")
    void testOnAuthenticationSuccess_givenExistingMember_willIssueTokenAndWriteResponse() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(stringWriter));
        UUID memberUuid = UUID.randomUUID();
        DefaultUserDetails userDetails = DefaultUserDetails.builder()
                .email("test123@example.com")
                .uuid(memberUuid)
                .nickname("nickname")
                .provider(AuthProvider.BASIC)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        Authentication authentication = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(memberRepository.existsByUuid(memberUuid)).willReturn(true);
        MemberEntity member = Mockito.mock(MemberEntity.class);
        given(memberRepository.findByUuid(memberUuid)).willReturn(Optional.of(member));
        given(tokenService.issueToken(any(), any(), any(), any()))
                .willReturn(new TokenPair("access-token", "refresh-token"));
        given(cookieProvider.generateRefreshTokenCookieAsString("refresh-token")).willReturn("refreshToken=refresh-token");
        given(objectMapper.writeValueAsString(any())).willReturn("{\"data\":{\"accessToken\":\"access-token\"}}");

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        Mockito.verify(member).updateLoggedInAt(any());
        Mockito.verify(memberRepository).save(member);
        Mockito.verify(response).setHeader(HttpHeaders.SET_COOKIE, "refreshToken=refresh-token");
    }

    @Test
    @DisplayName("ROLE 권한이 없어 예외 반환")
    void testOnAuthenticationSuccess_givenNoRoleAuthority_willThrowException() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        UUID memberUuid = UUID.randomUUID();
        DefaultUserDetails userDetails = DefaultUserDetails.builder()
                .email("test123@example.com")
                .uuid(memberUuid)
                .nickname("nickname")
                .provider(AuthProvider.BASIC)
                .authorities(List.of())
                .build();
        Authentication authentication = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(memberRepository.existsByUuid(memberUuid)).willReturn(true);
        MemberEntity member = Mockito.mock(MemberEntity.class);
        given(memberRepository.findByUuid(memberUuid)).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> handler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(AccountStateException.class);
    }

    @Test
    @DisplayName("회원 정보를 찾을 수 없어 예외 반환")
    void testOnAuthenticationSuccess_givenMemberNotFound_willThrowException() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        UUID memberUuid = UUID.randomUUID();
        DefaultUserDetails userDetails = DefaultUserDetails.builder()
                .uuid(memberUuid)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        Authentication authentication = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(memberRepository.existsByUuid(memberUuid)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> handler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(NotFoundEntityException.class);
    }
}
