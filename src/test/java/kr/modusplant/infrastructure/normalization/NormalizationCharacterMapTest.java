package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CONJOINING_COMPATIBILITY_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP;
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
    void testLetterLikeSymbolLowerCaseLatinMap_givenLetterLikeSymbols_willReturnInteger() {
        // given & when & then
        assertThat(LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP).hasSize(78);
        assertThat(LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.get(0x1F1E6)).isEqualTo('a');
        assertThat(LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.get(0x1F1FF)).isEqualTo('z');
        assertThat(LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.get(0x1F150)).isEqualTo('a');
        assertThat(LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.get(0x1F169)).isEqualTo('z');
        assertThat(LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.get(0x1F170)).isEqualTo('a');
        assertThat(LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.get(0x1F189)).isEqualTo('z');
    }

    @Test
    @DisplayName("변경 시도로 예외 반환")
    void testLetterLikeSymbolLowerCaseLatinMap_givenModification_willThrowException() {
        // given & when & then
        assertThatThrownBy(() -> LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("키릴 및 그리스 동형 문자로 라틴 소문자 반환")
    void testCrossScriptHomoglyphLatinMap_givenHomoglyphs_willReturnInteger() {
        // given & when & then
        assertThat(CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP.get(0x0455)).isEqualTo('s');
        assertThat(CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP.get(0x0456)).isEqualTo('i');
        assertThat(CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP.get(0x03BF)).isEqualTo('o');
    }

    @Test
    @DisplayName("변경 시도로 예외 반환")
    void testCrossScriptHomoglyphLatinMap_givenModification_willThrowException() {
        // given & when & then
        assertThatThrownBy(() -> CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("한글 문맥의 유사 문자로 호환 자모 반환")
    void testHangulContextLookAlikeJamoMap_givenLookAlikes_willReturnInteger() {
        // given & when & then
        assertThat(HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP.get((int) '1')).isEqualTo('ㅣ');
        assertThat(HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP.get((int) 'l')).isEqualTo('ㅣ');
        assertThat(HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP.get((int) '0')).isEqualTo('ㅇ');
        assertThat(HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP.get((int) '^')).isEqualTo('ㅅ');
        assertThat(HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP.get((int) '人')).isEqualTo('ㅅ');
    }

    @Test
    @DisplayName("변경 시도로 예외 반환")
    void testHangulContextLookAlikeJamoMap_givenModification_willThrowException() {
        // given & when & then
        assertThatThrownBy(() -> HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("라틴 문맥의 유사 문자로 라틴 소문자 반환")
    void testLatinContextLookAlikeLeetLetterMap_givenLookAlikes_willReturnInteger() {
        // given & when & then
        assertThat(LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP.get((int) '1')).isEqualTo('i');
        assertThat(LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP.get((int) '5')).isEqualTo('s');
        assertThat(LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP.get((int) '$')).isEqualTo('s');
        // 문자인 유사 문자와 문장 끝에 흔한 느낌표는 제외
        assertThat(LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP).doesNotContainKeys((int) 'l', (int) 'o', (int) '!');
    }

    @Test
    @DisplayName("변경 시도로 예외 반환")
    void testLatinContextLookAlikeLeetLetterMap_givenModification_willThrowException() {
        // given & when & then
        assertThatThrownBy(() -> LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
