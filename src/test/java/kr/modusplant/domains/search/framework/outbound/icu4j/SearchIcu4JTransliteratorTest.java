package kr.modusplant.domains.search.framework.outbound.icu4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchIcu4JTransliteratorTest {

    private final SearchIcu4JTransliterator transliterator = new SearchIcu4JTransliterator();

    @Test
    @DisplayName("한국어 문자열 입력 시 NFD 분해된 문자열 반환")
    void testSeparateKoreanIntoConsonantAndVowel_givenKoreanString_willReturnNFDString() {
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
    @DisplayName("NFD 분해 문자열 입력 시 NFC 조합된 원래 한국어 문자열 반환")
    void testCombineKoreanIntoConsonantAndVowel_givenNFDString_willReturnNFCString() {
        // given
        String korean = "장미";
        String transliteratedKorean = transliterator.separateKoreanIntoConsonantAndVowel(korean);

        // when
        String combinedKorean = transliterator.combineKoreanIntoConsonantAndVowel(transliteratedKorean);

        // then
        assertThat(combinedKorean).isEqualTo(korean);
    }

    @Test
    @DisplayName("비한국어 문자열 입력 시 변경 없이 동일한 문자열 반환")
    void testSeparateKoreanIntoConsonantAndVowel_givenNonKoreanString_willReturnUnchangedString() {
        // given
        String nonKorean = "rose";

        // when
        String result = transliterator.separateKoreanIntoConsonantAndVowel(nonKorean);

        // then
        assertThat(result).isEqualTo(nonKorean);
    }

    @Test
    @DisplayName("한국어와 비한국어가 혼합된 문자열 입력 시 한국어 부분만 분해된 문자열 반환")
    void testSeparateKoreanIntoConsonantAndVowel_givenMixedString_willReturnPartiallyDecomposedString() {
        // given
        String mixed = "장미rose";

        // when
        String result = transliterator.separateKoreanIntoConsonantAndVowel(mixed);

        // then
        assertThat(result).contains("rose");
        assertThat(result.length()).isGreaterThan(mixed.length());
    }
}
