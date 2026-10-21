package kr.modusplant.shared.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;
import static kr.modusplant.shared.util.MaskUtils.maskEmail;
import static org.assertj.core.api.Assertions.assertThat;

class MaskUtilsTest {

    @Test
    @DisplayName("값으로 가린 문자열 반환")
    void testMaskAnyString_givenValue_willReturnString() {
        // given & when & then
        assertThat(maskAnyString("userPw2!")).isEqualTo("***");
    }

    @Test
    @DisplayName("null로 null 반환")
    void testMaskAnyString_givenNull_willReturnNull() {
        // given & when & then
        assertThat(maskAnyString(null)).isNull();
    }

    @Test
    @DisplayName("이메일로 첫 글자와 도메인만 남긴 문자열 반환")
    void testMaskEmail_givenEmail_willReturnString() {
        // given & when & then
        assertThat(maskEmail("akdnjs0308@gmail.com")).isEqualTo("a***@gmail.com");
    }

    @Test
    @DisplayName("이메일 서식이 아닌 값으로 가린 문자열 반환")
    void testMaskEmail_givenNonEmail_willReturnString() {
        // given & when & then
        assertThat(maskEmail("not-an-email")).isEqualTo("***");
        assertThat(maskEmail("@gmail.com")).isEqualTo("***");
    }

    @Test
    @DisplayName("null로 null 반환")
    void testMaskEmail_givenNull_willReturnNull() {
        // given & when & then
        assertThat(maskEmail(null)).isNull();
    }
}
