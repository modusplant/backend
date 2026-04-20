package kr.modusplant.domains.account.social.adapter.controller;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.common.util.domain.vo.AgreedTermsTestUtils;
import kr.modusplant.domains.account.social.common.util.domain.vo.SocialMemberProfileTestUtils;
import kr.modusplant.domains.account.social.common.util.domain.vo.SocialProfileTestUtils;
import kr.modusplant.domains.account.social.common.util.usecase.record.TempTokenInfoTestUtils;
import kr.modusplant.domains.account.social.common.util.usecase.request.SocialAuthRequestTestUtils;
import kr.modusplant.domains.account.social.common.util.usecase.request.SocialSignUpRequestTestUtils;
import kr.modusplant.domains.account.social.common.util.usecase.response.SocialLoginResultTestUtils;
import kr.modusplant.domains.account.social.domain.exception.AlreadyRegisteredWithOtherProviderException;
import kr.modusplant.domains.account.social.domain.exception.InvalidValueException;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClient;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClientFactory;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.domains.account.social.usecase.record.TempTokenInfo;
import kr.modusplant.domains.account.social.usecase.request.SocialSignUpRequest;
import kr.modusplant.domains.account.social.usecase.response.LoginResult;
import kr.modusplant.domains.account.social.usecase.response.NeedLinkResult;
import kr.modusplant.domains.account.social.usecase.response.NeedSignupResult;
import kr.modusplant.domains.account.social.usecase.response.SocialLoginResult;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class SocialIdentityControllerTest implements SocialAuthRequestTestUtils, SocialSignUpRequestTestUtils, SocialMemberProfileTestUtils, SocialProfileTestUtils,
        AgreedTermsTestUtils, SocialLoginResultTestUtils, TempTokenInfoTestUtils {
    private final SocialAuthClientFactory clientFactory = mock(SocialAuthClientFactory.class);
    private final SocialIdentityRepository socialIdentityRepository = mock(SocialIdentityRepository.class);
    private final SocialIdentityMapper socialIdentityMapper = mock(SocialIdentityMapper.class);
    private final SocialIdentityController socialIdentityController = new SocialIdentityController(clientFactory,socialIdentityRepository,socialIdentityMapper);


    @Test
    @DisplayName("소셜 접근 토큰 발급받기")
    void testIssueSocialAccessToken_givenSocialProviderAndCode_willReturnSocialAccessToken() {
        // given
        String code = createTestKakaoLoginRequest().code();
        String socialAccessToken = "access-token";
        SocialAuthClient authClient = mock(SocialAuthClient.class);

        given(clientFactory.getClient(SocialProvider.KAKAO)).willReturn(authClient);
        given(authClient.getAccessToken(code)).willReturn(socialAccessToken);

        // when
        String result = socialIdentityController.issueSocialAccessToken(SocialProvider.KAKAO,code);

        // then
        assertEquals(result,socialAccessToken);
        verify(clientFactory).getClient(SocialProvider.KAKAO);
        verify(authClient).getAccessToken(code);
    }

    @Test
    @DisplayName("최초 회원 로그인시 NeedSignupResult 반환하기")
    void testHandleSocialLogin_givenSocialProviderAndSocialAccessToken_willReturnSocialLoginResult() {
        // given
        String socialAccessToken = "access-token";
        SocialAuthClient authClient = mock(SocialAuthClient.class);
        SocialUserInfo userInfo = mock(SocialUserInfo.class);
        given(clientFactory.getClient(SocialProvider.KAKAO)).willReturn(authClient);
        given(authClient.getUserInfo(socialAccessToken)).willReturn(userInfo);
        given(socialIdentityMapper.toSocialProfile(SocialProvider.KAKAO,userInfo)).willReturn(testKakaoSocialProfile);
        given(socialIdentityRepository.getSocialMemberProfileByEmail(testKakaoSocialProfile.getEmail())).willReturn(Optional.empty());

        // when
        SocialLoginResult result = socialIdentityController.handleSocialLogin(SocialProvider.KAKAO,socialAccessToken);

        // then
        assertEquals(result,createKakaoNeedSignupResult());
        assertThat(result).isInstanceOf(NeedSignupResult.class);
        verify(clientFactory).getClient(SocialProvider.KAKAO);
        verify(authClient).getUserInfo(socialAccessToken);
        verify(socialIdentityMapper).toSocialProfile(SocialProvider.KAKAO,userInfo);
        verify(socialIdentityRepository).getSocialMemberProfileByEmail(testKakaoSocialProfile.getEmail());
    }

    @Test
    @DisplayName("기존 회원이고 최초 소셜 로그인 시 NeedLinkResult 반환하기")
    void testClassifyMember_givenNewSocialMemberSocialProfile_willReturnNeedLinkResult() {
        // given
        given(socialIdentityRepository.getSocialMemberProfileByEmail(testKakaoSocialProfileWithBasicEmail.getEmail()))
                .willReturn(Optional.of(testBasicSocialMemberProfile));

        // when
        SocialLoginResult result = socialIdentityController.classifyMember(testKakaoSocialProfileWithBasicEmail);

        // then
        assertEquals(result,createKakaoNeedLinkResult());
        assertThat(result).isInstanceOf(NeedLinkResult.class);
        verify(socialIdentityRepository).getSocialMemberProfileByEmail(testKakaoSocialProfileWithBasicEmail.getEmail());
    }

    @Test
    @DisplayName("기존 다른 소셜 회원일 경우 예외 처리하기")
    void testClassifyMember_givenSocialProfileWithOtherProvider_willThrowException() {
        // given
        given(socialIdentityRepository.getSocialMemberProfileByEmail(testKakaoSocialProfileWithBasicEmail.getEmail()))
                .willReturn(Optional.of(testBasicGoogleSocialMemberProfile));

        // when & then
        assertThatThrownBy(() -> socialIdentityController.classifyMember(testKakaoSocialProfileWithBasicEmail))
                .isInstanceOf(AlreadyRegisteredWithOtherProviderException.class);
        verify(socialIdentityRepository).getSocialMemberProfileByEmail(testKakaoSocialProfileWithBasicEmail.getEmail());
    }

    @Test
    @DisplayName("기존 소셜 회원일 경우 로그인 완료하기")
    void testClassifyMember_givenExistingSocialMemberSocialMemberProfile_willReturnLoginResult() {
        // given
        given(socialIdentityRepository.getSocialMemberProfileByEmail(testGoogleSocialProfile.getEmail()))
                .willReturn(Optional.of(testGoogleSocialMemberProfile));
        given(socialIdentityRepository.updateLoggedInAtAndGetProfile(testGoogleSocialMemberProfile.getAccountId())).willReturn(testGoogleSocialMemberProfile);
        given(socialIdentityMapper.toLoginResult(testGoogleSocialMemberProfile)).willReturn(createGoogleLoginResult());

        // when
        SocialLoginResult result = socialIdentityController.classifyMember(testGoogleSocialProfile);

        // then
        assertEquals(result,createGoogleLoginResult());
        assertThat(result).isInstanceOf(LoginResult.class);
        verify(socialIdentityRepository).getSocialMemberProfileByEmail(testGoogleSocialProfile.getEmail());
        verify(socialIdentityRepository).updateLoggedInAtAndGetProfile(testGoogleSocialMemberProfile.getAccountId());
        verify(socialIdentityMapper).toLoginResult(testGoogleSocialMemberProfile);
    }

    @Test
    @DisplayName("providerid가 provider의 값이 아닌 경우 예외 발생")
    void testClassifyMember_givenInvalidProviderId_willThrowException() {
        // given
        SocialMemberProfile differentProviderIdProfile = SocialMemberProfile.create(
                AccountId.fromUuid(UUID.randomUUID()),
                SocialCredentials.createKakao("9999999999"),  // 다른 providerId
                Email.create("test@kakao.com"),
                Nickname.create("테스트"),
                Role.USER
        );
        given(socialIdentityRepository.getSocialMemberProfileByEmail(any())).willReturn(Optional.of(differentProviderIdProfile));

        // when & then
        assertThatThrownBy(() -> socialIdentityController.classifyMember(testKakaoSocialProfile)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    @DisplayName("SocialSignupRequest와 TempToken이 주어졌을 때 회원가입 진행")
    void testCreateNewMember_givenSocialSignupRequestAndTempTokenInfo_willReturnLoginResult() {
        // given
        SocialSignUpRequest signUpRequest = createTestSocialSignUpRequest();
        TempTokenInfo tempTokenInfo = createGoogleTempTokenInfoWithBasicEmail();
        SocialMemberProfile socialMemberProfile = SocialMemberProfile.createNewMember(
                SocialCredentials.createGoogle(tempTokenInfo.providerId()),
                Email.create(tempTokenInfo.email()),Nickname.create(signUpRequest.nickname()),Role.USER);
        SocialMemberProfile newSocialMemberProfile = testGoogleSocialMemberProfileWithBasicEmail;
        LoginResult loginResult = createBasicSocialLoginResult();
        given(socialIdentityRepository.getSocialMemberProfileByEmail(Email.create(tempTokenInfo.email()))).willReturn(Optional.empty());
        given(socialIdentityMapper.toSocialAuthProvider(tempTokenInfo.socialProvider())).willReturn(AuthProvider.GOOGLE);
        given(socialIdentityRepository.saveSocialMember(socialMemberProfile,signUpRequest.introduction(),testAgreedTerms)).willReturn(newSocialMemberProfile);
        given(socialIdentityMapper.toLoginResult(newSocialMemberProfile)).willReturn(loginResult);

        // when
        LoginResult result = socialIdentityController.createNewMember(signUpRequest,tempTokenInfo);

        // then
        assertThat(result).isNotNull();
        assertEquals(tempTokenInfo.email(),result.email());
        assertEquals(signUpRequest.nickname(),result.nickname());
    }

    @Test
    @DisplayName("이미 존재하는 회원이라면 예외 발생")
    void testCreateNewMember_givenExistingMember_willThrowException() {
        // given
        given(socialIdentityRepository.getSocialMemberProfileByEmail(any())).willReturn(Optional.of(testGoogleSocialMemberProfile));

        // when & then
        assertThatThrownBy(() -> socialIdentityController.createNewMember(createTestSocialSignUpRequest(), createGoogleTempTokenInfo()))
                .isInstanceOf(SocialAccountConflictException.class);
    }

    @Test
    @DisplayName("TempToken 정보가 주어졌을 때 연동 진행")
    void testLinkBasicSocialMember_givenTempTokenInfo_willReturnLoginResult() {
        AuthProvider linkedAuthProvider = AuthProvider.BASIC_KAKAO;
        TempTokenInfo tokenInfo = createKakaoTempTokenInfoWithBasicEmail();
        SocialMemberProfile memberProfile = testBasicKakaoSocialMemberProfile;
        LoginResult loginResult = createBasicSocialLoginResult();
        given(socialIdentityMapper.toLinkedAuthProvider(tokenInfo.socialProvider())).willReturn(linkedAuthProvider);
        given(socialIdentityRepository.updateSocialLinkedMember(memberProfile.getSocialCredentials(),memberProfile.getEmail())).willReturn(memberProfile);
        given(socialIdentityMapper.toLoginResult(memberProfile)).willReturn(loginResult);

        // when
        LoginResult result = socialIdentityController.linkBasicSocialMember(tokenInfo);

        // then
        assertThat(result).isNotNull();
        assertEquals(result.email(),tokenInfo.email());
        assertEquals(result.nickname(),memberProfile.getNickname().getValue());
    }

    @Test
    @DisplayName("SocialProvider와 소셜 인증 토큰이 주어졌을 때 소셜 연결 해제")
    void testUnlinkSocialAccount_givenSocialProviderAndSocialAccessToken_willUnlinkSocialAccount() {
        // given
        String socialAccessToken = "access-token";
        SocialAuthClient authClient = mock(SocialAuthClient.class);
        given(clientFactory.getClient(SocialProvider.KAKAO)).willReturn(authClient);

        // when
        socialIdentityController.unlinkSocialAccount(SocialProvider.KAKAO,socialAccessToken);

        // then
        verify(clientFactory).getClient(SocialProvider.KAKAO);
        verify(authClient).revokeAccess(socialAccessToken);
    }


}