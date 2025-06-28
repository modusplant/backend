package kr.modusplant.global.middleware.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.middleware.security.DefaultAuthenticationEntryPoint;
import kr.modusplant.global.middleware.security.models.DefaultAuthToken;
import kr.modusplant.global.middleware.security.models.DefaultUserDetails;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final DefaultAuthenticationEntryPoint entryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws JwtException, ServletException, IOException {

        String rawAccessToken = request.getHeader("Authorization");

        if(rawAccessToken != null) {
            String accessToken = rawAccessToken.substring(7);

            if(tokenProvider.validateToken(accessToken)) {
                DefaultUserDetails defaultUserDetails = constructUserDetails(accessToken);
                DefaultAuthToken authenticatedToken =
                        new DefaultAuthToken(defaultUserDetails, defaultUserDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticatedToken);

            } else {
               SecurityContextHolder.clearContext();
               entryPoint.commence(request, response, new BadCredentialsException("접근 토큰이 만료되었습니다."));
            }
        }

        filterChain.doFilter(request, response);
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
