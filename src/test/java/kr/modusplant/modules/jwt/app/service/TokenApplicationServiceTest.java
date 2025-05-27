package kr.modusplant.modules.jwt.app.service;

import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberRoleApplicationService;
import kr.modusplant.domains.member.common.util.app.http.response.SiteMemberRoleResponseTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.global.enums.Role;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.domain.service.TokenValidationService;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.error.InvalidTokenException;
import kr.modusplant.modules.jwt.error.TokenNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER_UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenApplicationServiceTest implements SiteMemberEntityTestUtils, SiteMemberRoleResponseTestUtils {
    @InjectMocks
    private TokenApplicationService tokenApplicationService;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private SiteMemberApplicationService siteMemberService;
    @Mock
    private SiteMemberRoleApplicationService siteMemberRoleService;
    @Mock
    private RefreshTokenApplicationService refreshTokenApplicationService;
    @Mock
    private TokenValidationService tokenValidationService;

    private UUID memberUuid;
    private String nickname;
    private Role role;
    private UUID deviceId;
    private String accessToken;
    private String refreshToken;
    private Map<String,String> claims;
    private Date issuedAt;
    private Date expiredAt;

    @BeforeEach
    void setUp() {
        memberUuid = UUID.randomUUID();
        nickname = "testUser";
        role = Role.ROLE_USER;
        deviceId = UUID.fromString("378c0ca1-b67f-4ae7-a43b-e6cf583b7667");
        accessToken = "access-token";
        refreshToken = "refresh-token";
        claims = Map.of(
                "nickname", nickname,
                "role", role.getValue()
        );
        issuedAt = Date.from(Instant.now());
        expiredAt = Date.from(Instant.now().plusSeconds(3600));
    }

    @Test
    @DisplayName("토큰 생성 성공 테스트")
    void issueTokenSuccess() {
        // given
        willDoNothing().given(tokenValidationService).validateNotFoundMemberUuid(MEMBER_UUID, memberUuid);
        doNothing().when(tokenValidationService).validateExistedDeviceId(deviceId);

        given(tokenProvider.generateAccessToken(memberUuid, claims)).willReturn(accessToken);
        given(tokenProvider.generateRefreshToken(memberUuid)).willReturn(refreshToken);
        given(tokenProvider.getIssuedAtFromToken(refreshToken)).willReturn(issuedAt);
        given(tokenProvider.getExpirationFromToken(refreshToken)).willReturn(expiredAt);

        given(refreshTokenApplicationService.insert(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        TokenPair tokenPair = tokenApplicationService.issueToken(memberUuid, nickname, role, deviceId);

        // then
        assertNotNull(tokenPair);
        assertEquals(accessToken, tokenPair.getAccessToken());
        assertEquals(refreshToken, tokenPair.getRefreshToken());

        verify(tokenValidationService).validateNotFoundMemberUuid(MEMBER_UUID, memberUuid);
        verify(tokenValidationService).validateExistedDeviceId(deviceId);
        verify(tokenProvider).generateAccessToken(eq(memberUuid), anyMap());
        verify(tokenProvider).generateRefreshToken(memberUuid);
        verify(refreshTokenApplicationService).insert(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰 생성 실패 테스트 : device id 존재")
    void issueTokenThrowInvalidTokenWhenDeviceIdExists() {
        // given
        willDoNothing().given(tokenValidationService).validateNotFoundMemberUuid(MEMBER_UUID, memberUuid);
        doThrow(new InvalidTokenException("Device Id already exists"))
                .when(tokenValidationService).validateExistedDeviceId(deviceId);

        // then
        assertThrows(InvalidTokenException.class,
                () -> tokenApplicationService.issueToken(memberUuid, nickname, role, deviceId));
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    void reissueTokenSuccess() {
        // given
        SiteMemberResponse siteMemberResponse = mock(SiteMemberResponse.class);
        given(siteMemberResponse.nickname()).willReturn(nickname);
        String newAccessToken = "new-access-token";

        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        given(refreshTokenApplicationService.checkNotExistedRefreshToken(refreshToken)).willReturn(false);
        given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
        given(siteMemberService.getByUuid(memberUuid)).willReturn(Optional.of(siteMemberResponse));
        given(siteMemberRoleService.getByUuid(memberUuid)).willReturn(Optional.of(memberRoleUserResponse));
        given(tokenProvider.generateAccessToken(memberUuid, claims)).willReturn(newAccessToken);

        // when
        TokenPair result = tokenApplicationService.reissueToken(refreshToken);

        // then
        assertNotEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
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
        given(refreshTokenApplicationService.checkNotExistedRefreshToken(refreshToken)).willReturn(true);
        // then
        assertThrows(InvalidTokenException.class, () -> tokenApplicationService.reissueToken(refreshToken));
    }

    @Test
    @DisplayName("토큰 갱신 실패 테스트 : SiteMember 조회 불가")
    void reissueTokenFailWhenSiteMemberNotFound() {
        // given
        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        given(refreshTokenApplicationService.checkNotExistedRefreshToken(refreshToken)).willReturn(false);
        given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
        given(siteMemberService.getByUuid(memberUuid)).willReturn(Optional.empty());

        // then
        assertThrows(TokenNotFoundException.class, () -> tokenApplicationService.reissueToken(refreshToken));
    }

    @Test
    @DisplayName("토큰 갱신 실패 테스트 : SiteMemberRole 조회 불가")
    void reissueTokenFailWhenSiteMemberRoleNotFound() {
        // given
        SiteMemberResponse siteMemberResponse = mock(SiteMemberResponse.class);

        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        given(refreshTokenApplicationService.checkNotExistedRefreshToken(refreshToken)).willReturn(false);
        given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
        given(siteMemberService.getByUuid(memberUuid)).willReturn(Optional.of(siteMemberResponse));

        // then
        assertThrows(TokenNotFoundException.class, () -> tokenApplicationService.reissueToken(refreshToken));
    }

    @Test
    @DisplayName("토큰 삭제 성공 테스트")
    void removeTokenSuccess() {
        // given
        RefreshToken mockRefreshToken = RefreshToken.builder()
                .uuid(UUID.randomUUID())
                .deviceId(deviceId)
                .memberUuid(memberUuid)
                .refreshToken(refreshToken)
                .build();

        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        given(refreshTokenApplicationService.checkNotExistedRefreshToken(refreshToken)).willReturn(false);
        given(tokenProvider.getMemberUuidFromToken(refreshToken)).willReturn(memberUuid);
        given(refreshTokenApplicationService.getByRefreshToken(refreshToken)).willReturn(Optional.of(mockRefreshToken));
        given(refreshTokenApplicationService.getByMemberUuidAndDeviceId(memberUuid, deviceId)).willReturn(Optional.of(mockRefreshToken));
        willDoNothing().given(tokenValidationService).validateNotFoundTokenUuid(mockRefreshToken.getUuid());

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
        given(refreshTokenApplicationService.checkNotExistedRefreshToken(refreshToken)).willReturn(true);

        // when
        tokenApplicationService.removeToken(refreshToken);

        // then
        assertDoesNotThrow(() -> tokenApplicationService.removeToken(refreshToken));
        verify(refreshTokenApplicationService, never()).removeByUuid(any());
    }

}