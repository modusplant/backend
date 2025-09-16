package kr.modusplant.legacy.modules.jwt.app.service;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.legacy.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.legacy.domains.member.app.service.SiteMemberRoleApplicationService;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberRoleResponseTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.legacy.modules.jwt.app.dto.TokenPair;
import kr.modusplant.legacy.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.legacy.modules.jwt.domain.service.TokenValidationService;
import kr.modusplant.legacy.modules.jwt.error.InvalidTokenException;
import kr.modusplant.legacy.modules.jwt.error.TokenNotFoundException;
import kr.modusplant.legacy.modules.jwt.persistence.repository.TokenRedisRepository;
import kr.modusplant.domains.identity.framework.legacy.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TokenApplicationServiceTest implements SiteMemberEntityTestUtils, SiteMemberRoleResponseTestUtils {
    @InjectMocks
    @Spy
    private TokenApplicationService tokenApplicationService;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private SiteMemberApplicationService memberApplicationService;
    @Mock
    private SiteMemberRoleApplicationService memberRoleApplicationService;
    @Mock
    private RefreshTokenApplicationService refreshTokenApplicationService;
    @Mock
    private TokenValidationService tokenValidationService;
    @Mock
    private SiteMemberValidationService memberValidationService;
    @Mock
    private TokenRedisRepository tokenRedisRepository;

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
        memberUuid = UUID.randomUUID();
        nickname = "testUser";
        role = Role.USER;
        accessToken = "access-token";
        refreshToken = "refresh-token";
        claims = Map.of(
                "nickname", nickname,
                "role", role.name()
        );
        issuedAt = Date.from(Instant.now());
        expiredAt = Date.from(Instant.now().plusSeconds(3600));
    }

    @Nested
    @DisplayName("토큰 생성 테스트")
    class issueTokenTest {
        @Test
        @DisplayName("토큰 생성 성공 테스트")
        void issueTokenSuccess() {
            // given
            willDoNothing().given(memberValidationService).validateNotFoundUuid(memberUuid);
            given(tokenProvider.generateAccessToken(memberUuid, claims)).willReturn(accessToken);
            given(tokenProvider.generateRefreshToken(memberUuid)).willReturn(refreshToken);
            given(tokenProvider.getIssuedAtFromToken(refreshToken)).willReturn(issuedAt);
            given(tokenProvider.getExpirationFromToken(refreshToken)).willReturn(expiredAt);
            given(refreshTokenApplicationService.insert(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            TokenPair tokenPair = tokenApplicationService.issueToken(memberUuid, nickname, role);

            // then
            assertNotNull(tokenPair);
            assertEquals(accessToken, tokenPair.accessToken());
            assertEquals(refreshToken, tokenPair.refreshToken());

            verify(memberValidationService).validateNotFoundUuid(memberUuid);
            verify(tokenProvider).generateAccessToken(eq(memberUuid), anyMap());
            verify(tokenProvider).generateRefreshToken(memberUuid);
            verify(refreshTokenApplicationService).insert(any(RefreshToken.class));
        }
    }

    @Nested
    @DisplayName("토큰 삭제 테스트")
    class removeTokenTest {
        @Test
        @DisplayName("토큰 삭제 성공 테스트")
        void removeTokenSuccess() {
            // given
            RefreshToken mockRefreshToken = RefreshToken.builder()
                    .uuid(UUID.randomUUID())
                    .memberUuid(memberUuid)
                    .refreshToken(refreshToken)
                    .build();

            given(tokenProvider.validateToken(refreshToken)).willReturn(true);
            willDoNothing().given(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
            given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
            given(refreshTokenApplicationService.getByMemberUuidAndRefreshToken(memberUuid, refreshToken)).willReturn(Optional.of(mockRefreshToken));

            // when
            tokenApplicationService.removeToken(refreshToken);

            // then
            verify(refreshTokenApplicationService).removeByUuid(mockRefreshToken.getUuid());
        }

        @Test
        @DisplayName("토큰 삭제 실패 테스트 : refresh token 조회 불가")
        void shouldFailToRemoveTokenWhenRefreshTokenNotFound() {
            // given
            given(tokenProvider.validateToken(refreshToken)).willReturn(true);
            willDoNothing().given(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
            given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
            given(refreshTokenApplicationService.getByMemberUuidAndRefreshToken(memberUuid, refreshToken)).willReturn(Optional.empty());

            // when & then
            assertThrows(TokenNotFoundException.class, () -> tokenApplicationService.removeToken(refreshToken));
            verify(refreshTokenApplicationService, never()).removeByUuid(any());
        }
    }

    @Nested
    @DisplayName("토큰 검증 및 재발급 테스트")
    class verifyAndReissueTokenTest {
        @Test
        @DisplayName("access token 만료 전이면 토큰 검증 성공 및 기존 토큰 반환하기")
        void shouldReturnTokensWhenBothTokensAreValidAndNotExpired() {
            // given
            given(tokenRedisRepository.isBlacklisted(accessToken)).willReturn(false);
            given(tokenProvider.validateToken(refreshToken)).willReturn(true);
            given(tokenProvider.validateToken(accessToken)).willReturn(true);

            // when
            TokenPair result = tokenApplicationService.verifyAndReissueToken(accessToken,refreshToken);

            // then
            assertThat(result.accessToken()).isEqualTo(accessToken);
            assertThat(result.refreshToken()).isEqualTo(refreshToken);

            verify(tokenRedisRepository).isBlacklisted(accessToken);
            verify(tokenProvider).validateToken(refreshToken);
            verify(tokenProvider).validateToken(accessToken);
            verify(tokenValidationService, never()).validateNotFoundRefreshToken(refreshToken);
        }

        @Test
        @DisplayName("access token이 블랙리스트에 있으면 검증이 실패한다.")
        void shouldFailTokenVerificationWhenAccessTokenIsBlacklisted() {
            // given
            given(tokenRedisRepository.isBlacklisted(accessToken)).willReturn(true);

            // when & then
            assertThrows(InvalidTokenException.class , () -> tokenApplicationService.verifyAndReissueToken(accessToken,refreshToken));

            verify(tokenRedisRepository).isBlacklisted(accessToken);
            verify(tokenProvider,never()).validateToken(refreshToken);
        }

        @Test
        @DisplayName("refresh token이 만료되면 검증이 실패한다.")
        void shouldFailTokenVerificationWhenRefreshTokenIsExpired() {
            // given
            given(tokenRedisRepository.isBlacklisted(accessToken)).willReturn(false);
            given(tokenProvider.validateToken(refreshToken)).willReturn(false);

            // when & then
            assertThrows(InvalidTokenException.class, () -> tokenApplicationService.verifyAndReissueToken(accessToken,refreshToken));

            verify(tokenRedisRepository).isBlacklisted(accessToken);
            verify(tokenProvider).validateToken(refreshToken);
            verify(tokenProvider,never()).validateToken(accessToken);
        }

        @Test
        @DisplayName("access token 만료 시 토큰 검증 성공 및 토큰 갱신 성공하기")
        void shouldReissueTokensWhenAccessTokenExpiredAndRefreshTokenIsValid() {
            // given
            SiteMemberResponse siteMemberResponse = mock(SiteMemberResponse.class);
            SiteMemberRoleResponse siteMemberRoleResponse = mock(SiteMemberRoleResponse.class);
            given(siteMemberResponse.nickname()).willReturn(nickname);
            given(siteMemberRoleResponse.role()).willReturn(role);
            String newRefreshToken = "new-refresh-token";
            String newAccessToken = "new-access-token";
            RefreshToken oldToken = RefreshToken.builder()
                    .uuid(UUID.randomUUID())
                    .memberUuid(memberUuid)
                    .refreshToken(refreshToken)
                    .build();

            given(tokenRedisRepository.isBlacklisted(accessToken)).willReturn(false);
            given(tokenProvider.validateToken(refreshToken)).willReturn(true);
            given(tokenProvider.validateToken(accessToken)).willReturn(false);
            willDoNothing().given(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
            given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
            given(memberApplicationService.getByUuid(memberUuid)).willReturn(Optional.of(siteMemberResponse));
            given(memberRoleApplicationService.getByUuid(memberUuid)).willReturn(Optional.of(siteMemberRoleResponse));
            given(tokenProvider.generateRefreshToken(memberUuid)).willReturn(newRefreshToken);
            given(refreshTokenApplicationService.getByRefreshToken(refreshToken)).willReturn(Optional.of(oldToken));
            given(tokenProvider.getIssuedAtFromToken(newRefreshToken)).willReturn(issuedAt);
            given(tokenProvider.getExpirationFromToken(newRefreshToken)).willReturn(expiredAt);
            given(refreshTokenApplicationService.insert(any())).willAnswer(invocation -> invocation.getArgument(0));
            given(tokenProvider.generateAccessToken(memberUuid, claims)).willReturn(newAccessToken);


            // when
            TokenPair result = tokenApplicationService.verifyAndReissueToken(accessToken,refreshToken);

            // then
            assertThat(result.accessToken()).isEqualTo(newAccessToken);
            assertThat(result.refreshToken()).isEqualTo(newRefreshToken);

            verify(tokenRedisRepository).isBlacklisted(accessToken);
            verify(tokenProvider).validateToken(refreshToken);
            verify(tokenProvider).validateToken(accessToken);
            verify(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
            verify(tokenProvider).getMemberUuidFromToken(refreshToken);
            verify(memberApplicationService).getByUuid(memberUuid);
            verify(memberRoleApplicationService).getByUuid(memberUuid);
            verify(tokenProvider).generateRefreshToken(memberUuid);
            verify(refreshTokenApplicationService).getByRefreshToken(refreshToken);
            verify(tokenProvider).getIssuedAtFromToken(newRefreshToken);
            verify(tokenProvider).getExpirationFromToken(newRefreshToken);
            verify(refreshTokenApplicationService).insert(any());
            verify(tokenProvider).generateAccessToken(memberUuid,claims);
        }

        @Test
        @DisplayName("토큰 검증이 성공하고 access token이 만료되었을 때, refresh token를 조회할 수 없으면 토큰 갱신이 실패한다.")
        void shouldFailReissueWhenRefreshTokenIsNotFound() {
            // given
            given(tokenRedisRepository.isBlacklisted(accessToken)).willReturn(false);
            given(tokenProvider.validateToken(refreshToken)).willReturn(true);
            given(tokenProvider.validateToken(accessToken)).willReturn(false);
            doThrow(new TokenNotFoundException())
                    .when(tokenValidationService).validateNotFoundRefreshToken(refreshToken);

            // when & then
            assertThrows(TokenNotFoundException.class, () -> tokenApplicationService.verifyAndReissueToken(accessToken,refreshToken));

            verify(tokenRedisRepository).isBlacklisted(accessToken);
            verify(tokenProvider).validateToken(refreshToken);
            verify(tokenProvider).validateToken(accessToken);
            verify(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
            verify(tokenProvider,never()).getMemberUuidFromToken(refreshToken);
        }

        @Test
        @DisplayName("토큰 검증이 성공하고 access token이 만료되었을 때, SiteMember를 조회할 수 없으면 토큰 갱신이 실패한다.")
        void shouldFailReissueWhenSiteMemberNotFound() {
            // given
            given(tokenRedisRepository.isBlacklisted(accessToken)).willReturn(false);
            given(tokenProvider.validateToken(refreshToken)).willReturn(true);
            given(tokenProvider.validateToken(accessToken)).willReturn(false);
            willDoNothing().given(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
            given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
            given(memberApplicationService.getByUuid(memberUuid)).willReturn(Optional.empty());

            // when & then
            assertThrows(TokenNotFoundException.class, () -> tokenApplicationService.verifyAndReissueToken(accessToken,refreshToken));

            verify(tokenRedisRepository).isBlacklisted(accessToken);
            verify(tokenProvider).validateToken(refreshToken);
            verify(tokenProvider).validateToken(accessToken);
            verify(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
            verify(tokenProvider).getMemberUuidFromToken(refreshToken);
            verify(memberApplicationService).getByUuid(memberUuid);
            verify(memberRoleApplicationService,never()).getByUuid(memberUuid);
        }

        @Test
        @DisplayName("토큰 검증이 성공하고 access token이 만료되었을 때, SiteMemberRole을 조회할 수 없으면 토큰 갱신이 실패한다.")
        void shouldFailReissueWhenSiteMemberRoleNotFound() {
            // given
            SiteMemberResponse siteMemberResponse = mock(SiteMemberResponse.class);
            given(tokenRedisRepository.isBlacklisted(accessToken)).willReturn(false);
            given(tokenProvider.validateToken(refreshToken)).willReturn(true);
            given(tokenProvider.validateToken(accessToken)).willReturn(false);
            willDoNothing().given(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
            given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
            given(memberApplicationService.getByUuid(memberUuid)).willReturn(Optional.of(siteMemberResponse));
            given(memberRoleApplicationService.getByUuid(memberUuid)).willReturn(Optional.empty());

            // when & then
            assertThrows(TokenNotFoundException.class, () -> tokenApplicationService.verifyAndReissueToken(accessToken,refreshToken));

            verify(tokenRedisRepository).isBlacklisted(accessToken);
            verify(tokenProvider).validateToken(refreshToken);
            verify(tokenProvider).validateToken(accessToken);
            verify(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
            verify(tokenProvider).getMemberUuidFromToken(refreshToken);
            verify(memberApplicationService).getByUuid(memberUuid);
            verify(memberRoleApplicationService).getByUuid(memberUuid);
            verify(tokenProvider,never()).generateRefreshToken(memberUuid);
        }
    }

    @Nested
    @DisplayName("access token 블랙리스트 추가 테스트")
    class blacklistAccessTokenTest {
        @Test
        @DisplayName("유효한 access token인 경우 블랙리스트 추가에 성공한다.")
        void blacklistAccessTokenSuccess() {
            // given
            given(tokenProvider.validateToken(accessToken)).willReturn(true);
            given(tokenProvider.getExpirationFromToken(accessToken)).willReturn(expiredAt);
            willDoNothing().given(tokenRedisRepository).addToBlacklist(eq(accessToken),any(Long.class));

            // when
            tokenApplicationService.blacklistAccessToken(accessToken);

            // then
            verify(tokenRedisRepository).addToBlacklist(eq(accessToken),any(Long.class));
        }

        @Test
        @DisplayName("유효하지 않은 access token인 경우 블랙리스트 추가에 실패한다.")
        void shouldFailToAddAccessTokenToBlacklistWhenTokenIsInvalid() {
            // given
            given(tokenProvider.validateToken(accessToken)).willReturn(false);

            // when
            tokenApplicationService.blacklistAccessToken(accessToken);

            // then
            verify(tokenProvider).validateToken(accessToken);
            verify(tokenProvider, never()).getExpirationFromToken(accessToken);
            verify(tokenRedisRepository, never()).addToBlacklist(eq(accessToken),any(Long.class));
        }
    }

    @Nested
    @DisplayName("access token 블랙리스트 제거 테스트")
    class removeAccessTokenFromBlacklistTest {
        @Test
        @DisplayName("access token 블랙리스트 제거한다.")
        void removeAccessTokenFromBlacklistSuccess() {
            // given
            willDoNothing().given(tokenRedisRepository).removeFromBlacklist(accessToken);

            // when
            tokenApplicationService.removeAccessTokenFromBlacklist(accessToken);

            // then
            verify(tokenRedisRepository).removeFromBlacklist(accessToken);
        }
    }
}
