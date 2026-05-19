package kr.modusplant.domains.member.adapter.translator;

import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkController;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.out.exception.UnsupportedSocialProviderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.*;
import static kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode.UNSUPPORTED_SOCIAL_PROVIDER;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberSocialTranslatorTest {
    private final SocialIdentityLinkController socialIdentityLinkController = Mockito.mock(SocialIdentityLinkController.class);
    private final MemberSocialTranslator memberSocialTranslator = new MemberSocialTranslator(socialIdentityLinkController);

    @Test
    @DisplayName("Kakao 소셜 제공자로 getSocialAccessToken으로 소셜 접근 토큰 반환")
    void testGetSocialAccessToken_withKakaoSocialProvider_willReturnAccessToken() {
        // given
        given(socialIdentityLinkController.issueSocialAccessToken(SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_CODE)).willReturn(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

        // when
        String socialAccessToken = memberSocialTranslator.getSocialAccessToken(TEST_SOCIAL_KAKAO_CODE, SocialProvider.KAKAO.getValue());

        // then
        assertThat(socialAccessToken).isEqualTo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("Google 소셜 제공자로 getSocialAccessToken으로 소셜 접근 토큰 반환")
    void testGetSocialAccessToken_withGoogleSocialProvider_willReturnAccessToken() {
        // given
        given(socialIdentityLinkController.issueSocialAccessToken(SocialProvider.GOOGLE, TEST_SOCIAL_GOOGLE_CODE)).willReturn(TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN);

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
    @DisplayName("Kakao 소셜 제공자로 deleteSocialAccountWithSocialAccessToken으로 소셜 계정 삭제")
    void testDeleteSocialAccountWithSocialAccessToken_withKakaoSocialProvider_willDeleteSocialAccount() {
        // given
        willDoNothing().given(socialIdentityLinkController).deleteSocialAccount(MEMBER_BASIC_USER_UUID, SocialProvider.KAKAO, TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

        // when
        memberSocialTranslator.deleteSocialAccountWithSocialAccessToken(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN, SocialProvider.KAKAO.getValue(), MEMBER_BASIC_USER_UUID);

        // then
        verify(socialIdentityLinkController, times(1)).deleteSocialAccount(any(), any(), any());
    }

    @Test
    @DisplayName("Google 소셜 제공자로 deleteSocialAccountWithSocialAccessToken으로 소셜 계정 삭제")
    void testDeleteSocialAccountWithSocialAccessToken_withGoogleSocialProvider_willDeleteSocialAccount() {
        // given
        willDoNothing().given(socialIdentityLinkController).deleteSocialAccount(MEMBER_BASIC_USER_UUID, SocialProvider.GOOGLE, TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN);

        // when
        memberSocialTranslator.deleteSocialAccountWithSocialAccessToken(TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN, SocialProvider.GOOGLE.getValue(), MEMBER_BASIC_USER_UUID);

        // then
        verify(socialIdentityLinkController, times(1)).deleteSocialAccount(any(), any(), any());
    }

    @Test
    @DisplayName("지원되지 않는 소셜 제공자를 통해 deleteSocialAccountWithSocialAccessToken으로 예외 발생")
    void testDeleteSocialAccountWithSocialAccessToken_withNotSupportedSocialProvider_willThrowException() {
        // given when
        UnsupportedSocialProviderException unsupportedSocialProviderException = assertThrows(UnsupportedSocialProviderException.class, () ->
                memberSocialTranslator.deleteSocialAccountWithSocialAccessToken(TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN, "notSupportedProvider", MEMBER_BASIC_USER_UUID));

        // then
        assertThat(unsupportedSocialProviderException.getErrorCode()).isEqualTo(UNSUPPORTED_SOCIAL_PROVIDER);
    }
}