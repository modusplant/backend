package kr.modusplant.global.middleware.security.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ForwardRequestLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SiteMemberRepository memberRepository;
    private final TokenApplicationService tokenApplicationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        SiteMemberUserDetails currentMember = (SiteMemberUserDetails) authentication.getPrincipal();

        updateMemberLoggedInAt(currentMember.getActiveUuid());

        TokenPair loginTokenPair = tokenApplicationService.issueToken(
                currentMember.getActiveUuid(), currentMember.getNickname(), getMemberRole(currentMember));

        request.setAttribute("accessToken", loginTokenPair.accessToken());
        request.setAttribute("refreshToken", loginTokenPair.refreshToken());

        request.getRequestDispatcher("/api/auth/login-success").forward(request, response);
    }

    private Role getMemberRole(SiteMemberUserDetails memberUserDetails) {
        GrantedAuthority memberRole = memberUserDetails.getAuthorities().stream()
                .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("The authenticated user does not have role"));

        String rawRole = memberRole.getAuthority();

        return Role.valueOf(rawRole
                .substring(rawRole.indexOf("_") + 1)
        );
    }

    private void updateMemberLoggedInAt(UUID currentMemberUuid) {
        SiteMemberEntity memberEntity = memberRepository.findByUuid(currentMemberUuid)
                .orElseThrow(() -> new EntityNotFoundException("cannot find the member of the uuid"));
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberRepository.save(memberEntity);
    }
}
