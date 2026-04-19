package kr.modusplant.domains.account.social.domain.vo;

import kr.modusplant.domains.account.social.common.util.domain.vo.SocialProfileTestUtils;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_KAKAO_USER_PROVIDER_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_GOOGLE_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_KAKAO_USER_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SocialProfileTest implements SocialProfileTestUtils {
    @Test
    @DisplayName("유효한 정보들로 SocialProfile을 생성한다")
    void testCreate_givenValidInfo_willReturnSocialProfile() {
        // when
        SocialProfile profile = SocialProfile.create(
                SocialProvider.KAKAO,
                MEMBER_AUTH_KAKAO_USER_PROVIDER_ID,
                testKakaoUserEmail,
                MEMBER_KAKAO_USER_NICKNAME
        );

        // then
        assertNotNull(profile);
        assertThat(profile.getSocialProvider()).isEqualTo(SocialProvider.KAKAO);
        assertThat(profile.getProviderId()).isEqualTo(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
        assertThat(profile.getEmail()).isEqualTo(testKakaoUserEmail);
        assertThat(profile.getSocialNickname()).isEqualTo(MEMBER_KAKAO_USER_NICKNAME);
    }

    @Test
    @DisplayName("같은 값을 가진 두 SocialProfile 객체는 동등하다")
    void useEqual_givenSameValues_willReturnTrue() {
        // given
        SocialProfile firstProfile = SocialProfile.create(
                SocialProvider.GOOGLE,
                MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID,
                testGoogleUserEmail,
                MEMBER_GOOGLE_USER_NICKNAME
        );

        // when & then
        assertEquals(firstProfile, testGoogleSocialProfile);
        assertEquals(firstProfile.hashCode(), testGoogleSocialProfile.hashCode());
    }

    @Test
    @DisplayName("다른 프로퍼티(닉네임)를 가진 인스턴스에 대한 equals 호출은 false를 반환한다")
    void useEqual_givenDifferentNickname_willReturnFalse() {
        // when & then
        assertNotEquals(testKakaoSocialProfile, testKakaoSocialProfileWithBasicEmail);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출은 false를 반환한다")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        // when & then
        assertNotEquals(testKakaoSocialProfile, testKakaoUserEmail);
    }

    @Test
    @DisplayName("null과 비교 시 false를 반환한다")
    void useEqual_givenNull_willReturnFalse() {
        // when & then
        assertNotEquals(null, testKakaoSocialProfile);
    }

}