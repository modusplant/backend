package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isCompatibilityJamo;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isNoiseJamo;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isHangulSyllable;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isHangulSyllableWithoutFinal;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isInCombiningMark;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isInDICP;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isLineTerminator;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isNonLetterNoiseCategory;
import static org.assertj.core.api.Assertions.assertThat;

class NormalizationHelperTest {
    @Test
    @DisplayName("기본 무시 코드 포인트로 true 반환")
    void testIsInDICP_givenDefaultIgnorableCodePoint_willReturnTrue() {
        // given & when & then
        assertThat(isInDICP().test(0x200B)).isTrue();
        assertThat(isInDICP().test(0x3164)).isTrue();
    }

    @Test
    @DisplayName("기본 무시 코드 포인트가 아닌 문자로 false 반환")
    void testIsInDICP_givenOtherCharacter_willReturnFalse() {
        // given & when & then
        assertThat(isInDICP().test('a')).isFalse();
        assertThat(isInDICP().test('시')).isFalse();
    }

    @Test
    @DisplayName("결합 문자로 true 반환")
    void testIsInCombiningMark_givenCombiningMark_willReturnTrue() {
        // given & when & then
        assertThat(isInCombiningMark(0x0301)).isTrue();
        assertThat(isInCombiningMark(0x0903)).isTrue();
        assertThat(isInCombiningMark(0x20DD)).isTrue();
    }

    @Test
    @DisplayName("결합 문자가 아닌 문자로 false 반환")
    void testIsInCombiningMark_givenOtherCharacter_willReturnFalse() {
        // given & when & then
        assertThat(isInCombiningMark('a')).isFalse();
        assertThat(isInCombiningMark('시')).isFalse();
    }

    @Test
    @DisplayName("구분자, 제어 문자, 문장 부호, 기호, 숫자로 true 반환")
    void testIsNonLetterNoiseCategory_givenNonLetterNoise_willReturnTrue() {
        // given & when & then
        assertThat(isNonLetterNoiseCategory(' ')).isTrue();
        assertThat(isNonLetterNoiseCategory('\n')).isTrue();
        assertThat(isNonLetterNoiseCategory('.')).isTrue();
        assertThat(isNonLetterNoiseCategory('+')).isTrue();
        assertThat(isNonLetterNoiseCategory('1')).isTrue();
        assertThat(isNonLetterNoiseCategory(0x1F331)).isTrue();
    }

    @Test
    @DisplayName("문자로 false 반환")
    void testIsNonLetterNoiseCategory_givenLetter_willReturnFalse() {
        // given & when & then
        assertThat(isNonLetterNoiseCategory('시')).isFalse();
        assertThat(isNonLetterNoiseCategory('ㅅ')).isFalse();
        assertThat(isNonLetterNoiseCategory('a')).isFalse();
    }

    @Test
    @DisplayName("잡음 자모로 true 반환")
    void testIsNoiseJamo_givenNoiseJamo_willReturnTrue() {
        // given & when & then
        assertThat(isNoiseJamo('ㅋ')).isTrue();
        assertThat(isNoiseJamo('ㅎ')).isTrue();
        assertThat(isNoiseJamo('ㅠ')).isTrue();
        assertThat(isNoiseJamo('ㅜ')).isTrue();
    }

    @Test
    @DisplayName("잡음 자모가 아닌 문자로 false 반환")
    void testIsNoiseJamo_givenOtherCharacter_willReturnFalse() {
        // given & when & then
        assertThat(isNoiseJamo('ㅅ')).isFalse();
        assertThat(isNoiseJamo('가')).isFalse();
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
    @DisplayName("받침 없는 음절로 true 반환")
    void testIsHangulSyllableWithoutFinal_givenSyllableWithoutFinal_willReturnTrue() {
        // given & when & then
        assertThat(isHangulSyllableWithoutFinal('가')).isTrue();
    }

    @Test
    @DisplayName("받침 있는 음절로 false 반환")
    void testIsHangulSyllableWithoutFinal_givenSyllableWithFinal_willReturnFalse() {
        // given & when & then
        assertThat(isHangulSyllableWithoutFinal('각')).isFalse();
    }

    @Test
    @DisplayName("호환 자모로 true 반환")
    void testIsCompatibilityJamo_givenCompatibilityJamo_willReturnTrue() {
        // given & when & then
        assertThat(isCompatibilityJamo(0x3131)).isTrue();
        assertThat(isCompatibilityJamo(0x3163)).isTrue();
    }

    @Test
    @DisplayName("첫소리 자모와 음절로 false 반환")
    void testIsCompatibilityJamo_givenConjoiningJamoOrSyllable_willReturnFalse() {
        // given & when & then
        assertThat(isCompatibilityJamo(0x1100)).isFalse();
        assertThat(isCompatibilityJamo('가')).isFalse();
    }
}
