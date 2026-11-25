package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isAsciiLowerCaseLetter;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isCompatibilityJamo;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isHangulSyllable;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isHangulSyllableWithoutTrailingConsonant;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isCombiningMark;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isDefaultIgnorableCodePoint;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isLetter;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isLineTerminator;
import static org.assertj.core.api.Assertions.assertThat;

class TextNormalizationHelperTest {
    @Test
    @DisplayName("기본 무시 코드 포인트로 true 반환")
    void testIsDefaultIgnorableCodePoint_givenDefaultIgnorableCodePoint_willReturnTrue() {
        // given & when & then
        assertThat(isDefaultIgnorableCodePoint(0x200B)).isTrue();
        assertThat(isDefaultIgnorableCodePoint(0x3164)).isTrue();
    }

    @Test
    @DisplayName("기본 무시 코드 포인트가 아닌 문자로 false 반환")
    void testIsDefaultIgnorableCodePoint_givenOtherCharacter_willReturnFalse() {
        // given & when & then
        assertThat(isDefaultIgnorableCodePoint('a')).isFalse();
        assertThat(isDefaultIgnorableCodePoint('시')).isFalse();
    }

    @Test
    @DisplayName("결합 문자로 true 반환")
    void testIsCombiningMark_givenCombiningMark_willReturnTrue() {
        // given & when & then
        assertThat(isCombiningMark(0x0301)).isTrue();
        assertThat(isCombiningMark(0x0903)).isTrue();
        assertThat(isCombiningMark(0x20DD)).isTrue();
    }

    @Test
    @DisplayName("결합 문자가 아닌 문자로 false 반환")
    void testIsCombiningMark_givenOtherCharacter_willReturnFalse() {
        // given & when & then
        assertThat(isCombiningMark('a')).isFalse();
        assertThat(isCombiningMark('시')).isFalse();
    }

    @Test
    @DisplayName("문자로 true 반환")
    void testIsLetter_givenLetter_willReturnTrue() {
        // given & when & then
        assertThat(isLetter('시')).isTrue();
        assertThat(isLetter('ㅅ')).isTrue();
        assertThat(isLetter('a')).isTrue();
        // 호환 자모가 없는 옛한글 조합형 자모
        assertThat(isLetter(0x1113)).isTrue();
    }

    @Test
    @DisplayName("구분자, 제어 문자, 문장 부호, 기호, 숫자, 서식 문자, 사용자 정의 문자, 미할당 코드 포인트로 false 반환")
    void testIsLetter_givenNonLetter_willReturnFalse() {
        // given & when & then
        assertThat(isLetter(' ')).isFalse();
        assertThat(isLetter('\n')).isFalse();
        assertThat(isLetter('.')).isFalse();
        assertThat(isLetter('+')).isFalse();
        assertThat(isLetter('1')).isFalse();
        assertThat(isLetter(0x1F331)).isFalse();
        assertThat(isLetter(0x0600)).isFalse();
        assertThat(isLetter(0xE000)).isFalse();
        assertThat(isLetter(0x0378)).isFalse();
    }

    @Test
    @DisplayName("줄 종결 문자로 true 반환")
    void testIsLineTerminator_givenLineTerminator_willReturnTrue() {
        // given & when & then
        assertThat(isLineTerminator('\n')).isTrue();
        assertThat(isLineTerminator('\r')).isTrue();
        assertThat(isLineTerminator(0x000B)).isTrue();
        assertThat(isLineTerminator(0x000C)).isTrue();
        assertThat(isLineTerminator(0x0085)).isTrue();
        assertThat(isLineTerminator(0x2028)).isTrue();
        assertThat(isLineTerminator(0x2029)).isTrue();
    }

    @Test
    @DisplayName("줄 종결 문자가 아닌 문자로 false 반환")
    void testIsLineTerminator_givenOtherCharacter_willReturnFalse() {
        // given & when & then
        assertThat(isLineTerminator(' ')).isFalse();
        assertThat(isLineTerminator('\t')).isFalse();
        assertThat(isLineTerminator('가')).isFalse();
    }

    @Test
    @DisplayName("ASCII 소문자로 true 반환")
    void testIsAsciiLowerCaseLetter_givenAsciiLowerCaseLetter_willReturnTrue() {
        // given & when & then
        assertThat(isAsciiLowerCaseLetter('a')).isTrue();
        assertThat(isAsciiLowerCaseLetter('z')).isTrue();
    }

    @Test
    @DisplayName("ASCII 소문자가 아닌 문자로 false 반환")
    void testIsAsciiLowerCaseLetter_givenOtherCharacter_willReturnFalse() {
        // given & when & then
        assertThat(isAsciiLowerCaseLetter('A')).isFalse();
        assertThat(isAsciiLowerCaseLetter(0x00DF)).isFalse(); // ß
        assertThat(isAsciiLowerCaseLetter(0x0455)).isFalse(); // 키릴 ѕ
        assertThat(isAsciiLowerCaseLetter('시')).isFalse();
    }

    @Test
    @DisplayName("한글 음절로 true 반환")
    void testIsHangulSyllable_givenHangulSyllable_willReturnTrue() {
        // given & when & then
        assertThat(isHangulSyllable('가')).isTrue();
        assertThat(isHangulSyllable('각')).isTrue();
    }

    @Test
    @DisplayName("한글 음절이 아닌 문자로 false 반환")
    void testIsHangulSyllable_givenOtherCharacter_willReturnFalse() {
        // given & when & then
        assertThat(isHangulSyllable('ㄱ')).isFalse();
        assertThat(isHangulSyllable('a')).isFalse();
    }

    @Test
    @DisplayName("종성 없는 음절로 true 반환")
    void testIsHangulSyllableWithoutTrailingConsonant_givenSyllableWithoutTrailingConsonant_willReturnTrue() {
        // given & when & then
        assertThat(isHangulSyllableWithoutTrailingConsonant('가')).isTrue();
    }

    @Test
    @DisplayName("종성 있는 음절로 false 반환")
    void testIsHangulSyllableWithoutTrailingConsonant_givenSyllableWithTrailingConsonant_willReturnFalse() {
        // given & when & then
        assertThat(isHangulSyllableWithoutTrailingConsonant('각')).isFalse();
    }

    @Test
    @DisplayName("호환 자모로 true 반환")
    void testIsCompatibilityJamo_givenCompatibilityJamo_willReturnTrue() {
        // given & when & then
        assertThat(isCompatibilityJamo(0x3131)).isTrue();
        assertThat(isCompatibilityJamo(0x3163)).isTrue();
    }

    @Test
    @DisplayName("초성 자모와 음절로 false 반환")
    void testIsCompatibilityJamo_givenConjoiningJamoOrSyllable_willReturnFalse() {
        // given & when & then
        assertThat(isCompatibilityJamo(0x1100)).isFalse();
        assertThat(isCompatibilityJamo('가')).isFalse();
    }
}
