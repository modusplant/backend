package kr.modusplant.infrastructure.normalization;

import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextNormalizerTest {
    private static final Icu4jConfig icu4jConfig = new Icu4jConfig();
    private static final TextNormalizer textNormalizer =
            new TextNormalizer(icu4jConfig.nfdNormalizer(), icu4jConfig.nfcNormalizer(),
                    icu4jConfig.nfkdNormalizer(), icu4jConfig.nfkcNormalizer());

    private static String lightlyNormalizedText(String original) {
        return textNormalizer.lightlyNormalize(original).getText();
    }

    private static void assertSpans(NormalizedText normalizedText, int index,
                                    int lightlyNormalizedStart, int lightlyNormalizedEnd,
                                    int originalStart, int originalEnd) {
        assertThat(normalizedText.getLightlyNormalizedStart(index)).isEqualTo(lightlyNormalizedStart);
        assertThat(normalizedText.getLightlyNormalizedEnd(index)).isEqualTo(lightlyNormalizedEnd);
        assertThat(normalizedText.getOriginalStart(index)).isEqualTo(originalStart);
        assertThat(normalizedText.getOriginalEnd(index)).isEqualTo(originalEnd);
    }

    @Test
    @DisplayName("전각 및 호환 문자로 정규화 뷰 반환")
    void testLightlyNormalize_givenFullWidthCharacters_willReturnNormalizedText() {
        // given & when & then
        assertThat(lightlyNormalizedText("시１발")).isEqualTo("시1발");
        assertThat(lightlyNormalizedText("ｓｉｂａｌ")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("수학 및 원문자 영숫자로 정규화 뷰 반환")
    void testLightlyNormalize_givenMathematicalAndEnclosedAlphanumerics_willReturnNormalizedText() {
        // given & when & then
        assertThat(lightlyNormalizedText("𝐬𝐢𝐛𝐚𝐥")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("ⓢⓘⓑⓐⓛ")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("호환 분해가 없는 문자형 기호로 정규화 뷰 반환")
    void testLightlyNormalize_givenLetterLikeSymbols_willReturnNormalizedText() {
        // given & when & then
        assertThat(lightlyNormalizedText("🇸🇮🇧🇦🇱")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("🅢🅘🅑🅐🅛")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("🆂🅸🅱🅰🅻")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("반각 및 원문자 한글로 정규화 뷰 반환")
    void testLightlyNormalize_givenHalfWidthAndEnclosedHangul_willReturnNormalizedText() {
        // given & when & then
        // 반각 ㄱ, 반각 ㅅ, 반각 ㅂ
        assertThat(lightlyNormalizedText("ﾡ")).isEqualTo("ㄱ");
        assertThat(lightlyNormalizedText("ﾵﾲ")).isEqualTo("ㅅㅂ");
        assertThat(lightlyNormalizedText("㉦㉥")).isEqualTo("ㅅㅂ");
        assertThat(lightlyNormalizedText("㉮")).isEqualTo("가");
    }

    @Test
    @DisplayName("조합형 자모 입력으로 정규화 뷰 반환")
    void testLightlyNormalize_givenConjoiningJamo_willReturnNormalizedText() {
        // given & when & then
        assertThat(lightlyNormalizedText("시발")).isEqualTo("시발");
        // 짝이 없는 첫소리
        assertThat(lightlyNormalizedText("ᄉᄇ")).isEqualTo("ㅅㅂ");
        // 짝이 없는 가운뎃소리와 끝소리
        assertThat(lightlyNormalizedText("ᅵ ᆯ")).isEqualTo("ㅣ ㄹ");
        // 확장 블록의 옛한글 자모 중 호환 자모가 있는 경우
        assertThat(lightlyNormalizedText("ᅀ")).isEqualTo("ㅿ");
        assertThat(lightlyNormalizedText("ᆡ")).isEqualTo("ㆎ");
    }

    @Test
    @DisplayName("호환 자모가 없는 옛한글 자모로 정규화 뷰 반환")
    void testLightlyNormalize_givenArchaicJamoWithoutCompatibilityForm_willReturnNormalizedText() {
        // given & when & then
        assertThat(lightlyNormalizedText("ᄓ")).isEqualTo("ᄓ");
        assertThat(lightlyNormalizedText("ꥠ")).isEqualTo("ꥠ");
    }

    @Test
    @DisplayName("보이지 않는 문자 삽입으로 정규화 뷰 반환")
    void testLightlyNormalize_givenInvisibleCharacters_willReturnNormalizedText() {
        // given & when & then
        // 폭 없는 공백, 폭 없는 결합자, 양방향 제어, 태그 문자, 이체자 선택자, 보충 이체자 선택자, 소프트 하이픈, 단어 결합자,
        // 자소 결합자, BOM
        assertThat(lightlyNormalizedText("시​발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시‍발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시‮발‬")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시󠁡발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시️발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시󠄀발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시­발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시⁠발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시͏발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("﻿시발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("한글 채움 문자 삽입으로 정규화 뷰 반환")
    void testLightlyNormalize_givenHangulFillers_willReturnNormalizedText() {
        // given & when & then
        assertThat(lightlyNormalizedText("시ㅤ발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시ᅟ발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시ᅠ발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시ﾠ발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("결합 문자 부착으로 정규화 뷰 반환")
    void testLightlyNormalize_givenCombiningMarks_willReturnNormalizedText() {
        // given & when & then
        assertThat(lightlyNormalizedText("시̈발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("s̈ibal")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("śíbàl")).isEqualTo("sibal");
        // 한글 방점(공백 결합 문자)
        assertThat(lightlyNormalizedText("시〮발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("대소문자 혼용으로 정규화 뷰 반환")
    void testLightlyNormalize_givenMixedCase_willReturnNormalizedText() {
        // given & when & then
        assertThat(lightlyNormalizedText("SiBaL")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("QUDTLS")).isEqualTo("qudtls");
        // 소문자 변환 시 길이가 바뀌는 문자도 결합 문자 제거 후 한 단위로 유지
        assertThat(lightlyNormalizedText("İ")).isEqualTo("i");
    }

    @Test
    @DisplayName("공백과 문장 부호로 정규화 뷰 반환")
    void testLightlyNormalize_givenWhitespaceAndPunctuation_willReturnNormalizedText() {
        // given & when & then
        assertThat(lightlyNormalizedText("시 발.")).isEqualTo("시 발.");
    }

    @Test
    @DisplayName("보이지 않는 문자만 있는 문자열로 정규화 뷰 반환")
    void testLightlyNormalize_givenOnlyInvisibleCharacters_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.lightlyNormalize("​ㅤ﻿󠁡");

        // then
        assertThat(normalizedText.getText()).isEmpty();
        assertThat(normalizedText.length()).isZero();
    }

    @Test
    @DisplayName("빈 문자열로 정규화 뷰 반환")
    void testLightlyNormalize_givenEmptyString_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.lightlyNormalize("");

        // then
        assertThat(normalizedText.length()).isZero();
    }

    @Test
    @DisplayName("서로게이트 쌍으로 정규화 뷰 반환")
    void testLightlyNormalize_givenSurrogatePair_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.lightlyNormalize("𝐬i");

        // then
        assertThat(normalizedText.getText()).isEqualTo("si");
        assertSpans(normalizedText, 0, 0, 1, 0, 2);
        assertSpans(normalizedText, 1, 1, 2, 2, 3);
    }

    @Test
    @DisplayName("조합형 자모 군집으로 정규화 뷰 반환")
    void testLightlyNormalize_givenConjoiningJamoCluster_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.lightlyNormalize("시발!");

        // then
        assertThat(normalizedText.getText()).isEqualTo("시발!");
        assertSpans(normalizedText, 0, 0, 1, 0, 2);
        assertSpans(normalizedText, 1, 1, 2, 2, 5);
        assertSpans(normalizedText, 2, 2, 3, 5, 6);
    }

    @Test
    @DisplayName("지역 표시 기호 쌍으로 정규화 뷰 반환")
    void testLightlyNormalize_givenRegionalIndicatorPair_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.lightlyNormalize("🇸🇮🇧");

        // then
        assertThat(normalizedText.getText()).isEqualTo("sib");
        assertSpans(normalizedText, 0, 0, 2, 0, 4);
        assertSpans(normalizedText, 1, 0, 2, 0, 4);
        assertSpans(normalizedText, 2, 2, 3, 4, 6);
    }

    @Test
    @DisplayName("결합 문자와 보이지 않는 문자가 붙은 군집으로 정규화 뷰 반환")
    void testLightlyNormalize_givenClusterWithMarkAndInvisibleCharacter_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.lightlyNormalize("s̈i시‍발");

        // then
        assertThat(normalizedText.getText()).isEqualTo("si시발");
        assertSpans(normalizedText, 0, 0, 1, 0, 2);
        assertSpans(normalizedText, 1, 1, 2, 2, 3);
        assertSpans(normalizedText, 2, 2, 3, 3, 5);
        assertSpans(normalizedText, 3, 3, 4, 5, 6);
    }

    @Test
    @DisplayName("여러 단위로 펼쳐지는 문자로 정규화 뷰 반환")
    void testLightlyNormalize_givenCharacterExpandingToSeveralUnits_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.lightlyNormalize("ﬁ시");

        // then
        assertThat(normalizedText.getText()).isEqualTo("fi시");
        assertSpans(normalizedText, 0, 0, 2, 0, 1);
        assertSpans(normalizedText, 1, 0, 2, 0, 1);
        assertSpans(normalizedText, 2, 2, 3, 1, 2);
    }

    @Test
    @DisplayName("보이지 않는 문자 사이의 단위로 정규화 뷰 반환")
    void testLightlyNormalize_givenUnitsAroundRemovedCharacter_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.lightlyNormalize("시​발");

        // then
        assertThat(normalizedText.getText()).isEqualTo("시발");
        assertSpans(normalizedText, 0, 0, 1, 0, 1);
        assertSpans(normalizedText, 1, 1, 2, 2, 3);
    }

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
