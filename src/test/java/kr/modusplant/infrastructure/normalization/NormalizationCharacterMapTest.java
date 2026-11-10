package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.COMPATIBILITY_CONJOINING_CHOSEONG_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.COMPATIBILITY_CONJOINING_JONGSEONG_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.COMPATIBILITY_CONJOINING_JUNGSEONG_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CONJOINING_COMPATIBILITY_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CROSS_SCRIPT_CONFUSABLE_LATIN_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP;
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
        //noinspection DataFlowIssue
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
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("라틴 문자와 혼동하기 쉬운 키릴 및 그리스 문자로 라틴 소문자 반환")
    void testCrossScriptConfusableLatinMap_givenConfusables_willReturnInteger() {
        // given & when & then
        assertThat(CROSS_SCRIPT_CONFUSABLE_LATIN_MAP.get(0x0455)).isEqualTo('s');
        assertThat(CROSS_SCRIPT_CONFUSABLE_LATIN_MAP.get(0x0456)).isEqualTo('i');
        assertThat(CROSS_SCRIPT_CONFUSABLE_LATIN_MAP.get(0x03BF)).isEqualTo('o');
    }

    @Test
    @DisplayName("변경 시도로 예외 반환")
    void testCrossScriptConfusableLatinMap_givenModification_willThrowException() {
        // given & when & then
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> CROSS_SCRIPT_CONFUSABLE_LATIN_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("호환 자모로 위치별 조합형 자모 반환")
    void testCompatibilityJamoMaps_givenCompatibilityJamo_willReturnInteger() {
        // given & when & then
        assertThat(COMPATIBILITY_CONJOINING_CHOSEONG_MAP.get((int) 'ㄱ')).isEqualTo(0x1100);
        assertThat(COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.get((int) 'ㅏ')).isEqualTo(0x1161);
        assertThat(COMPATIBILITY_CONJOINING_JONGSEONG_MAP.get((int) 'ㄱ')).isEqualTo(0x11A8);
    }

    @Test
    @DisplayName("현대 한글 자모 전체로 위치별 크기 반환")
    void testCompatibilityJamoMaps_givenModernJamo_willReturnInteger() {
        // given & when & then
        assertThat(COMPATIBILITY_CONJOINING_CHOSEONG_MAP).hasSize(19);
        assertThat(COMPATIBILITY_CONJOINING_JUNGSEONG_MAP).hasSize(21);
        assertThat(COMPATIBILITY_CONJOINING_JONGSEONG_MAP).hasSize(27);
    }

    @Test
    @DisplayName("라틴 소문자로 같은 키의 호환 자모 반환")
    void testLatinLowerCaseTwoBeolsikJamoMap_givenLatinLetter_willReturnInteger() {
        // given & when & then
        assertThat(LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP).hasSize(26);
        assertThat(LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.get((int) 'q')).isEqualTo(0x3142); // ㅂ
        assertThat(LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.get((int) 'l')).isEqualTo(0x3163); // ㅣ
        assertThat(LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.get((int) 'm')).isEqualTo(0x3161); // ㅡ
    }

    @Test
    @DisplayName("겹모음과 겹받침으로 키 입력 순서의 문자열 반환")
    void testCompoundJamoKeystrokeJamoMap_givenCompoundJamo_willReturnString() {
        // given & when & then
        assertThat(COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP).hasSize(18);
        assertThat(COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP.get((int) 'ㅟ')).isEqualTo("ㅜㅣ");
        assertThat(COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP.get((int) 'ㅄ')).isEqualTo("ㅂㅅ");
    }

    @Test
    @DisplayName("한 번에 입력하는 자모로 null 반환")
    void testCompoundJamoKeystrokeJamoMap_givenSingleKeystrokeJamo_willReturnNull() {
        // given & when & then
        assertThat(COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP.get((int) 'ㅐ')).isNull();
        assertThat(COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP.get((int) 'ㅆ')).isNull();
    }

    @Test
    @DisplayName("키 입력 순서의 두 자모로 겹모음과 겹받침 반환")
    void testKeystrokeJamoCompoundJamoMap_givenKeystrokeJamoPair_willReturnInteger() {
        // given & when & then
        assertThat(KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP).hasSize(18);
        assertThat(KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP.get("ㅜㅓ")).isEqualTo('ㅝ');
        assertThat(KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP.get("ㅂㅅ")).isEqualTo('ㅄ');
    }

    @Test
    @DisplayName("겹자모를 이루지 않는 두 자모로 null 반환")
    void testKeystrokeJamoCompoundJamoMap_givenNonCompoundPair_willReturnNull() {
        // given & when & then
        assertThat(KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP.get("ㅓㅜ")).isNull();
        assertThat(KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP.get("ㄴㄴ")).isNull();
    }

    @Test
    @DisplayName("해당 위치에 올 수 없는 자모로 null 반환")
    void testCompatibilityJamoMaps_givenJamoInvalidForPosition_willReturnNull() {
        // given & when & then
        assertThat(COMPATIBILITY_CONJOINING_CHOSEONG_MAP.get((int) 'ㄳ')).isNull();
        assertThat(COMPATIBILITY_CONJOINING_JONGSEONG_MAP.get((int) 'ㄸ')).isNull();
        assertThat(COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.get((int) 'ㄱ')).isNull();
    }

    @Test
    @DisplayName("변경 시도로 예외 반환")
    void testCompatibilityJamoMaps_givenModification_willThrowException() {
        // given & when & then
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> COMPATIBILITY_CONJOINING_CHOSEONG_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> COMPATIBILITY_CONJOINING_JONGSEONG_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.put(0, 0))
                .isInstanceOf(UnsupportedOperationException.class);
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP.put(0, ""))
                .isInstanceOf(UnsupportedOperationException.class);
        //noinspection DataFlowIssue
        assertThatThrownBy(() -> KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP.put("", 0))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
