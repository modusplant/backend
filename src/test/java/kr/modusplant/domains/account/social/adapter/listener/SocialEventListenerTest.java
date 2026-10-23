package kr.modusplant.domains.account.social.adapter.listener;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.domain.event.SocialAccessRevokeEvent;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClient;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClientFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SocialEventListenerTest {
    private final SocialAuthClientFactory clientFactory = Mockito.mock(SocialAuthClientFactory.class);
    private final SocialAuthClient socialAuthClient = Mockito.mock(SocialAuthClient.class);
    private final SocialEventListener socialEventListener = new SocialEventListener(clientFactory);

    @Test
    @DisplayName("Kakao 소셜 접근 권한 해제 이벤트로 handleSocialAccessRevoke으로 활동 수행")
    void testHandleSocialAccessRevoke_givenKakaoEvent_willRevokeSocialAccess() {
        // given
        SocialAccessRevokeEvent event = SocialAccessRevokeEvent.create(
                MEMBER_BASIC_USER_UUID, SocialProvider.KAKAO.getValue(), TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        given(clientFactory.getClient(SocialProvider.KAKAO)).willReturn(socialAuthClient);
        willDoNothing().given(socialAuthClient).revokeAccess(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

        // when
        socialEventListener.handleSocialAccessRevoke(event);

        // then
        verify(socialAuthClient, times(1)).revokeAccess(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("Google 소셜 접근 권한 해제 이벤트로 handleSocialAccessRevoke으로 활동 수행")
    void testHandleSocialAccessRevoke_givenGoogleEvent_willRevokeSocialAccess() {
        // given
        SocialAccessRevokeEvent event = SocialAccessRevokeEvent.create(
                MEMBER_BASIC_USER_UUID, SocialProvider.GOOGLE.getValue(), TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN);
        given(clientFactory.getClient(SocialProvider.GOOGLE)).willReturn(socialAuthClient);
        willDoNothing().given(socialAuthClient).revokeAccess(TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN);

        // when
        socialEventListener.handleSocialAccessRevoke(event);

        // then
        verify(socialAuthClient, times(1)).revokeAccess(TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("소셜 접근 권한 해제 실패 시 handleSocialAccessRevoke으로 활동 수행")
    void testHandleSocialAccessRevoke_givenRevokeFailure_willLogError() {
        // given
        SocialAccessRevokeEvent event = SocialAccessRevokeEvent.create(
                MEMBER_BASIC_USER_UUID, SocialProvider.KAKAO.getValue(), TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        given(clientFactory.getClient(SocialProvider.KAKAO)).willReturn(socialAuthClient);
        willThrow(new RuntimeException()).given(socialAuthClient).revokeAccess(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

        // when & then
        assertDoesNotThrow(() -> socialEventListener.handleSocialAccessRevoke(event));
        verify(socialAuthClient, times(1)).revokeAccess(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
    }
}
