package kr.modusplant.modules.jwt.app.service;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberRoleApplicationService;
import kr.modusplant.domains.member.common.util.app.http.response.SiteMemberRoleResponseTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.global.enums.Role;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.error.InvalidTokenException;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.domain.service.TokenValidationService;
import kr.modusplant.modules.jwt.error.TokenNotFoundException;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
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

    @Test
    @DisplayName("토큰 생성 테스트")
    void issueTokenSuccess() {
        // given
        willDoNothing().given(tokenValidationService).validateNotFoundMemberUuid(memberUuid);
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

        verify(tokenValidationService).validateNotFoundMemberUuid(memberUuid);
        verify(tokenProvider).generateAccessToken(eq(memberUuid), anyMap());
        verify(tokenProvider).generateRefreshToken(memberUuid);
        verify(refreshTokenApplicationService).insert(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    void reissueTokenSuccess() {
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

        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
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
        TokenPair result = tokenApplicationService.reissueToken(refreshToken);

        // then
        assertThat(result.accessToken()).isEqualTo(newAccessToken);
        assertThat(result.refreshToken()).isEqualTo(newRefreshToken);

        then(tokenProvider).should().validateToken(refreshToken);
        then(tokenValidationService).should().validateNotFoundRefreshToken(refreshToken);
        then(tokenProvider).should().getMemberUuidFromToken(refreshToken);
        then(memberApplicationService).should().getByUuid(memberUuid);
        then(memberRoleApplicationService).should().getByUuid(memberUuid);
        then(tokenProvider).should().generateRefreshToken(memberUuid);
        then(refreshTokenApplicationService).should().getByRefreshToken(refreshToken);
        then(tokenProvider).should().getIssuedAtFromToken(newRefreshToken);
        then(tokenProvider).should().getExpirationFromToken(newRefreshToken);
        then(refreshTokenApplicationService).should().insert(any(RefreshToken.class));
        then(tokenProvider).should().generateAccessToken(eq(memberUuid), eq(claims));
    }

    @Test
    @DisplayName("토큰 갱신 실패 테스트 : refresh token 만료")
    void reissueTokenFailWhenRefreshTokenExpired() {
        // given
        given(tokenProvider.validateToken(refreshToken)).willReturn(false);
        // then
        assertThrows(InvalidTokenException.class, () -> tokenApplicationService.reissueToken(refreshToken));
    }

    @Test
    @DisplayName("토큰 갱신 실패 테스트 : refresh token 조회 불가")
    void reissueTokenFailWhenRefreshTokenNotFoundInDB() {
        // given
        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        doThrow(new EntityNotFoundException(getFormattedExceptionMessage(NOT_FOUND_ENTITY, "refreshToken", refreshToken, RefreshTokenEntity.class)))
                .when(tokenValidationService).validateNotFoundRefreshToken(refreshToken);

        // then
        assertThrows(EntityNotFoundException.class, () -> tokenApplicationService.reissueToken(refreshToken));
    }

    @Test
    @DisplayName("토큰 갱신 실패 테스트 : SiteMember 조회 불가")
    void reissueTokenFailWhenSiteMemberNotFound() {
        // given
        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        willDoNothing().given(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
        given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
        given(memberApplicationService.getByUuid(memberUuid)).willReturn(Optional.empty());

        // then
        assertThrows(TokenNotFoundException.class, () -> tokenApplicationService.reissueToken(refreshToken));
    }

    @Test
    @DisplayName("토큰 갱신 실패 테스트 : SiteMemberRole 조회 불가")
    void reissueTokenFailWhenSiteMemberRoleNotFound() {
        // given
        SiteMemberResponse siteMemberResponse = mock(SiteMemberResponse.class);

        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        willDoNothing().given(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
        given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
        given(memberApplicationService.getByUuid(memberUuid)).willReturn(Optional.of(siteMemberResponse));
        given(memberRoleApplicationService.getByUuid(memberUuid)).willReturn(Optional.empty());

        // then
        assertThrows(TokenNotFoundException.class, () -> tokenApplicationService.reissueToken(refreshToken));
    }

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
    void removeTokenNotFoundEarlyExit() {
        // given
        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        willDoNothing().given(tokenValidationService).validateNotFoundRefreshToken(refreshToken);
        given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
        given(refreshTokenApplicationService.getByMemberUuidAndRefreshToken(memberUuid, refreshToken)).willReturn(Optional.empty());

        // when & then
        assertThrows(TokenNotFoundException.class, () -> tokenApplicationService.removeToken(refreshToken));
        verify(refreshTokenApplicationService, never()).removeByUuid(any());
    }

    @Test
    @DisplayName("토큰 검증 테스트 : access token 만료 전")
    void verifyAndReissueTokenWhenAccessTokenIsNotExpiredTest() {
        // given
        given(tokenProvider.validateToken(accessToken)).willReturn(true);

        // when
        TokenPair result = tokenApplicationService.verifyAndReissueToken(accessToken,refreshToken);

        // then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);

        then(tokenProvider).should().validateToken(accessToken);
        then(tokenProvider).should(never()).validateToken(refreshToken);
    }

    @Test
    @DisplayName("토큰 검증 테스트 : access token 만료")
    void verifyAndReissueTokenWhenAccessTokenIsExpiredTest() {
        // given
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        given(tokenProvider.validateToken(accessToken)).willReturn(false);
        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        doReturn(new TokenPair(newAccessToken,newRefreshToken)).when(tokenApplicationService).reissueToken(refreshToken);

        // when
        TokenPair result = tokenApplicationService.verifyAndReissueToken(accessToken,refreshToken);

        // then
        assertThat(result.accessToken()).isEqualTo(newAccessToken);
        assertThat(result.refreshToken()).isEqualTo(newRefreshToken);

        then(tokenProvider).should().validateToken(accessToken);
        then(tokenProvider).should().validateToken(refreshToken);
        then(tokenApplicationService).should().reissueToken(refreshToken);
    }

}