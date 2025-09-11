package kr.modusplant.legacy.modules.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.legacy.modules.jwt.app.dto.TokenPair;
import kr.modusplant.legacy.modules.jwt.app.service.TokenApplicationService;
import kr.modusplant.legacy.modules.security.enums.Role;
import kr.modusplant.legacy.modules.security.models.DefaultUserDetails;
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
    private final SiteMemberValidationService memberValidationService;
    private final TokenApplicationService tokenApplicationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultUserDetails currentMember = (DefaultUserDetails) authentication.getPrincipal();

        updateMemberLoggedInAt(currentMember.getActiveUuid());

        TokenPair loginTokenPair = tokenApplicationService.issueToken(
                currentMember.getActiveUuid(), currentMember.getNickname(), getMemberRole(currentMember));

        request.setAttribute("accessToken", loginTokenPair.accessToken());
        request.setAttribute("refreshToken", loginTokenPair.refreshToken());

        request.getRequestDispatcher("/api/auth/login-success").forward(request, response);
    }

    private Role getMemberRole(DefaultUserDetails currentUserDetails) {
        GrantedAuthority memberRole = currentUserDetails.getAuthorities().stream()
                .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("인증된 유저에게 할당된 역할이 없습니다."));

        String rawRole = memberRole.getAuthority();

        return Role.valueOf(rawRole
                .substring(rawRole.indexOf("_") + 1)
        );
    }

    private void updateMemberLoggedInAt(UUID currentMemberUuid) {
        memberValidationService.validateNotFoundUuid(currentMemberUuid);
        SiteMemberEntity memberEntity = memberRepository.findByUuid(currentMemberUuid).orElseThrow();
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberRepository.save(memberEntity);
    }
}
