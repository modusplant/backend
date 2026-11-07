package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextNormalizerTest {

    private final TextNormalizer textNormalizer = new TextNormalizer();

    @Test
    @DisplayName("한국어 문자열로 문자열 반환")
    void testSeparateKoreanIntoConsonantAndVowel_givenKoreanString_willReturnString() {
        // given
        String korean = "장미";

        // when
        String result = textNormalizer.nfdNormalize(korean);

        // then
        assertThat(result).isNotBlank();
        assertThat(result).isNotEqualTo(korean);
        assertThat(result.length()).isGreaterThan(korean.length());
    }

    @Test
    @DisplayName("NFD 문자열로 문자열 반환")
    void testNfcNormalize_givenNFDString_willReturnString() {
        // given
        String korean = "장미";
        String transliteratedKorean = textNormalizer.nfdNormalize(korean);

        // when
        String combinedKorean = textNormalizer.nfcNormalize(transliteratedKorean);

        // then
        assertThat(combinedKorean).isEqualTo(korean);
    }

    @Test
    @DisplayName("비한국어 문자열로 문자열 반환")
    void testSeparateKoreanIntoConsonantAndVowel_givenNonKoreanString_willReturnString() {
        // given
        String nonKorean = "rose";

        // when
        String result = textNormalizer.nfdNormalize(nonKorean);

        // then
        assertThat(result).isEqualTo(nonKorean);
    }

    @Test
    @DisplayName("혼합 문자열로 문자열 반환")
    void testNfdNormalize_givenMixedString_willReturnString() {
        // given
        String mixed = "장미rose";

        // when
        String result = textNormalizer.nfdNormalize(mixed);

        // then
        assertThat(result).contains("rose");
        assertThat(result.length()).isGreaterThan(mixed.length());
    }
}
