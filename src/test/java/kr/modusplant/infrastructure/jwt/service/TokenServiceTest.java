package kr.modusplant.infrastructure.jwt.service;

import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberRoleEntityTestUtils;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberRoleJpaRepository;
import kr.modusplant.infrastructure.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.exception.InvalidTokenException;
import kr.modusplant.infrastructure.jwt.exception.TokenNotFoundException;
import kr.modusplant.infrastructure.jwt.framework.out.jpa.entity.RefreshTokenEntity;
import kr.modusplant.infrastructure.jwt.framework.out.jpa.repository.RefreshTokenJpaRepository;
import kr.modusplant.infrastructure.jwt.framework.out.redis.AccessTokenRedisRepository;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest implements SiteMemberEntityTestUtils, SiteMemberRoleEntityTestUtils, RefreshTokenEntityTestUtils {
    @InjectMocks
    private TokenService tokenService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private SiteMemberJpaRepository siteMemberJpaRepository;
    @Mock
    private SiteMemberRoleJpaRepository siteMemberRoleJpaRepository;
    @Mock
    private RefreshTokenJpaRepository refreshTokenJpaRepository;
    @Mock
    private AccessTokenRedisRepository accessTokenRedisRepository;

    private SiteMemberEntity memberEntity;
    private RefreshTokenEntity refreshTokenEntity;
    private UUID memberUuid;
    private String nickname;
    private Role role;
    private String accessToken;
    private String refreshToken;
    private Map<String,String> claims;
    private Date issuedAt;
    private Date expiredAt;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        refreshTokenEntity = createRefreshTokenBasicEntityBuilder()
                .uuid(UUID.randomUUID())
                .member(memberEntity)
                .build();
        memberUuid = memberEntity.getUuid();
        nickname = memberEntity.getNickname();
        role = Role.USER;
        accessToken = "access-token";
        refreshToken = refreshTokenEntity.getRefreshToken();
        claims = Map.of(
                "nickname", nickname,
                "role", role.name()
        );
        issuedAt = Date.from(Instant.now());
        expiredAt = Date.from(Instant.now().plusSeconds(3600));
    }

    @Nested
    @DisplayName("토큰 생성 테스트")
    class testIssueToken {
        @Test
        @DisplayName("회원 uuid, nickname, role로 TokenPair 생성하기")
        void testIssueToken_givenMemberUuidAndNicknameAndRole_willReturnTokenPair() {
            // given
            given(siteMemberJpaRepository.existsByUuid(memberUuid)).willReturn(true);
            given(jwtTokenProvider.generateAccessToken(memberUuid, claims)).willReturn(accessToken);
            given(jwtTokenProvider.generateRefreshToken(memberUuid)).willReturn(refreshToken);
            given(jwtTokenProvider.getIssuedAtFromToken(refreshToken)).willReturn(issuedAt);
            given(jwtTokenProvider.getExpirationFromToken(refreshToken)).willReturn(expiredAt);
            given(siteMemberJpaRepository.findByUuid(memberUuid)).willReturn(Optional.of(memberEntity));
            given(refreshTokenJpaRepository.save(any(RefreshTokenEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            TokenPair result = tokenService.issueToken(memberUuid, nickname, role);

            // then
            assertThat(result.accessToken()).isEqualTo(accessToken);
            assertThat(result.refreshToken()).isEqualTo(refreshToken);
        }

        @Test
        @DisplayName("존재하지 않는 회원으로 토큰 발급 시 예외 발생")
        void testIssueToken_givenNotExistMember_willThrowException() {
            // given
            given(siteMemberJpaRepository.existsByUuid(memberUuid)).willReturn(false);

            // when & then
            assertThrows(EntityNotFoundException.class, () -> tokenService.issueToken(memberUuid,nickname,Role.USER));
        }
    }

    @Nested
    @DisplayName("토큰 삭제 테스트")
    class testRemoveToken {
        @Test
        @DisplayName("refresh token으로 token 삭제")
        void testRemoveToken_givenRefreshToken_willRemoveToken() {
            // given
            given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
            given(refreshTokenJpaRepository.existsByRefreshToken(refreshToken)).willReturn(true);
            given(jwtTokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
            given(siteMemberJpaRepository.findByUuid(memberUuid)).willReturn(Optional.of(memberEntity));
            given(refreshTokenJpaRepository.findByMemberAndRefreshToken(memberEntity, refreshToken)).willReturn(Optional.of(refreshTokenEntity));

            // when
            tokenService.removeToken(refreshToken);

            // then
            verify(refreshTokenJpaRepository).deleteByUuid(refreshTokenEntity.getUuid());
        }

        @Test
        @DisplayName("존재하지 않는 refresh token 삭제 시 예외 발생")
        void testRemoveToken_givenNotExistRefreshToken_willThrowException() {
            // given
            given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
            given(refreshTokenJpaRepository.existsByRefreshToken(refreshToken)).willReturn(false);

            // when & then
            assertThrows(TokenNotFoundException.class, () -> tokenService.removeToken(refreshToken));
            verify(jwtTokenProvider, never()).getMemberUuidFromToken(any());
        }
    }

    @Nested
    @DisplayName("토큰 검증 및 재발급 테스트")
    class testVerifyAndReissueToken {
        @Test
        @DisplayName("유효한 access token과 refresh token으로 검증 시 토큰 그대로 반환")
        void testVerifyAndReissueToken_givenValidAccessTokenAndRefreshToken_willReturnToken() {
            // given
            given(accessTokenRedisRepository.isBlacklisted(accessToken)).willReturn(false);
            given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
            given(jwtTokenProvider.validateToken(accessToken)).willReturn(true);

            // when
            TokenPair result = tokenService.verifyAndReissueToken(accessToken, refreshToken);

            // then
            assertThat(result.accessToken()).isEqualTo(accessToken);
            assertThat(result.refreshToken()).isEqualTo(refreshToken);
        }

        @Test
        @DisplayName("만료된 access token과 유효한 refresh token으로 검증 시 토큰 갱신")
        void testVerifyAndReissueToken_givenExpiredAccessTokenAndValidRefreshToken_willReissue() {
            // given
            String expiredAccessToken = "expired_access_token";
            String newRefreshToken = "new_refresh_token";
            String newAccessToken = "new_access_token";
            given(accessTokenRedisRepository.isBlacklisted(expiredAccessToken)).willReturn(false);
            given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
            given(jwtTokenProvider.validateToken(expiredAccessToken)).willReturn(false);
            given(refreshTokenJpaRepository.existsByRefreshToken(refreshToken)).willReturn(true);
            given(jwtTokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
            given(siteMemberJpaRepository.findByUuid(memberUuid)).willReturn(Optional.of(memberEntity));
            given(siteMemberRoleJpaRepository.findByUuid(memberUuid)).willReturn(Optional.of(createMemberRoleUserEntityWithUuid()));
            given(jwtTokenProvider.generateRefreshToken(memberUuid)).willReturn(newRefreshToken);
            given(refreshTokenJpaRepository.findByRefreshToken(refreshToken)).willReturn(Optional.of(refreshTokenEntity));
            given(jwtTokenProvider.getIssuedAtFromToken(newRefreshToken)).willReturn(issuedAt);
            given(jwtTokenProvider.getExpirationFromToken(newRefreshToken)).willReturn(expiredAt);
            given(refreshTokenJpaRepository.save(any(RefreshTokenEntity.class))).willAnswer(invocation -> invocation.getArgument(0));
            /*given(memberEntity.getNickname()).willReturn("testUser");
            given(memberRoleEntity.getRole()).willReturn(Role.ROLE_USER);*/
            given(jwtTokenProvider.generateAccessToken(eq(memberUuid), any())).willReturn(newAccessToken);

            // when
            TokenPair result = tokenService.verifyAndReissueToken(expiredAccessToken, refreshToken);

            // then
            assertThat(result.accessToken()).isEqualTo(newAccessToken);
            assertThat(result.refreshToken()).isEqualTo(newRefreshToken);
        }

        @Test
        @DisplayName("블랙리스트에 있는 access token으로 검증 시 예외 발생")
        void testVerifyAndReissueToken_givenBlacklistedAccessToken_willThrowException() {
            // given
            given(accessTokenRedisRepository.isBlacklisted(accessToken)).willReturn(true);

            // when & then
            assertThrows(InvalidTokenException.class , () -> tokenService.verifyAndReissueToken(accessToken,refreshToken));
            verify(accessTokenRedisRepository).isBlacklisted(accessToken);
            verify(jwtTokenProvider,never()).validateToken(any());
        }

        @Test
        @DisplayName("유효하지 않은 refresh token으로 검증 시 예외 발생")
        void testVerifyAndReissueToken_givenInvalidRefreshToken_willThrowException() {
            // given
            String invalidRefreshToken = "invalid_refresh_token";
            given(accessTokenRedisRepository.isBlacklisted(accessToken)).willReturn(false);
            given(jwtTokenProvider.validateToken(invalidRefreshToken)).willReturn(false);

            // when & then
            assertThrows(InvalidTokenException.class , () -> tokenService.verifyAndReissueToken(accessToken,invalidRefreshToken));
            verify(accessTokenRedisRepository).isBlacklisted(accessToken);
            verify(jwtTokenProvider).validateToken(invalidRefreshToken);
            verify(jwtTokenProvider,never()).validateToken(accessToken);
        }
    }

    @Nested
    @DisplayName("access token 블랙리스트 추가 테스트")
    class testBlacklistAccessToken {
        @Test
        @DisplayName("access token을 블랙리스트에 추가")
        void testBlacklistAccessToken_givenAccessToken_willAddToBlacklist() {
            // given
            given(jwtTokenProvider.validateToken(accessToken)).willReturn(true);
            given(jwtTokenProvider.getExpirationFromToken(accessToken)).willReturn(expiredAt);
            willDoNothing().given(accessTokenRedisRepository).addToBlacklist(eq(accessToken),any(Long.class));

            // when
            tokenService.blacklistAccessToken(accessToken);

            // then
            verify(accessTokenRedisRepository).addToBlacklist(eq(accessToken),any(Long.class));
        }

        @Test
        @DisplayName("유효하지 않은 access token은 블랙리스트에 추가하지 않기")
        void testBlacklistAccessToken_givenInvalidAccessToken_willNotAddToBlacklist() {
            // given
            given(jwtTokenProvider.validateToken(accessToken)).willReturn(false);

            // when
            tokenService.blacklistAccessToken(accessToken);

            // then
            verify(jwtTokenProvider).validateToken(accessToken);
            verify(jwtTokenProvider, never()).getExpirationFromToken(accessToken);
            verify(accessTokenRedisRepository, never()).addToBlacklist(eq(accessToken),any(Long.class));
        }
    }

    @Nested
    @DisplayName("access token 블랙리스트 제거 테스트")
    class testRemoveAccessTokenFromBlacklist {
        @Test
        @DisplayName("블랙리스트에서 access token 제거하기")
        void testRemoveAccessTokenFromBlacklist_givenAccessToken_willDelete() {
            // given
            willDoNothing().given(accessTokenRedisRepository).removeFromBlacklist(accessToken);

            // when
            tokenService.removeAccessTokenFromBlacklist(accessToken);

            // then
            verify(accessTokenRedisRepository).removeFromBlacklist(accessToken);
        }
    }
}