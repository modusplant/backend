package kr.modusplant.domains.account.social.domain.vo;

import kr.modusplant.domains.account.social.common.util.domain.vo.SocialMemberProfileTestUtils;
import kr.modusplant.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SocialMemberProfileTest implements SocialMemberProfileTestUtils {

    @Test
    @DisplayName("전체 필드를 사용하여 SocialMemberProfile을 생성한다")
    void testCreate_givenAllFields_willReturnSocialMemberProfile() {
        // when
        SocialMemberProfile profile = SocialMemberProfile.create(
                testNormalMemberId,
                testBasicSocialCredentials,
                testNormalUserEmail,
                testNormalUserNickname,
                Role.USER
        );

        // then
        assertNotNull(profile);
        assertThat(profile.getAccountId()).isEqualTo(testNormalMemberId);
        assertThat(profile.getSocialCredentials()).isEqualTo(testBasicSocialCredentials);
        assertThat(profile.getEmail()).isEqualTo(testNormalUserEmail);
        assertThat(profile.getNickname()).isEqualTo(testNormalUserNickname);
        assertThat(profile.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("AccountId가 null인 새로운 멤버 프로필을 생성한다")
    void testCreateNewMember_givenFieldsWithoutId_willReturnSocialMemberProfileWithNullId() {
        // when
        SocialMemberProfile newProfile = SocialMemberProfile.createNewMember(
                testKakaoSocialCredentials,
                testKakaoUserEmail,
                testKakaoUserNickname,
                Role.USER
        );

        // then
        assertNotNull(newProfile);
        assertNull(newProfile.getAccountId());
        assertThat(newProfile.getSocialCredentials()).isEqualTo(testKakaoSocialCredentials);
        assertThat(newProfile.getEmail()).isEqualTo(testKakaoUserEmail);
    }

    @Test
    @DisplayName("모든 필드 값이 같으면 두 객체는 동등하다")
    void useEqual_givenSameValues_willReturnTrue() {
        // given
        SocialMemberProfile sameProfile = SocialMemberProfile.create(
                testKakaoAccountId,
                testKakaoSocialCredentials,
                testKakaoUserEmail,
                testKakaoUserNickname,
                Role.USER
        );

        // when & then
        assertEquals(testKakaoSocialMemberProfile, sameProfile);
        assertEquals(testKakaoSocialMemberProfile.hashCode(), sameProfile.hashCode());
    }

    @Test
    @DisplayName("AccountId가 다르면 동등성 비교에서 false를 반환한다")
    void useEqual_givenDifferentAccountId_willReturnFalse() {
        // given
        SocialMemberProfile differentIdProfile = SocialMemberProfile.create(
                testGoogleAccountId, // ID만 다름
                testKakaoSocialCredentials,
                testKakaoUserEmail,
                testKakaoUserNickname,
                Role.USER
        );

        // when & then
        assertNotEquals(testKakaoSocialMemberProfile, differentIdProfile);
    }

    @Test
    @DisplayName("이메일 정보가 다른 프로필은 서로 다른 객체로 취급한다")
    void useEqual_givenDifferentEmail_willReturnFalse() {
        // when & then
        assertNotEquals(testKakaoSocialMemberProfile, testKakaoSocialMemberProfileWithBasicEmail);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스와 비교 시 false를 반환한다")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        // when & then
        assertNotEquals(testKakaoSocialMemberProfile, testKakaoAccountId);
    }

}