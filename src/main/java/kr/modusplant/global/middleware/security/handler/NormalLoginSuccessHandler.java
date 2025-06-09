package kr.modusplant.global.middleware.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class NormalLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenApplicationService tokenApplicationService;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        SiteMemberUserDetails currentUser = (SiteMemberUserDetails) authentication.getPrincipal();
        UUID deviceIdToSend = UUID.randomUUID();

        TokenPair loginTokenPair = tokenApplicationService.issueToken(
                currentUser.getActiveUuid(), currentUser.getNickname(), getMemberRole(currentUser), deviceIdToSend
        );
        long epochSecondsOfAccessTokenExpirationTime =
                (tokenProvider.getExpirationFromToken(loginTokenPair.getAccessToken())).getTime() / 1000;
        long epochSecondsOfRefreshTokenExpirationTime =
                (tokenProvider.getExpirationFromToken(loginTokenPair.getRefreshToken())).getTime() / 1000;

        // TODO: authentication을 컨트롤러에서 사용하지 않는다면 null로 초기화할 것. 컨텍스트도 비우고.
        request.setAttribute("authentication", authentication);
        request.setAttribute("accessToken", loginTokenPair.getAccessToken());
        request.setAttribute("refreshToken", loginTokenPair.getRefreshToken());
        request.setAttribute("accessTokenExpirationTime", epochSecondsOfAccessTokenExpirationTime);
        request.setAttribute("refreshTokenExpirationTime", epochSecondsOfRefreshTokenExpirationTime);

        request.getRequestDispatcher("/api/auth/login-success").forward(request, response);
    }

    private Role getMemberRole(SiteMemberUserDetails memberUserDetails) {
        GrantedAuthority memberRole = memberUserDetails.getAuthorities().stream()
                .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("The authenticated user does not have role"));

        return Role.valueOf(memberRole.getAuthority());
    }
}
