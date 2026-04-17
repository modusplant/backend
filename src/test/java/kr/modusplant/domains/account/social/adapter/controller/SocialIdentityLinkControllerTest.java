package kr.modusplant.domains.account.social.adapter.controller;

import kr.modusplant.domains.account.social.common.util.domain.vo.SocialMemberProfileTestUtils;
import kr.modusplant.domains.account.social.domain.exception.AlreadyRegisteredWithOtherProviderException;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
import kr.modusplant.domains.account.social.domain.exception.SocialActionRequiredException;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClient;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClientFactory;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SocialIdentityLinkControllerTest implements SocialMemberProfileTestUtils {
    private final SocialAuthClientFactory clientFactory = mock(SocialAuthClientFactory.class);
    private final SocialIdentityRepository socialIdentityRepository = mock(SocialIdentityRepository.class);
    private final SocialIdentityMapper socialIdentityMapper = mock(SocialIdentityMapper.class);
    private final SocialIdentityLinkController socialIdentityLinkController = new SocialIdentityLinkController(
            clientFactory, socialIdentityRepository, socialIdentityMapper
    );

    private SocialAuthClient socialAuthClient;
    private SocialUserInfo kakaoUserInfo;

    @BeforeEach
    void setUp() {
        socialAuthClient = mock(SocialAuthClient.class);
        kakaoUserInfo = mock(SocialUserInfo.class);
        given(clientFactory.getClient(any())).willReturn(socialAuthClient);
        given(kakaoUserInfo.getEmail()).willReturn(testNormalUserEmail.getValue());
        given(kakaoUserInfo.getId()).willReturn(testKakaoSocialCredentials.getProviderId());
    }

    @Nested
    @DisplayName("소셜 계정 연동 테스트")
    class LinkSocialAccountTests {
        @Test
        @DisplayName("일반 회원이 카카오 소셜 계정을 연동한다")
        void testLinkSocialAccount_givenBasicMemberAndKakao_willLinkSuccessfully() {
            // given
            given(socialAuthClient.getUserInfo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN)).willReturn(kakaoUserInfo);
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicSocialMemberProfile);
            given(socialIdentityMapper.toLinkedAuthProvider(SocialProvider.KAKAO)).willReturn(AuthProvider.BASIC_KAKAO);

            // when
            socialIdentityLinkController.linkSocialAccount(testNormalMemberId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

            // then
            verify(socialIdentityRepository).updateSocialLinkedMember(testBasicKakaoSocialCredentials, testNormalUserEmail);
        }

        @Test
        @DisplayName("이메일 불일치 시 연동 실패 예외가 발생한다")
        void testLinkSocialAccount_givenEmailMismatch_willThrowException() {
            // given
            SocialUserInfo mismatchUserInfo = mock(SocialUserInfo.class);
            given(mismatchUserInfo.getEmail()).willReturn("different@email.com");
            given(socialAuthClient.getUserInfo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN)).willReturn(mismatchUserInfo);
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> socialIdentityLinkController.linkSocialAccount(
                    testNormalMemberId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN)
            ).isInstanceOf(SocialAccountConflictException.class);
        }

        @Test
        @DisplayName("이미 연동된 계정으로 연동 시도 시 예외가 발생한다")
        void testLinkSocialAccount_givenAlreadyLinkedMember_willThrowException() {
            // given
            given(socialAuthClient.getUserInfo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN)).willReturn(kakaoUserInfo);
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicKakaoSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> socialIdentityLinkController.linkSocialAccount(
                    testNormalMemberId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN
            )).isInstanceOf(SocialAccountConflictException.class);
        }

        @Test
        @DisplayName("소셜 전용 카카오 계정으로 연동 시도 시 예외가 발생한다")
        void testLinkSocialAccount_givenPureKakaoSocialMember_willThrowException() {
            // given
            given(socialAuthClient.getUserInfo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN)).willReturn(kakaoUserInfo);
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testKakaoAccountId)).willReturn(testKakaoSocialMemberProfileWithBasicEmail);

            // when & then
            assertThatThrownBy(() -> socialIdentityLinkController.linkSocialAccount(
                    testKakaoAccountId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN
            )).isInstanceOf(AlreadyRegisteredWithOtherProviderException.class);
        }

    }

    @Nested
    @DisplayName("소셜 계정 연동 해제 테스트")
    class UnlinkSocialAccountTests {
        @Test
        @DisplayName("연동된 카카오 계정을 연동 해제한다")
        void testUnlinkSocialAccount_givenLinkedKakaoMember_willUnlinkSuccessfully() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicKakaoSocialMemberProfile);

            // when
            socialIdentityLinkController.unlinkSocialAccount(testNormalMemberId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

            // then
            verify(socialAuthClient).revokeAccess(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
            verify(socialIdentityRepository).updateSocialUnlinkedMember(testNormalMemberId);
        }

        @Test
        @DisplayName("일반 계정의 연동 해제 시도 시 예외가 발생한다")
        void testUnlinkSocialAccount_givenBasicMember_willThrowException() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> socialIdentityLinkController.unlinkSocialAccount(
                    testNormalMemberId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN
            )).isInstanceOf(SocialAccountConflictException.class);
        }

        @Test
        @DisplayName("소셜 전용 계정의 연동 해제 시도 시 탈퇴 필요 예외가 발생한다")
        void testUnlinkSocialAccount_givenPureSocialMember_willThrowSocialWithdrawalRequiredException() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testKakaoAccountId)).willReturn(testKakaoSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> socialIdentityLinkController.unlinkSocialAccount(
                    testKakaoAccountId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN
            )).isInstanceOf(SocialActionRequiredException.class);
        }

        @Test
        @DisplayName("provider 불일치 시 연동 해제 예외가 발생한다")
        void testUnlinkSocialAccount_givenProviderMismatch_willThrowException() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicKakaoSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> socialIdentityLinkController.unlinkSocialAccount(
                    testNormalMemberId.getValue(), SocialProvider.GOOGLE, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN
            )).isInstanceOf(SocialAccountConflictException.class);
        }

    }

    @Nested
    @DisplayName("소셜 계정 삭제 테스트")
    class DeleteSocialAccountTests {
        @Test
        @DisplayName("소셜 전용 카카오 회원이 탈퇴한다")
        void testDeleteSocialAccount_givenPureKakaoMember_willDeleteSuccessfully() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testKakaoAccountId)).willReturn(testKakaoSocialMemberProfile);
            // todo: deleteSocialMember 부분 추가

            // when
            socialIdentityLinkController.deleteSocialAccount(testKakaoAccountId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

            // then
            verify(socialAuthClient).revokeAccess(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("일반 회원이 소셜 탈퇴 시도 시 예외가 발생한다")
        void testDeleteSocialAccount_givenBasicMember_willThrowException() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> socialIdentityLinkController.deleteSocialAccount(
                    testNormalMemberId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN
            )).isInstanceOf(SocialAccountConflictException.class);
        }

        @Test
        @DisplayName("연동 계정이 탈퇴 시도 시 연동 해제 필요 예외가 발생한다")
        void testDeleteSocialAccount_givenLinkedMember_willThrowSocialLinkageRequiredException() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicKakaoSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> socialIdentityLinkController.deleteSocialAccount(
                    testNormalMemberId.getValue(), SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN
            )).isInstanceOf(SocialActionRequiredException.class);
        }

        @Test
        @DisplayName("provider 불일치 시 탈퇴 예외가 발생한다")
        void testDeleteSocialAccount_givenProviderMismatch_willThrowException() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testKakaoAccountId)).willReturn(testKakaoSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> socialIdentityLinkController.deleteSocialAccount(
                    testKakaoAccountId.getValue(), SocialProvider.GOOGLE, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN
            )).isInstanceOf(SocialAccountConflictException.class);
        }

    }



}