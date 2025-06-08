package kr.modusplant.global.middleware.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        SiteMemberUserDetails currentUser =
                (SiteMemberUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID deviceIdToSend = UUID.randomUUID();

        TokenPair loginTokenPair = tokenApplicationService.issueToken(
                currentUser.getActiveUuid(), currentUser.getNickname(), getMemberRole(currentUser), deviceIdToSend
        );

        request.setAttribute("authentication", authentication);
        request.setAttribute("accessToken", loginTokenPair.getAccessToken());
        request.setAttribute("refreshToken", loginTokenPair.getRefreshToken());

        request.getRequestDispatcher("/api/auth/login-success").forward(request, response);
    }

    private Role getMemberRole(SiteMemberUserDetails memberUserDetails) {
        GrantedAuthority memberRole = memberUserDetails.getAuthorities().stream()
                .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("The authenticated user does not have role"));

        return Role.valueOf(memberRole.getAuthority());
    }
}
