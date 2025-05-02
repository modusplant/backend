package kr.modusplant.modules.jwt.app.service;

import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberRoleApplicationService;
import kr.modusplant.global.enums.Role;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.domain.service.TokenValidationService;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.error.InvalidTokenException;
import kr.modusplant.modules.jwt.app.error.TokenDataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER_UUID;

@Service
@RequiredArgsConstructor
public class TokenApplicationService {
    private final TokenProvider tokenProvider;
    private final SiteMemberApplicationService memberApplicationService;
    private final SiteMemberRoleApplicationService memberRoleApplicationService;
    private final RefreshTokenApplicationService refreshTokenApplicationService;
    private final TokenValidationService tokenValidationService;

    // 토큰 생성
    public TokenPair issueToken(UUID memberUuid, String nickname, Role role, UUID deviceId) {
        // memberUuid, deviceId 검증
        tokenValidationService.validateNotFoundMemberUuid(MEMBER_UUID, memberUuid);
        tokenValidationService.validateExistedDeviceId(deviceId);

        // accessToken , refresh token 생성
        Map<String,String> claims = new HashMap<>();
        claims.put("nickname",nickname);
        claims.put("role",role.getValue());

        String accessToken = tokenProvider.generateAccessToken(memberUuid, claims);
        String refreshToken = tokenProvider.generateRefreshToken(memberUuid);

        // refresh token DB에 저장
        RefreshToken token = RefreshToken.builder()
                .memberUuid(memberUuid)
                .deviceId(deviceId)
                .refreshToken(refreshToken)
                .issuedAt(tokenProvider.getIssuedAtFromToken(refreshToken))
                .expiredAt(tokenProvider.getExpirationFromToken(refreshToken))
                .build();

        refreshTokenApplicationService.insert(token);

        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰 갱신
    public TokenPair reissueToken(String refreshToken) {
        // refresh token 검증
        if(!tokenProvider.validateToken(refreshToken))
            throw new InvalidTokenException("The Refresh Token has expired. Please log in again.");
        if (refreshTokenApplicationService.checkNotExistedRefreshToken(refreshToken))
            throw new InvalidTokenException("Failed to find Refresh Token");

        // access token 재발급
        UUID memberUuid = tokenProvider.getMemberUuidFromToken(refreshToken);
        SiteMemberResponse siteMember = memberApplicationService.getByUuid(memberUuid)
                .orElseThrow(() -> new TokenDataNotFoundException("Failed to find Site member"));
        SiteMemberRoleResponse siteMemberRole = memberRoleApplicationService.getByUuid(memberUuid)
                .orElseThrow(() -> new TokenDataNotFoundException("Failed to find Site member role"));

        Map<String,String> claims = new HashMap<>();
        claims.put("nickname",siteMember.nickname());
        claims.put("role",siteMemberRole.role().getValue());
        String accessToken = tokenProvider.generateAccessToken(memberUuid,claims);

        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰 삭제
    public void removeToken(String refreshToken) {
        // 토큰 검증
        tokenProvider.validateToken(refreshToken);
        if (refreshTokenApplicationService.checkNotExistedRefreshToken(refreshToken))
            return ;

        UUID memberUuid = tokenProvider.getMemberUuidFromToken(refreshToken);
        UUID deviceId = refreshTokenApplicationService.getByRefreshToken(refreshToken)
                .map(RefreshToken::getDeviceId)
                .orElseThrow(() -> new TokenDataNotFoundException("Failed to find Device ID"));

        RefreshToken token = refreshTokenApplicationService.getByMemberUuidAndDeviceId(memberUuid,deviceId)
                .orElseThrow(() -> new TokenDataNotFoundException("Failed to find Refresh Token"));

        tokenValidationService.validateNotFoundTokenUuid(token.getUuid());
        refreshTokenApplicationService.removeByUuid(token.getUuid());
    }
}
