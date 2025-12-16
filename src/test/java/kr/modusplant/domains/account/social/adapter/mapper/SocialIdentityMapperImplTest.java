package kr.modusplant.domains.account.social.adapter.mapper;

import kr.modusplant.domains.account.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.account.social.framework.out.client.dto.GoogleUserInfo;
import kr.modusplant.domains.account.social.framework.out.client.dto.KakaoUserInfo;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SocialIdentityMapperImplTest {
    private final SocialIdentityMapper socialIdentityMapper = new SocialIdentityMapperImpl();

    @Test
    @DisplayName("toSocialUserProfile으로 SocialUserProfile 반환")
    void testToSocialUserProfile_givenAuthProviderAndKakaoUserInfo_willReturnSocialUserProfile() {
        // given
        SocialUserInfo userInfo = mock(KakaoUserInfo.class);
        given(userInfo.getId()).willReturn(TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING);
        given(userInfo.getEmail()).willReturn(TEST_SOCIAL_KAKAO_EMAIL_STRING);
        given(userInfo.getNickname()).willReturn(TEST_SOCIAL_KAKAO_NICKNAME_STRING);

        // when
        SocialAccountProfile result = socialIdentityMapper.toSocialUserProfile(AuthProvider.KAKAO,userInfo);

        // then
        assertNotNull(result);
        assertEquals(AuthProvider.KAKAO, result.getSocialCredentials().getProvider());
        assertEquals(TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING, result.getSocialCredentials().getProviderId());
        assertEquals(TEST_SOCIAL_KAKAO_EMAIL_STRING, result.getEmail().getValue());
        assertEquals(TEST_SOCIAL_KAKAO_NICKNAME_STRING, result.getNickname().getValue());
        assertTrue(result.getSocialCredentials().isKakao());
    }

    @Test
    @DisplayName("toSocialUserProfile으로 SocialUserProfile 반환")
    void testToSocialUserProfile_givenAuthProviderAndGoogleUserInfo_willReturnSocialUserProfile() {
        // given
        SocialUserInfo userInfo = mock(GoogleUserInfo.class);
        given(userInfo.getId()).willReturn(TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING);
        given(userInfo.getEmail()).willReturn(TEST_SOCIAL_GOOGLE_EMAIL_STRING);
        given(userInfo.getNickname()).willReturn(TEST_SOCIAL_GOOGLE_NICKNAME_STRING);

        // when
        SocialAccountProfile result = socialIdentityMapper.toSocialUserProfile(AuthProvider.GOOGLE,userInfo);

        // then
        assertNotNull(result);
        assertEquals(AuthProvider.GOOGLE, result.getSocialCredentials().getProvider());
        assertEquals(TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING, result.getSocialCredentials().getProviderId());
        assertEquals(TEST_SOCIAL_GOOGLE_EMAIL_STRING, result.getEmail().getValue());
        assertEquals(TEST_SOCIAL_GOOGLE_NICKNAME_STRING, result.getNickname().getValue());
        assertTrue(result.getSocialCredentials().isGoogle());
    }

}