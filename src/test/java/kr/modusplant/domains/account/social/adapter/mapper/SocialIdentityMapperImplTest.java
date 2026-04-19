package kr.modusplant.domains.account.social.adapter.mapper;

import kr.modusplant.domains.account.social.common.util.domain.vo.SocialMemberProfileTestUtils;
import kr.modusplant.domains.account.social.domain.vo.SocialProfile;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.out.client.dto.GoogleUserInfo;
import kr.modusplant.domains.account.social.framework.out.client.dto.KakaoUserInfo;
import kr.modusplant.domains.account.social.usecase.port.client.dto.SocialUserInfo;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.account.social.usecase.response.LoginResult;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_GOOGLE_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_KAKAO_USER_NICKNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SocialIdentityMapperImplTest implements SocialMemberProfileTestUtils {
    private final SocialIdentityMapper socialIdentityMapper = new SocialIdentityMapperImpl();

    @Test
    @DisplayName("toSocialUserProfile으로 SocialProfile 반환")
    void testToSocialProfile_givenSocialProviderAndKakaoUserInfo_willReturnSocialProfile() {
        // given
        SocialUserInfo userInfo = mock(KakaoUserInfo.class);
        given(userInfo.getId()).willReturn(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
        given(userInfo.getEmail()).willReturn(MEMBER_AUTH_KAKAO_USER_EMAIL);
        given(userInfo.getNickname()).willReturn(MEMBER_KAKAO_USER_NICKNAME);

        // when
        SocialProfile result = socialIdentityMapper.toSocialProfile(SocialProvider.GOOGLE.KAKAO,userInfo);

        // then
        assertNotNull(result);
        assertEquals(SocialProvider.KAKAO, result.getSocialProvider());
        assertEquals(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID, result.getProviderId());
        assertEquals(MEMBER_AUTH_KAKAO_USER_EMAIL, result.getEmail().getValue());
        assertEquals(MEMBER_KAKAO_USER_NICKNAME, result.getSocialNickname());
    }

    @Test
    @DisplayName("toSocialProfile으로 SocialProfile 반환")
    void testToSocialProfile_givenSocialProviderAndGoogleUserInfo_willReturnSocialProfile() {
        // given
        SocialUserInfo userInfo = mock(GoogleUserInfo.class);
        given(userInfo.getId()).willReturn(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
        given(userInfo.getEmail()).willReturn(MEMBER_AUTH_GOOGLE_USER_EMAIL);
        given(userInfo.getNickname()).willReturn(MEMBER_GOOGLE_USER_NICKNAME);

        // when
        SocialProfile result = socialIdentityMapper.toSocialProfile(SocialProvider.GOOGLE,userInfo);

        // then
        assertNotNull(result);
        assertEquals(SocialProvider.GOOGLE, result.getSocialProvider());
        assertEquals(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID, result.getProviderId());
        assertEquals(MEMBER_AUTH_GOOGLE_USER_EMAIL, result.getEmail().getValue());
        assertEquals(MEMBER_GOOGLE_USER_NICKNAME, result.getSocialNickname());
    }

    @Test
    @DisplayName("toSocialAuthProvider로 AuthProvider 반환")
    void testToSocialAuthProvider_givenSocialProvider_willReturnAuthProvider() {
        // when & then
        assertEquals(socialIdentityMapper.toSocialAuthProvider(SocialProvider.KAKAO),AuthProvider.KAKAO);
        assertEquals(socialIdentityMapper.toSocialAuthProvider(SocialProvider.GOOGLE),AuthProvider.GOOGLE);
    }

    @Test
    @DisplayName("toLinkedAuthProvider Linked AuthProvider 반환")
    void testToLinkedAuthProvider_givenSocialProvider_willReturnLinkedAuthProvider() {
        // when & then
        assertEquals(socialIdentityMapper.toLinkedAuthProvider(SocialProvider.KAKAO),AuthProvider.BASIC_KAKAO);
        assertEquals(socialIdentityMapper.toLinkedAuthProvider(SocialProvider.GOOGLE),AuthProvider.BASIC_GOOGLE);
    }

    @Test
    @DisplayName("toLoginResult로 LoginResult 반환")
    void testToLoginResult_givenSocialMemberProfile_willReturnLoginResult() {
        // given & when
        LoginResult result = socialIdentityMapper.toLoginResult(testKakaoSocialMemberProfile);

        // then
        assertEquals(result.uuid(),testKakaoSocialMemberProfile.getAccountId().getValue());
        assertEquals(result.email(),testKakaoSocialMemberProfile.getEmail().getValue());
        assertEquals(result.nickname(),testKakaoSocialMemberProfile.getNickname().getValue());
        assertEquals(result.role(), Role.USER);
    }

}