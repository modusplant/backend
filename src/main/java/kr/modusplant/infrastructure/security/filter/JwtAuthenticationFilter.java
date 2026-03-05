package kr.modusplant.infrastructure.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.jwt.framework.out.redis.AccessTokenRedisRepository;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BadCredentialException;
import kr.modusplant.infrastructure.security.models.DefaultAuthToken;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final AccessTokenRedisRepository tokenRedisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws JwtException, ServletException, IOException {

        String rawAccessToken = request.getHeader("Authorization");

        if (evaluateAccessToken(rawAccessToken)) {
            String accessToken = rawAccessToken.substring(7);

            DefaultUserDetails defaultUserDetails = constructUserDetails(accessToken);
            DefaultAuthToken authenticatedToken =
                    new DefaultAuthToken(defaultUserDetails, defaultUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticatedToken);

            filterChain.doFilter(request, response);
        } else {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean evaluateAccessToken(String rawAccessToken) {
        if (rawAccessToken == null) {
            throw new BadCredentialException(SecurityErrorCode.EMPTY_TOKEN);
        }
        if (!rawAccessToken.startsWith("Bearer ")) {
            throw new BadCredentialException(SecurityErrorCode.INVALID_TOKEN_FORMAT);
        }

        String accessToken = rawAccessToken.substring(7);
        if (!tokenProvider.validateToken(accessToken)) {
            throw new BadCredentialException(SecurityErrorCode.EXPIRED_TOKEN);
        }
        if (tokenRedisRepository.isBlacklisted(accessToken)) {
            throw new BadCredentialException(SecurityErrorCode.BLACKLISTED_TOKEN);
        }
        return true;
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
