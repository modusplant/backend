package kr.modusplant.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.exception.enums.GeneralSuccessCode;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ForwardRequestLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SiteMemberJpaRepository memberRepository;
    private final TokenService tokenService;
    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultUserDetails currentMember = (DefaultUserDetails) authentication.getPrincipal();

        updateMemberLoggedInAt(currentMember.getActiveUuid());

        TokenPair loginTokenPair = tokenService.issueToken(
                currentMember.getActiveUuid(), currentMember.getNickname(), currentMember.getEmail(), getMemberRole(currentMember));

        String refreshTokenCookie = tokenProvider.generateRefreshTokenCookieAsString(loginTokenPair.refreshToken());
        Map<String, Object> accessTokenData = Map.of("accessToken", loginTokenPair.accessToken());

        response.setStatus(GeneralSuccessCode.GENERIC_SUCCESS.getHttpStatus());
        response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(DataResponse.ok(accessTokenData)));

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
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER);
        }
        SiteMemberEntity memberEntity = memberRepository.findByUuid(currentMemberUuid).orElseThrow();
        memberEntity.updateLoggedInAt(LocalDateTime.now());
        memberRepository.save(memberEntity);
    }
}
