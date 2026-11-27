package kr.modusplant.domains.account.social.adapter.mapper;

import kr.modusplant.domains.account.social.common.util.domain.vo.SocialMemberProfileTestUtils;
import kr.modusplant.domains.account.social.domain.vo.SocialProfile;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.port.mapper.SocialIdentityMapper;
import kr.modusplant.domains.account.social.usecase.record.LoginResult;
import kr.modusplant.domains.account.social.usecase.record.SocialUserInfo;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.*;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_GOOGLE_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_KAKAO_USER_NICKNAME;
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
        SocialUserInfo userInfo = mock(SocialUserInfo.class);
        given(userInfo.id()).willReturn(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
        given(userInfo.email()).willReturn(MEMBER_AUTH_KAKAO_USER_EMAIL);
        given(userInfo.nickname()).willReturn(MEMBER_KAKAO_USER_NICKNAME);

        // when
        SocialProfile result = socialIdentityMapper.toSocialProfile(SocialProvider.KAKAO,userInfo);

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
        SocialUserInfo userInfo = mock(SocialUserInfo.class);
        given(userInfo.id()).willReturn(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
        given(userInfo.email()).willReturn(MEMBER_AUTH_GOOGLE_USER_EMAIL);
        given(userInfo.nickname()).willReturn(MEMBER_GOOGLE_USER_NICKNAME);

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
        assertEquals(AuthProvider.KAKAO, socialIdentityMapper.toSocialAuthProvider(SocialProvider.KAKAO));
        assertEquals(AuthProvider.GOOGLE, socialIdentityMapper.toSocialAuthProvider(SocialProvider.GOOGLE));
    }

    @Test
    @DisplayName("toLinkedAuthProvider Linked AuthProvider 반환")
    void testToLinkedAuthProvider_givenSocialProvider_willReturnLinkedAuthProvider() {
        // when & then
        assertEquals(AuthProvider.BASIC_KAKAO, socialIdentityMapper.toLinkedAuthProvider(SocialProvider.KAKAO));
        assertEquals(AuthProvider.BASIC_GOOGLE, socialIdentityMapper.toLinkedAuthProvider(SocialProvider.GOOGLE));
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
        assertEquals(Role.USER, result.role());
    }

}