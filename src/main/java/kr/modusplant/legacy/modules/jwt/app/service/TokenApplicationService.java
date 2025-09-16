package kr.modusplant.legacy.modules.jwt.app.service;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.legacy.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.legacy.domains.member.app.service.SiteMemberRoleApplicationService;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.legacy.modules.jwt.app.dto.TokenPair;
import kr.modusplant.legacy.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.legacy.modules.jwt.domain.service.TokenValidationService;
import kr.modusplant.legacy.modules.jwt.error.InvalidTokenException;
import kr.modusplant.legacy.modules.jwt.error.TokenNotFoundException;
import kr.modusplant.legacy.modules.jwt.persistence.repository.TokenRedisRepository;
import kr.modusplant.domains.identity.framework.legacy.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenApplicationService {
    private final TokenProvider tokenProvider;
    private final SiteMemberApplicationService memberApplicationService;
    private final SiteMemberRoleApplicationService memberRoleApplicationService;
    private final RefreshTokenApplicationService refreshTokenApplicationService;
    private final TokenValidationService tokenValidationService;
    private final SiteMemberValidationService memberValidationService;
    private final TokenRedisRepository tokenRedisRepository;

    // 토큰 생성
    public TokenPair issueToken(UUID memberUuid, String nickname, Role role) {
        // memberUuid 검증
        memberValidationService.validateNotFoundUuid(memberUuid);

        // accessToken , refresh token 생성
        Map<String,String> claims = createClaims(nickname,role);
        String accessToken = tokenProvider.generateAccessToken(memberUuid, claims);
        String refreshToken = tokenProvider.generateRefreshToken(memberUuid);

        // refresh token DB에 저장
        refreshTokenApplicationService.insert(
                RefreshToken.builder()
                        .memberUuid(memberUuid)
                        .refreshToken(refreshToken)
                        .issuedAt(tokenProvider.getIssuedAtFromToken(refreshToken))
                        .expiredAt(tokenProvider.getExpirationFromToken(refreshToken))
                        .build());

        return new TokenPair(accessToken,refreshToken);
    }

    // 토큰 삭제
    public void removeToken(String refreshToken) {
        // 토큰 검증
        tokenProvider.validateToken(refreshToken);
        tokenValidationService.validateNotFoundRefreshToken(refreshToken);
        UUID memberUuid = tokenProvider.getMemberUuidFromToken(refreshToken);
        RefreshToken token = refreshTokenApplicationService.getByMemberUuidAndRefreshToken(memberUuid,refreshToken)
                .orElseThrow(TokenNotFoundException::new);
        // 토큰 삭제
        refreshTokenApplicationService.removeByUuid(token.getUuid());
    }

    // 토큰 검증 및 재발급
    public TokenPair verifyAndReissueToken(String accessToken, String refreshToken) {
        // 블랙리스트 확인
        if (tokenRedisRepository.isBlacklisted(accessToken)) {
            throw new InvalidTokenException();
        }
        // refresh token 유효성 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }
        // access token 유효성 검증
        if (tokenProvider.validateToken(accessToken)) {
            return new TokenPair(accessToken,refreshToken);
        }
        // access token 만료 시 토큰 갱신
        return reissueTokenWithValidRefreshToken(refreshToken);
    }

    // access token 블랙리스트
    public void blacklistAccessToken(String accessToken) {
        if (tokenProvider.validateToken(accessToken)) {
            Instant expiration = tokenProvider.getExpirationFromToken(accessToken).toInstant();
            Long expirationSeconds = Duration.between(Instant.now(),expiration).getSeconds();
            tokenRedisRepository.addToBlacklist(accessToken, expirationSeconds);
        }
    }

    // 블랙리스트에서 access token 제거
    public void removeAccessTokenFromBlacklist(String accessToken) {
        tokenRedisRepository.removeFromBlacklist(accessToken);
    }

    // 토큰 갱신
    private TokenPair reissueTokenWithValidRefreshToken(String refreshToken) {
        // refresh token 검증
        tokenValidationService.validateNotFoundRefreshToken(refreshToken);

        // token에서 사용자 정보 가져오기
        UUID memberUuid = tokenProvider.getMemberUuidFromToken(refreshToken);
        SiteMemberResponse siteMember = memberApplicationService.getByUuid(memberUuid)
                .orElseThrow(TokenNotFoundException::new);
        SiteMemberRoleResponse siteMemberRole = memberRoleApplicationService.getByUuid(memberUuid)
                .orElseThrow(TokenNotFoundException::new);

        // refresh token 재발급 (RTR기법)
        String reissuedRefreshToken = tokenProvider.generateRefreshToken(memberUuid);
        refreshTokenApplicationService.insert(
                RefreshToken.builder()
                        .uuid(refreshTokenApplicationService.getByRefreshToken(refreshToken).orElseThrow().getUuid())
                        .memberUuid(memberUuid)
                        .refreshToken(reissuedRefreshToken)
                        .issuedAt(tokenProvider.getIssuedAtFromToken(reissuedRefreshToken))
                        .expiredAt(tokenProvider.getExpirationFromToken(reissuedRefreshToken))
                        .build());

        // access token 재발급
        Map<String,String> claims = createClaims(siteMember.nickname(),siteMemberRole.role());
        String accessToken = tokenProvider.generateAccessToken(memberUuid,claims);

        return new TokenPair(accessToken,reissuedRefreshToken);
    }

    private Map<String,String> createClaims(String nickname, Role role) {
        Map<String,String> claims = new HashMap<>();
        claims.put("nickname", nickname);
        claims.put("role", role.name());
        return claims;
    }
}
