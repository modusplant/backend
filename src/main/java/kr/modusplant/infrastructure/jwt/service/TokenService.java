package kr.modusplant.infrastructure.jwt.service;


import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberRoleJpaRepository;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.exception.InvalidTokenException;
import kr.modusplant.infrastructure.jwt.exception.TokenNotFoundException;
import kr.modusplant.infrastructure.jwt.framework.out.jpa.entity.RefreshTokenEntity;
import kr.modusplant.infrastructure.jwt.framework.out.jpa.repository.RefreshTokenJpaRepository;
import kr.modusplant.infrastructure.jwt.framework.out.redis.AccessTokenRedisRepository;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * JWT 토큰 관리 횡단 관심사 서비스
 * <p>
 * 기능 : 토큰 생성/삭제/검증 및 갱신/블랙리스트 기능을 담당
 * 사용 방법 : 이 클래스의 public 메서드들을 직접 주입받아 사용
 */
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final SiteMemberJpaRepository siteMemberJpaRepository;
    private final SiteMemberRoleJpaRepository siteMemberRoleJpaRepository;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final AccessTokenRedisRepository accessTokenRedisRepository;

    // 토큰 생성
    public TokenPair issueToken(UUID memberUuid, String nickname, Role role) {
        // memberUuid 검증
        if (memberUuid == null || !siteMemberJpaRepository.existsByUuid(memberUuid)) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, EntityName.SITE_MEMBER);
        }

        // accessToken , refresh token 생성
        Map<String,String> claims = createClaims(nickname,role);
        String accessToken = jwtTokenProvider.generateAccessToken(memberUuid, claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberUuid);

        // refresh token DB에 저장
        refreshTokenJpaRepository.save(
                RefreshTokenEntity.builder()
                        .member(siteMemberJpaRepository.findByUuid(memberUuid).orElseThrow())                   // memberUuid로 member 가져오기
                        .refreshToken(refreshToken)
                        .issuedAt(convertToLocalDateTime(jwtTokenProvider.getIssuedAtFromToken(refreshToken)))
                        .expiredAt(convertToLocalDateTime(jwtTokenProvider.getExpirationFromToken(refreshToken)))
                        .build()
        );

        return new TokenPair(accessToken,refreshToken);
    }

    // 토큰 삭제
    public void removeToken(String refreshToken) {
        // 토큰 검증
        jwtTokenProvider.validateToken(refreshToken);
        validateNotFoundRefreshToken(refreshToken);

        // refresh token 조회
        UUID memberUuid = jwtTokenProvider.getMemberUuidFromToken(refreshToken);
        SiteMemberEntity memberEntity = siteMemberJpaRepository.findByUuid(memberUuid).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, EntityName.SITE_MEMBER));
        RefreshTokenEntity refreshTokenEntity = refreshTokenJpaRepository.findByMemberAndRefreshToken(memberEntity,refreshToken).orElseThrow(TokenNotFoundException::new);

        // 토큰 삭제
        refreshTokenJpaRepository.deleteByUuid(refreshTokenEntity.getUuid());
    }

    // 토큰 검증 및 재발급
    public TokenPair verifyAndReissueToken(String accessToken, String refreshToken) {
        // 블랙리스트 확인
        if (accessTokenRedisRepository.isBlacklisted(accessToken)) {
            throw new InvalidTokenException();
        }
        // refresh token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }
        // access token 유효성 검증
        if (jwtTokenProvider.validateToken(accessToken)) {
            return new TokenPair(accessToken,refreshToken);
        }
        // access token 만료 시 토큰 갱신
        return reissueTokenWithValidRefreshToken(refreshToken);
    }

    // access token 블랙리스트
    public void blacklistAccessToken(String accessToken) {
        if (jwtTokenProvider.validateToken(accessToken)) {
            Instant expiration = jwtTokenProvider.getExpirationFromToken(accessToken).toInstant();
            Long expirationSeconds = Duration.between(Instant.now(),expiration).getSeconds();
            accessTokenRedisRepository.addToBlacklist(accessToken, expirationSeconds);
        }
    }

    // 블랙리스트에서 access token 제거
    public void removeAccessTokenFromBlacklist(String accessToken) {
        accessTokenRedisRepository.removeFromBlacklist(accessToken);
    }

    // 토큰 갱신
    private TokenPair reissueTokenWithValidRefreshToken(String refreshToken) {
        // refresh token 검증
        validateNotFoundRefreshToken(refreshToken);

        // token에서 사용자 정보 가져오기
        UUID memberUuid = jwtTokenProvider.getMemberUuidFromToken(refreshToken);

        SiteMemberEntity memberEntity = siteMemberJpaRepository.findByUuid(memberUuid).orElseThrow(TokenNotFoundException::new);
        SiteMemberRoleEntity memberRoleEntity = siteMemberRoleJpaRepository.findByUuid(memberUuid).orElseThrow(TokenNotFoundException::new);

        // refresh token 재발급 (RTR기법)
        String reissuedRefreshToken = jwtTokenProvider.generateRefreshToken(memberUuid);
        refreshTokenJpaRepository.save(
                RefreshTokenEntity.builder()
                        .uuid(refreshTokenJpaRepository.findByRefreshToken(refreshToken).orElseThrow().getUuid())
                        .member(memberEntity)
                        .refreshToken(reissuedRefreshToken)
                        .issuedAt(convertToLocalDateTime(jwtTokenProvider.getIssuedAtFromToken(reissuedRefreshToken)))
                        .expiredAt(convertToLocalDateTime(jwtTokenProvider.getExpirationFromToken(reissuedRefreshToken)))
                        .build()
        );

        // access token 재발급
        Map<String,String> claims = createClaims(memberEntity.getNickname(),memberRoleEntity.getRole());
        String accessToken = jwtTokenProvider.generateAccessToken(memberUuid,claims);

        return new TokenPair(accessToken,reissuedRefreshToken);
    }

    private Map<String,String> createClaims(String nickname, Role role) {
        Map<String,String> claims = new HashMap<>();
        claims.put("nickname", nickname);
        claims.put("role", role.name());
        return claims;
    }

    private void validateNotFoundRefreshToken(String refreshToken) {
        if (refreshToken == null || !refreshTokenJpaRepository.existsByRefreshToken(refreshToken)) {
            throw new TokenNotFoundException();
        }
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }
}
