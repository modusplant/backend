package kr.modusplant.modules.jwt.domain.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberRoleCrudService;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.domain.service.supers.RefreshTokenCrudService;
import kr.modusplant.modules.jwt.dto.TokenPair;
import kr.modusplant.global.enums.Role;
import kr.modusplant.modules.jwt.error.InvalidTokenException;
import kr.modusplant.modules.jwt.error.TokenDataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER_UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final SiteMemberCrudService siteMemberService;
    private final SiteMemberRoleCrudService siteMemberRoleService;
    private final RefreshTokenCrudService refreshTokenCrudService;
    private final TokenValidationService tokenValidationService;

    // 토큰 생성
    public TokenPair issueToken(UUID memberUuid, String nickname, Role role, UUID deviceId) {
        // memberUuid, deviceId 검증
        tokenValidationService.validateNotFoundMemberUuid(MEMBER_UUID, memberUuid);
        if (tokenValidationService.validateExistedDeviceId(deviceId))
            throw new InvalidTokenException("Device Id already exists");

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

        refreshTokenCrudService.insert(token);

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
        if(tokenValidationService.validateNotFoundRefreshToken(refreshToken))
            throw new InvalidTokenException("Failed to find Refresh Token");

        // access token 재발급
        UUID memberUuid = tokenProvider.getMemberUuidFromToken(refreshToken);
        SiteMember siteMember = siteMemberService.getByUuid(memberUuid)
                .orElseThrow(() -> new TokenDataNotFoundException("Failed to find Site member"));
        SiteMemberRole siteMemberRole = siteMemberRoleService.getByMember(siteMember)
                .orElseThrow(() -> new TokenDataNotFoundException("Failed to find Site member role"));

        Map<String,String> claims = new HashMap<>();
        claims.put("nickname",siteMember.getNickname());
        claims.put("role",siteMemberRole.getRole().getValue());
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
        if (tokenValidationService.validateNotFoundRefreshToken(refreshToken))
            return ;

        UUID memberUuid = tokenProvider.getMemberUuidFromToken(refreshToken);
        UUID deviceId = refreshTokenCrudService.getByRefreshToken(refreshToken)
                .map(RefreshToken::getDeviceId)
                .orElseThrow(() -> new TokenDataNotFoundException("Failed to find Device ID"));

        RefreshToken token = refreshTokenCrudService.getByMemberUuidAndDeviceId(memberUuid,deviceId)
                .orElseThrow(() -> new TokenDataNotFoundException("Failed to find Refresh Token"));

        tokenValidationService.validateNotFoundTokenUuid(token.getUuid());
        refreshTokenCrudService.removeByUuid(token.getUuid());
    }
}
