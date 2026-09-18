package kr.modusplant.infrastructure.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.jwt.framework.outbound.redis.AccessTokenRedisRepository;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BadCredentialException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class JwtAuthenticationFilterTest {

    private final JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
    private final AccessTokenRedisRepository tokenRedisRepository = Mockito.mock(AccessTokenRedisRepository.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider, tokenRedisRepository);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Authorization 헤더 없이 필터 체인 진행 활동 수행")
    void testDoFilterInternal_givenNoAuthorizationHeader_willProceedWithoutAuthentication() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        given(request.getHeader("Authorization")).willReturn(null);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        then(filterChain).should().doFilter(request, response);
    }

    @Test
    @DisplayName("유효한 Bearer 토큰으로 인증 정보 설정 활동 수행")
    void testDoFilterInternal_givenValidBearerToken_willSetAuthentication() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        UUID uuid = UUID.randomUUID();
        Claims claims = Mockito.mock(Claims.class);
        given(request.getHeader("Authorization")).willReturn("Bearer validToken");
        given(tokenProvider.validateToken("validToken")).willReturn(true);
        given(tokenRedisRepository.isBlacklisted("validToken")).willReturn(false);
        given(tokenProvider.getClaimsFromToken("validToken")).willReturn(claims);
        given(claims.getSubject()).willReturn(uuid.toString());
        given(claims.get("nickname")).willReturn("nickname");
        given(claims.get("roles")).willReturn("ROLE_USER");

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        then(filterChain).should().doFilter(request, response);
    }

    @Test
    @DisplayName("Bearer 접두사가 없는 토큰으로 예외 반환")
    void testDoFilterInternal_givenMalformedBearerPrefix_willThrowException() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        given(request.getHeader("Authorization")).willReturn("InvalidPrefix token");

        // when & then
        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(BadCredentialException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.INVALID_TOKEN_FORMAT);
    }

    @Test
    @DisplayName("검증에 실패한 토큰으로 예외 반환")
    void testDoFilterInternal_givenInvalidToken_willThrowException() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        given(request.getHeader("Authorization")).willReturn("Bearer invalidToken");
        given(tokenProvider.validateToken("invalidToken")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(BadCredentialException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.EXPIRED_TOKEN);
    }

    @Test
    @DisplayName("블랙리스트에 등록된 토큰으로 예외 반환")
    void testDoFilterInternal_givenBlacklistedToken_willThrowException() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        given(request.getHeader("Authorization")).willReturn("Bearer blacklistedToken");
        given(tokenProvider.validateToken("blacklistedToken")).willReturn(true);
        given(tokenRedisRepository.isBlacklisted("blacklistedToken")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(BadCredentialException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.BLACKLISTED_TOKEN);
    }
}
