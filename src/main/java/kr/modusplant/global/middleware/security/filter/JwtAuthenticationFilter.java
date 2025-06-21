package kr.modusplant.global.middleware.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.middleware.security.handler.JwtSecurityHandler;
import kr.modusplant.global.middleware.security.models.SiteMemberAuthToken;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final JwtSecurityHandler jwtSecurityHandler;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws JwtException, ServletException, IOException {

        String accessToken = request.getHeader("Authorization");

        if(accessToken != null) {
            if(tokenProvider.validateToken(accessToken.substring(7))) {
                SiteMemberUserDetails memberUserDetails = constructUserDetails(accessToken);
                saveSecurityContext(memberUserDetails);

            } else {
                String refreshToken = request.getHeader("Cookie");
                if(refreshTokenRepository.findByRefreshToken(refreshToken).isPresent()) {

                    Map<String, String> headersOfNewTokens = jwtSecurityHandler.provideHeaderTokenPair(request);
                    String newAccessToken = headersOfNewTokens.get("accessToken");
                    String newRefreshToken = headersOfNewTokens.get("refreshToken");

                    if(jwtSecurityHandler.insertRefreshToken(newRefreshToken) != null) {
                        response.setHeader("X-Access-Token", newAccessToken);
                        response.setHeader("Set-Cookie", newRefreshToken);

                        SiteMemberUserDetails memberUserDetails = constructUserDetails(newAccessToken);
                        saveSecurityContext(memberUserDetails);
                    }

                } else {
                    SecurityContextHolder.clearContext();
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private SiteMemberUserDetails constructUserDetails(String accessToken) {
        Claims tokenClaims = tokenProvider.getClaimsFromToken(accessToken);

        return SiteMemberUserDetails.builder()
                .activeUuid(UUID.fromString(tokenClaims.getSubject()))
                .nickname(String.valueOf(tokenClaims.get("nickname")))
                .isActive(true)
                .authorities(List.of(new SimpleGrantedAuthority(
                        String.valueOf(tokenClaims.get("roles")))
                ))
                .build();
    }

    private void saveSecurityContext(SiteMemberUserDetails memberUserDetails) {
        SiteMemberAuthToken authenticatedToken =
                new SiteMemberAuthToken(memberUserDetails, memberUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
    }
}
