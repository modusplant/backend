package kr.modusplant.domains.account.social.domain.vo;

import kr.modusplant.domains.account.social.common.util.domain.vo.AgreedTermVersionTestUtils;
import kr.modusplant.domains.account.social.domain.exception.EmptyValueException;
import kr.modusplant.domains.account.social.domain.exception.InvalidValueException;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AgreedTermVersionTest implements AgreedTermVersionTestUtils {
    @Test
    @DisplayName("유효한 버전 문자열로 AgreedTermVersion을 생성한다")
    void testCreate_givenValidVersion_willReturnAgreedTermVersion() {
        // when
        AgreedTermVersion version = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);

        // then
        assertNotNull(version);
        assertThat(version.getValue()).isEqualTo(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("빈 값이나 공백으로 생성 시 EmptyValueException이 발생한다")
    void testCreate_givenEmptyOrBlank_willThrowEmptyValueException(String invalidInput) {
        // when & then
        EmptyValueException exception = assertThrows(EmptyValueException.class,
                () -> AgreedTermVersion.create(invalidInput));

        assertThrows(EmptyValueException.class, () -> AgreedTermVersion.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(SocialIdentityErrorCode.EMPTY_AGREED_TERMS_OF_VERSION);
    }

    @ParameterizedTest
    @ValueSource(strings = {"v1", "1.0", "version_1", "1.a.1"}) // Regex.REGEX_VERSION 규격에 맞지 않는 예시
    @DisplayName("버전 형식에 맞지 않는 문자열로 생성 시 InvalidValueException이 발생한다")
    void testCreate_givenInvalidRegex_willThrowInvalidValueException(String invalidVersion) {
        // when & then
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> AgreedTermVersion.create(invalidVersion));

        assertThat(exception.getErrorCode()).isEqualTo(SocialIdentityErrorCode.INVALID_AGREED_TERMS_OF_VERSION);
    }

    @Test
    @DisplayName("동일한 버전 값을 가진 객체는 equals와 hashCode 결과가 같다")
    void useEqual_givenSameValue_willReturnTrue() {
        // given
        AgreedTermVersion sameVersion = AgreedTermVersion.create(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);

        // when & then
        assertEquals(testAgreedTermsOfUse, sameVersion);
        assertEquals(testAgreedTermsOfUse.hashCode(), sameVersion.hashCode());
    }

    @Test
    @DisplayName("값이 다른 객체는 서로 다른 객체로 취급한다")
    void useEqual_givenDifferentValue_willReturnFalse() {
        // when & then
        assertNotEquals(testAgreedTermsOfUse, testAgreedPrivacyPolicy);
    }
}