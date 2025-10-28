package kr.modusplant.domains.identity.social.adapter.controller;

import kr.modusplant.domains.identity.social.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.identity.social.common.util.domain.vo.SocialUserProfileTestUtils;
import kr.modusplant.domains.identity.social.common.util.domain.vo.UserPayloadTestUtils;
import kr.modusplant.domains.identity.social.common.util.usecase.request.SocialLoginRequestTestUtils;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.usecase.port.client.SocialAuthClient;
import kr.modusplant.domains.identity.social.usecase.port.client.SocialAuthClientFactory;
import kr.modusplant.domains.identity.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.identity.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.identity.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class SocialIdentityControllerTest implements SocialLoginRequestTestUtils, MemberIdTestUtils, SocialUserProfileTestUtils, UserPayloadTestUtils {
    private final SocialAuthClientFactory clientFactory = mock(SocialAuthClientFactory.class);
    private final SocialIdentityRepository socialIdentityRepository = mock(SocialIdentityRepository.class);
    private final SocialIdentityMapper socialIdentityMapper = mock(SocialIdentityMapper.class);
    private final SocialIdentityController socialIdentityController = new SocialIdentityController(clientFactory,socialIdentityRepository,socialIdentityMapper);

    @Test
    @DisplayName("소셜 로그인 처리")
    void testHandleSocialLogin_givenProviderAndCode_willReturnUserPayload() {
        // given
        String code = createTestKakaoLoginRequest().getCode();
        String accessToken = "access-token";
        SocialAuthClient authClient = mock(SocialAuthClient.class);
        SocialUserInfo userInfo = mock(SocialUserInfo.class);

        given(clientFactory.getClient(AuthProvider.KAKAO)).willReturn(authClient);
        given(authClient.getAccessToken(code)).willReturn(accessToken);
        given(authClient.getUserInfo(accessToken)).willReturn(userInfo);
        given(socialIdentityMapper.toSocialUserProfile(AuthProvider.KAKAO, userInfo)).willReturn(testKakaoSocialUserProfile);
        given(socialIdentityRepository.getMemberIdBySocialCredentials(testKakaoSocialCredentials)).willReturn(Optional.of(testSocialKakaoMemberId));
        given(socialIdentityRepository.getUserPayloadByMemberId(testSocialKakaoMemberId)).willReturn(testSocialKakaoUserPayload);

        // when
        UserPayload result = socialIdentityController.handleSocialLogin(AuthProvider.KAKAO, code);

        // then
        assertNotNull(result);
        assertEquals(testSocialKakaoUserPayload, result);
        verify(clientFactory, times(2)).getClient(AuthProvider.KAKAO);
        verify(authClient).getAccessToken(code);
        verify(authClient).getUserInfo(accessToken);
        verify(socialIdentityMapper).toSocialUserProfile(AuthProvider.KAKAO, userInfo);
        verify(socialIdentityRepository).updateLoggedInAt(testSocialKakaoMemberId);
    }

    @Test
    @DisplayName("기존 회원이 존재하면 해당 회원의 UserPayload를 반환한다")
    void testFindOrCreateMember_givenExistingMember_willReturnUserPayload() {
        // given
        given(socialIdentityRepository.getMemberIdBySocialCredentials(testKakaoSocialCredentials)).willReturn(Optional.of(testSocialKakaoMemberId));
        given(socialIdentityRepository.getUserPayloadByMemberId(testSocialKakaoMemberId)).willReturn(testSocialKakaoUserPayload);

        // when
        UserPayload result = socialIdentityController.findOrCreateMember(testKakaoSocialUserProfile);

        // then
        assertNotNull(result);
        assertEquals(testSocialKakaoUserPayload, result);
        verify(socialIdentityRepository).getMemberIdBySocialCredentials(testKakaoSocialCredentials);
        verify(socialIdentityRepository).updateLoggedInAt(testSocialKakaoMemberId);
        verify(socialIdentityRepository).getUserPayloadByMemberId(testSocialKakaoMemberId);
        verify(socialIdentityRepository, never()).createSocialMember(any(), any());
    }

    @Test
    @DisplayName("신규 회원이면 회원을 생성하고 UserPayload를 반환한다")
    void testFindOrCreateMember_givenNewMember_willCreateAndReturnUserPayload() {
        // given
        given(socialIdentityRepository.getMemberIdBySocialCredentials(testKakaoSocialCredentials)).willReturn(Optional.empty());
        given(socialIdentityRepository.createSocialMember(testKakaoSocialUserProfile, Role.USER)).willReturn(testSocialKakaoUserPayload);

        // when
        UserPayload result = socialIdentityController.findOrCreateMember(testKakaoSocialUserProfile);

        // then
        assertNotNull(result);
        assertEquals(testSocialKakaoUserPayload, result);
        verify(socialIdentityRepository).getMemberIdBySocialCredentials(testKakaoSocialCredentials);
        verify(socialIdentityRepository).createSocialMember(testKakaoSocialUserProfile, Role.USER);
        verify(socialIdentityRepository, never()).updateLoggedInAt(any());
        verify(socialIdentityRepository, never()).getUserPayloadByMemberId(any());
    }

}