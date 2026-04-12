package kr.modusplant.domains.account.social.domain.vo;

import kr.modusplant.domains.account.social.common.util.domain.vo.AgreedTermsTestUtils;
import kr.modusplant.domains.account.social.domain.exception.EmptyValueException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AgreedTermsTest implements AgreedTermsTestUtils {

    @Test
    @DisplayName("모든 약관 버전이 유효할 때 AgreedTerms를 생성한다")
    void testCreate_givenValidVersions_willReturnAgreedTerms() {
        // when
        AgreedTerms agreedTerms = AgreedTerms.create(
                testAgreedTermsOfUse,
                testAgreedPrivacyPolicy,
                testAgreedCommunityPolicy
        );

        // then
        assertNotNull(agreedTerms);
        assertThat(agreedTerms.getAgreedTermsOfUseVersion()).isEqualTo(testAgreedTermsOfUse);
        assertThat(agreedTerms.getAgreedPrivacyPolicyVersion()).isEqualTo(testAgreedPrivacyPolicy);
        assertThat(agreedTerms.getAgreedCommunityPolicyVersion()).isEqualTo(testAgreedCommunityPolicy);
    }

    @Test
    @DisplayName("약관 버전 중 하나라도 null이면 EmptyValueException이 발생한다")
    void testCreate_givenNullVersion_willThrowEmptyValueException() {
        // when & then
        assertThrows(EmptyValueException.class, () -> AgreedTerms.create(null, testAgreedPrivacyPolicy, testAgreedCommunityPolicy));
        assertThrows(EmptyValueException.class, () -> AgreedTerms.create(testAgreedTermsOfUse, null, testAgreedCommunityPolicy));
        assertThrows(EmptyValueException.class, () -> AgreedTerms.create(testAgreedTermsOfUse, testAgreedPrivacyPolicy, null));

        EmptyValueException exception = assertThrows(EmptyValueException.class,
                () -> AgreedTerms.create(null, null, null));

        assertThat(exception.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_AGREED_TERMS_OF_VERSION);
    }

    @Test
    @DisplayName("모든 내부 버전 객체가 같으면 AgreedTerms는 동등하다")
    void useEqual_givenSameInternalObjects_willReturnTrue() {
        // given
        AgreedTerms sameTerms = AgreedTerms.create(
                testAgreedTermsOfUse,
                testAgreedPrivacyPolicy,
                testAgreedCommunityPolicy
        );

        // when & then
        assertEquals(testAgreedTerms, sameTerms);
        assertEquals(testAgreedTerms.hashCode(), sameTerms.hashCode());
    }

    @Test
    @DisplayName("내부 버전 중 하나라도 다르면 서로 다른 객체로 취급한다")
    void useEqual_givenDifferentInternalVersion_willReturnFalse() {
        // given
        AgreedTermVersion differentVersion = AgreedTermVersion.create("v9.9.9");
        AgreedTerms differentTerms = AgreedTerms.create(
                differentVersion, // 이용약관 버전만 다름
                testAgreedPrivacyPolicy,
                testAgreedCommunityPolicy
        );

        // when & then
        assertNotEquals(testAgreedTerms, differentTerms);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스와 비교 시 false를 반환한다")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        // when & then
        assertNotEquals(testAgreedTerms, testAgreedTermsOfUse);
    }

}