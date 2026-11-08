package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CONJOINING_COMPATIBILITY_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NormalizationCharacterMapTest {
    @Test
    @DisplayName("첫소리와 끝소리로 같은 호환 자모 반환")
    void testConjoiningCompatibilityJamoMap_givenLeadingAndTrailingJamo_willReturnInteger() {
        // given & when & then
        assertThat(CONJOINING_COMPATIBILITY_JAMO_MAP.get(0x1100)).isEqualTo(0x3131);
        assertThat(CONJOINING_COMPATIBILITY_JAMO_MAP.get(0x11A8)).isEqualTo(0x3131);
    }

    @Test
    @DisplayName("가운뎃소리로 호환 자모 반환")
    void testConjoiningCompatibilityJamoMap_givenVowelJamo_willReturnInteger() {
        // given & when & then
        assertThat(CONJOINING_COMPATIBILITY_JAMO_MAP.get(0x1175)).isEqualTo(0x3163);
    }

    @Test
    @DisplayName("호환 자모가 없는 자모와 채움 문자로 null 반환")
    void testConjoiningCompatibilityJamoMap_givenJamoWithoutCompatibilityForm_willReturnNull() {
        // given & when & then
        assertThat(CONJOINING_COMPATIBILITY_JAMO_MAP.get(0x1113)).isNull();
        assertThat(CONJOINING_COMPATIBILITY_JAMO_MAP.get(0x115F)).isNull();
        assertThat(CONJOINING_COMPATIBILITY_JAMO_MAP.get(0x1160)).isNull();
    }

    @Test
    @DisplayName("변경 시도로 예외 반환")
    void testConjoiningCompatibilityJamoMap_givenModification_willThrowException() {
        // given & when & then
        assertThatThrownBy(() -> CONJOINING_COMPATIBILITY_JAMO_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("문자형 기호로 라틴 소문자 반환")
    void testLetterLikeSymbolToLowerCaseLatinMap_givenLetterLikeSymbols_willReturnInteger() {
        // given & when & then
        assertThat(LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP).hasSize(78);
        assertThat(LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP.get(0x1F1E6)).isEqualTo('a');
        assertThat(LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP.get(0x1F1FF)).isEqualTo('z');
        assertThat(LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP.get(0x1F150)).isEqualTo('a');
        assertThat(LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP.get(0x1F169)).isEqualTo('z');
        assertThat(LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP.get(0x1F170)).isEqualTo('a');
        assertThat(LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP.get(0x1F189)).isEqualTo('z');
    }

    @Test
    @DisplayName("변경 시도로 예외 반환")
    void testLetterLikeSymbolToLowerCaseLatinMap_givenModification_willThrowException() {
        // given & when & then
        assertThatThrownBy(() -> LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
