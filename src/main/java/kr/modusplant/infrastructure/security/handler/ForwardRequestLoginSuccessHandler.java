package kr.modusplant.infrastructure.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.exception.enums.ErrorCode.MEMBER_NOT_FOUND;

@RequiredArgsConstructor
public class ForwardRequestLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SiteMemberJpaRepository memberRepository;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultUserDetails currentMember = (DefaultUserDetails) authentication.getPrincipal();

        updateMemberLoggedInAt(currentMember.getActiveUuid());

        TokenPair loginTokenPair = tokenService.issueToken(
                currentMember.getActiveUuid(), currentMember.getNickname(), currentMember.getEmail(), getMemberRole(currentMember));

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
        if (currentMemberUuid == null) {
            return;
        }
        if (!memberRepository.existsByUuid(currentMemberUuid)) {
            throw new EntityNotFoundException(MEMBER_NOT_FOUND, TableName.SITE_MEMBER);
        }
        SiteMemberEntity memberEntity = memberRepository.findByUuid(currentMemberUuid).orElseThrow();
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberRepository.save(memberEntity);
    }
}
