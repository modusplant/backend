package kr.modusplant.shared.framework.icu4j.util;

import com.ibm.icu.text.Transliterator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Icu4jUtilsTest {

    @Test
    @DisplayName("getAnyNFDTransliterator 호출 시 Transliterator 반환")
    void testGetAnyNFDTransliterator_givenNoParam_willReturnTransliterator() {
        // when
        Transliterator transliterator = Icu4jUtils.getAnyNFDTransliterator();

        // then
        assertThat(transliterator).isNotNull();
    }

    @Test
    @DisplayName("getAnyNFCTransliterator 호출 시 Transliterator 반환")
    void testGetAnyNFCTransliterator_givenNoParam_willReturnTransliterator() {
        // when
        Transliterator transliterator = Icu4jUtils.getAnyNFCTransliterator();

        // then
        assertThat(transliterator).isNotNull();
    }

    @Test
    @DisplayName("NFD Transliterator로 한국어 입력 시 분해된 문자열 반환")
    void testGetAnyNFDTransliterator_givenKoreanInput_willReturnDecomposedOutput() {
        // given
        String korean = "장미";

        // when
        String result = Icu4jUtils.getAnyNFDTransliterator().transliterate(korean);

        // then
        assertThat(result).isNotBlank();
        assertThat(result).isNotEqualTo(korean);
        assertThat(result.length()).isGreaterThan(korean.length());
    }

    @Test
    @DisplayName("NFC Transliterator로 NFD 분해 문자열 입력 시 원래 한국어 문자열 반환")
    void testGetAnyNFCTransliterator_givenNFDInput_willReturnComposedOutput() {
        // given
        String korean = "장미";
        String nfd = Icu4jUtils.getAnyNFDTransliterator().transliterate(korean);

        // when
        String result = Icu4jUtils.getAnyNFCTransliterator().transliterate(nfd);

        // then
        assertThat(result).isEqualTo(korean);
    }
}
