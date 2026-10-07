package kr.modusplant.infrastructure.transliterate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnicodeTransliteratorTest {

    private final UnicodeTransliterator transliterator = new UnicodeTransliterator();

    @Test
    @DisplayName("한국어 문자열로 문자열 반환")
    void testSeparateKoreanIntoConsonantAndVowel_givenKoreanString_willReturnString() {
        // given
        String korean = "장미";

        // when
        String result = transliterator.separateKoreanIntoConsonantAndVowel(korean);

        // then
        assertThat(result).isNotBlank();
        assertThat(result).isNotEqualTo(korean);
        assertThat(result.length()).isGreaterThan(korean.length());
    }

    @Test
    @DisplayName("NFD 문자열로 문자열 반환")
    void testCombineKoreanIntoConsonantAndVowel_givenNFDString_willReturnString() {
        // given
        String korean = "장미";
        String transliteratedKorean = transliterator.separateKoreanIntoConsonantAndVowel(korean);

        // when
        String combinedKorean = transliterator.combineKoreanIntoConsonantAndVowel(transliteratedKorean);

        // then
        assertThat(combinedKorean).isEqualTo(korean);
    }

    @Test
    @DisplayName("비한국어 문자열로 문자열 반환")
    void testSeparateKoreanIntoConsonantAndVowel_givenNonKoreanString_willReturnString() {
        // given
        String nonKorean = "rose";

        // when
        String result = transliterator.separateKoreanIntoConsonantAndVowel(nonKorean);

        // then
        assertThat(result).isEqualTo(nonKorean);
    }

    @Test
    @DisplayName("혼합 문자열로 문자열 반환")
    void testSeparateKoreanIntoConsonantAndVowel_givenMixedString_willReturnString() {
        // given
        String mixed = "장미rose";

        // when
        String result = transliterator.separateKoreanIntoConsonantAndVowel(mixed);

        // then
        assertThat(result).contains("rose");
        assertThat(result.length()).isGreaterThan(mixed.length());
    }
}
