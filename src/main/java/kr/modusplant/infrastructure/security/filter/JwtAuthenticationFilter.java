package kr.modusplant.infrastructure.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.jwt.framework.out.redis.AccessTokenRedisRepository;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.security.DefaultAuthenticationEntryPoint;
import kr.modusplant.infrastructure.security.models.DefaultAuthToken;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final DefaultAuthenticationEntryPoint entryPoint;
    private final AccessTokenRedisRepository tokenRedisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws JwtException, ServletException, IOException {

        String rawAccessToken = request.getHeader("Authorization");

        if(rawAccessToken != null) {
            String accessToken = rawAccessToken.substring(7);
            evaluateAccessToken(request, response, accessToken);
        }

        filterChain.doFilter(request, response);
    }

    private void evaluateAccessToken(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String accessToken) throws IOException {
        if(tokenRedisRepository.isBlacklisted(accessToken)) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, new BadCredentialsException("블랙리스트에 있는 접근 토큰입니다."));

        } else if (!tokenProvider.validateToken(accessToken)) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, new CredentialsExpiredException("만료된 접근 토큰입니다."));

        } else {
            DefaultUserDetails defaultUserDetails = constructUserDetails(accessToken);
            DefaultAuthToken authenticatedToken =
                    new DefaultAuthToken(defaultUserDetails, defaultUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
        }
    }

    private DefaultUserDetails constructUserDetails(String accessToken) {
        Claims tokenClaims = tokenProvider.getClaimsFromToken(accessToken);

        return DefaultUserDetails.builder()
                .activeUuid(UUID.fromString(tokenClaims.getSubject()))
                .nickname(String.valueOf(tokenClaims.get("nickname")))
                .isActive(true)
                .authorities(List.of(new SimpleGrantedAuthority(
                        String.valueOf(tokenClaims.get("roles")))
                ))
                .build();
    }

}
