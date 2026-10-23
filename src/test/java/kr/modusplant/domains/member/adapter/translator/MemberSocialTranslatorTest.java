package kr.modusplant.domains.member.adapter.translator;

import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkController;
import kr.modusplant.domains.account.social.common.util.domain.vo.SocialMemberProfileTestUtils;
import kr.modusplant.domains.account.social.domain.exception.SocialAccountConflictException;
import kr.modusplant.domains.account.social.domain.exception.SocialActionRequiredException;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.outbound.exception.UnsupportedSocialProviderException;
import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.domains.account.social.usecase.record.SocialUserInfo;
import kr.modusplant.shared.framework.jdbc.common.util.SwearHolderTestUtils;
import kr.modusplant.shared.framework.jdbc.holder.SwearHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.*;
import static kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberSocialTranslatorTest implements SocialMemberProfileTestUtils {
    @SuppressWarnings("unused")
    private final SwearHolder swearHolder = SwearHolderTestUtils.createSwearHolder();

    private final SocialIdentityLinkController socialIdentityLinkController = Mockito.mock(SocialIdentityLinkController.class);
    private final SocialIdentityRepository socialIdentityRepository = Mockito.mock(SocialIdentityRepository.class);
    private final Environment environment = Mockito.mock(Environment.class);
    private final MemberSocialTranslator memberSocialTranslator = new MemberSocialTranslator(socialIdentityLinkController, socialIdentityRepository, environment);

    @Test
    @DisplayName("Kakao 소셜 제공자로 getSocialAccessToken으로 소셜 접근 토큰 반환")
    void testGetSocialAccessToken_withKakaoSocialProvider_willReturnAccessToken() {
        // given
        SocialUserInfo userInfo = Mockito.mock(SocialUserInfo.class);
        given(socialIdentityLinkController.issueSocialToken(SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_CODE, false)).willReturn(userInfo);
        given(userInfo.socialAccessToken()).willReturn(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

        // when
        String socialAccessToken = memberSocialTranslator.getSocialAccessToken(TEST_SOCIAL_KAKAO_CODE, SocialProvider.KAKAO.getValue());

        // then
        assertThat(socialAccessToken).isEqualTo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("Google 소셜 제공자로 getSocialAccessToken으로 소셜 접근 토큰 반환")
    void testGetSocialAccessToken_withGoogleSocialProvider_willReturnAccessToken() {
        // given
        SocialUserInfo userInfo = Mockito.mock(SocialUserInfo.class);
        given(socialIdentityLinkController.issueSocialToken(SocialProvider.GOOGLE, TEST_SOCIAL_GOOGLE_CODE, false)).willReturn(userInfo);
        given(userInfo.socialAccessToken()).willReturn(TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN);

        // when
        String socialAccessToken = memberSocialTranslator.getSocialAccessToken(TEST_SOCIAL_GOOGLE_CODE, SocialProvider.GOOGLE.getValue());

        // then
        assertThat(socialAccessToken).isEqualTo(TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("지원되지 않는 소셜 제공자를 통해 getSocialAccessToken으로 예외 발생")
    void testGetSocialAccessToken_withNotSupportedSocialProvider_willThrowException() {
        // given when
        UnsupportedSocialProviderException unsupportedSocialProviderException = assertThrows(UnsupportedSocialProviderException.class, () ->
                memberSocialTranslator.getSocialAccessToken(TEST_SOCIAL_GOOGLE_CODE, "notSupportedProvider"));

        // then
        assertThat(unsupportedSocialProviderException.getErrorCode()).isEqualTo(UNSUPPORTED_SOCIAL_PROVIDER);
    }

    @Test
    @DisplayName("Kakao 소셜 전용 회원으로 validateIfSocialAccountDeletable으로 활동 수행")
    void testValidateIfSocialAccountDeletable_givenPureKakaoMember_willProcessAction() {
        // given
        given(socialIdentityRepository.getSocialMemberProfileByAccountId(testKakaoAccountId)).willReturn(testKakaoSocialMemberProfile);

        // when & then
        assertDoesNotThrow(() -> memberSocialTranslator.validateIfSocialAccountDeletable(SocialProvider.KAKAO.getValue(), testKakaoAccountId.getValue()));
        verify(socialIdentityRepository, times(1)).getSocialMemberProfileByAccountId(testKakaoAccountId);
    }

    @Test
    @DisplayName("Google 소셜 전용 회원으로 validateIfSocialAccountDeletable으로 활동 수행")
    void testValidateIfSocialAccountDeletable_givenPureGoogleMember_willProcessAction() {
        // given
        given(socialIdentityRepository.getSocialMemberProfileByAccountId(testGoogleAccountId)).willReturn(testGoogleSocialMemberProfile);

        // when & then
        assertDoesNotThrow(() -> memberSocialTranslator.validateIfSocialAccountDeletable(SocialProvider.GOOGLE.getValue(), testGoogleAccountId.getValue()));
        verify(socialIdentityRepository, times(1)).getSocialMemberProfileByAccountId(testGoogleAccountId);
    }

    @Test
    @DisplayName("일반 회원으로 validateIfSocialAccountDeletable으로 예외 발생")
    void testValidateIfSocialAccountDeletable_givenBasicMember_willThrowException() {
        // given
        given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicSocialMemberProfile);

        // when
        SocialAccountConflictException socialAccountConflictException = assertThrows(SocialAccountConflictException.class, () ->
                memberSocialTranslator.validateIfSocialAccountDeletable(SocialProvider.KAKAO.getValue(), testNormalMemberId.getValue()));

        // then
        assertThat(socialAccountConflictException.getErrorCode()).isEqualTo(NORMAL_USER_CANNOT_WITHDRAW);
    }

    @Test
    @DisplayName("연동 회원으로 validateIfSocialAccountDeletable으로 예외 발생")
    void testValidateIfSocialAccountDeletable_givenLinkedMember_willThrowException() {
        // given
        given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicKakaoSocialMemberProfile);

        // when
        SocialActionRequiredException socialActionRequiredException = assertThrows(SocialActionRequiredException.class, () ->
                memberSocialTranslator.validateIfSocialAccountDeletable(SocialProvider.KAKAO.getValue(), testNormalMemberId.getValue()));

        // then
        assertThat(socialActionRequiredException.getErrorCode()).isEqualTo(SOCIAL_LINKAGE_REQUIRED);
    }

    @Test
    @DisplayName("소셜 제공자 불일치로 validateIfSocialAccountDeletable으로 예외 발생")
    void testValidateIfSocialAccountDeletable_givenProviderMismatch_willThrowException() {
        // given
        given(socialIdentityRepository.getSocialMemberProfileByAccountId(testKakaoAccountId)).willReturn(testKakaoSocialMemberProfile);

        // when
        SocialAccountConflictException socialAccountConflictException = assertThrows(SocialAccountConflictException.class, () ->
                memberSocialTranslator.validateIfSocialAccountDeletable(SocialProvider.GOOGLE.getValue(), testKakaoAccountId.getValue()));

        // then
        assertThat(socialAccountConflictException.getErrorCode()).isEqualTo(PROVIDER_MISMATCH);
    }

    @Test
    @DisplayName("지원되지 않는 소셜 제공자를 통해 validateIfSocialAccountDeletable으로 예외 발생")
    void testValidateIfSocialAccountDeletable_withNotSupportedSocialProvider_willThrowException() {
        // given when
        UnsupportedSocialProviderException unsupportedSocialProviderException = assertThrows(UnsupportedSocialProviderException.class, () ->
                memberSocialTranslator.validateIfSocialAccountDeletable("notSupportedProvider", testKakaoAccountId.getValue()));

        // then
        assertThat(unsupportedSocialProviderException.getErrorCode()).isEqualTo(UNSUPPORTED_SOCIAL_PROVIDER);
    }

    @Test
    @DisplayName("local 프로필일 때 initIsLocal으로 활동 수행")
    void testInitIsLocal_givenLocalProfile_willProcessAction() {
        // given
        given(environment.acceptsProfiles(any(Profiles.class))).willReturn(true);
        SocialUserInfo userInfo = Mockito.mock(SocialUserInfo.class);
        given(socialIdentityLinkController.issueSocialToken(SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_CODE, true)).willReturn(userInfo);
        given(userInfo.socialAccessToken()).willReturn(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

        // when
        memberSocialTranslator.initIsLocal();
        String socialAccessToken = memberSocialTranslator.getSocialAccessToken(TEST_SOCIAL_KAKAO_CODE, SocialProvider.KAKAO.getValue());

        // then
        assertThat(socialAccessToken).isEqualTo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("local이 아닌 프로필일 때 initIsLocal으로 활동 수행")
    void testInitIsLocal_givenNotLocalProfile_willProcessAction() {
        // given
        given(environment.acceptsProfiles(any(Profiles.class))).willReturn(false);
        SocialUserInfo userInfo = Mockito.mock(SocialUserInfo.class);
        given(socialIdentityLinkController.issueSocialToken(SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_CODE, false)).willReturn(userInfo);
        given(userInfo.socialAccessToken()).willReturn(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

        // when
        memberSocialTranslator.initIsLocal();
        String socialAccessToken = memberSocialTranslator.getSocialAccessToken(TEST_SOCIAL_KAKAO_CODE, SocialProvider.KAKAO.getValue());

        // then
        assertThat(socialAccessToken).isEqualTo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
    }
}