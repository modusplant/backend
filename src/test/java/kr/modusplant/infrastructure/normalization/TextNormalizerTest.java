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

    private static String aggressivelyNormalizedText(String original) {
        return textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize(original)).getText();
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
    @DisplayName("공백과 줄바꿈 삽입으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenWhitespaceInsertion_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시 발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시\n발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시\t발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("f u c k")).isEqualTo("fuck");
    }

    @Test
    @DisplayName("문장 부호와 기호 삽입으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenPunctuationAndSymbolInsertion_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시.발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시*발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("**시**발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시~~발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("숫자와 이모지 삽입으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenDigitAndEmojiInsertion_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("씨1발")).isEqualTo("씨발");
        assertThat(aggressivelyNormalizedText("시2발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시🌱발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("채움 자모 삽입으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenFillerJamoInsertion_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시ㅋ발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시ㅠ발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시ㅋㅎ발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("음절 사이가 아닌 채움 자모로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenFillerJamoOutsideSyllables_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅㅋ")).isEqualTo("ㅅㅋ");
        assertThat(aggressivelyNormalizedText("시ㅋ")).isEqualTo("시ㅋ");
        assertThat(aggressivelyNormalizedText("ㅋ발")).isEqualTo("ㅋ발");
    }

    @Test
    @DisplayName("음절로 나뉘는 자모 연속으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenComposableCompatibilityJamoRun_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅂㅕㅇ신")).isEqualTo("병신");
        assertThat(aggressivelyNormalizedText("ㅅㅣ발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("ㅅㅣㅂㅏㄹ")).isEqualTo("시발");
    }

    @Test
    @DisplayName("음절로 나뉘지 않는 자모 연속으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenNonComposableCompatibilityJamoRun_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅㅂ")).isEqualTo("ㅅㅂ");
        assertThat(aggressivelyNormalizedText("ㅋㅋ")).isEqualTo("ㅋ");
        assertThat(aggressivelyNormalizedText("시바ㄹ")).isEqualTo("시바ㄹ");
        assertThat(aggressivelyNormalizedText("ㅅㅂㅠㅠ")).isEqualTo("ㅅㅂㅠ");
        assertThat(aggressivelyNormalizedText("ㄷㅜㅣㅈㅕ")).isEqualTo("ㄷㅜㅣㅈㅕ");
    }

    @Test
    @DisplayName("결합 후 모음 늘이기가 있는 자모 연속으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenComposedJamoWithElongation_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅㅣ이발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("한글 사이의 문자와 숫자 및 기호로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenLettersAndNoiseNextToHangul_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅl발")).isEqualTo("ㅅl발");
        assertThat(aggressivelyNormalizedText("시l발")).isEqualTo("시l발");
        assertThat(aggressivelyNormalizedText("人l발")).isEqualTo("人l발");
        assertThat(aggressivelyNormalizedText("ㅅ|발")).isEqualTo("ㅅ발");
        assertThat(aggressivelyNormalizedText("시 1 발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("키릴 동형 문자로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenCrossScriptHomoglyphs_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ѕіbаl")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("라틴 문자 사이의 숫자와 기호로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenNoiseInLatinWord_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("s1bal")).isEqualTo("sbal");
        assertThat(aggressivelyNormalizedText("$ibal")).isEqualTo("ibal");
        assertThat(aggressivelyNormalizedText("5hit")).isEqualTo("hit");
    }

    @Test
    @DisplayName("라틴 문자와 숫자 및 기호만으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenLatinLettersOrOnlyNoise_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("tl발")).isEqualTo("tl발");
        assertThat(aggressivelyNormalizedText("helo")).isEqualTo("helo");
        assertThat(aggressivelyNormalizedText("1^0")).isEmpty();
    }

    @Test
    @DisplayName("문자 반복으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenRepeatedCharacters_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시발발발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("ssiiibal")).isEqualTo("sibal");
        assertThat(aggressivelyNormalizedText("fuuuck")).isEqualTo("fuck");
    }

    @Test
    @DisplayName("모음 늘이기로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenElongatedVowels_willReturnNormalizedText() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시이발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시이이발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("씨ㅣ발")).isEqualTo("씨발");
        // 뒤 음절의 받침을 앞 음절로 옮김
        assertThat(aggressivelyNormalizedText("씨이바알")).isEqualTo("씨발");
    }

    @Test
    @DisplayName("늘인 모음이 아닌 음절로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenSyllablesWithoutElongation_willReturnNormalizedText() {
        // given & when & then
        // 모음이 다름
        assertThat(aggressivelyNormalizedText("시아")).isEqualTo("시아");
        // 앞 음절에 받침이 있음
        assertThat(aggressivelyNormalizedText("발아")).isEqualTo("발아");
        // 뒤 음절의 초성이 ㅇ이 아님
        assertThat(aggressivelyNormalizedText("시미")).isEqualTo("시미");
    }

    @Test
    @DisplayName("제거된 문자 양옆의 단위로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenUnitsAroundRemovedSeparator_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize("시 발"));

        // then
        assertThat(normalizedText.getText()).isEqualTo("시발");
        assertSpans(normalizedText, 0, 0, 1, 0, 1);
        assertSpans(normalizedText, 1, 2, 3, 2, 3);
    }

    @Test
    @DisplayName("결합된 자모로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenComposedJamo_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize("ㅂㅕㅇ신"));

        // then
        assertThat(normalizedText.getText()).isEqualTo("병신");
        assertSpans(normalizedText, 0, 0, 3, 0, 3);
        assertSpans(normalizedText, 1, 3, 4, 3, 4);
    }

    @Test
    @DisplayName("접힌 반복 문자로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenCollapsedRepeats_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize("시발발발"));

        // then
        assertThat(normalizedText.getText()).isEqualTo("시발");
        assertSpans(normalizedText, 0, 0, 1, 0, 1);
        assertSpans(normalizedText, 1, 1, 4, 1, 4);
    }

    @Test
    @DisplayName("병합된 모음으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenMergedVowels_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize("씨이바알"));

        // then
        assertThat(normalizedText.getText()).isEqualTo("씨발");
        assertSpans(normalizedText, 0, 0, 2, 0, 2);
        assertSpans(normalizedText, 1, 2, 4, 2, 4);
    }

    @Test
    @DisplayName("서로게이트 쌍으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenSurrogatePairs_willReturnNormalizedText() {
        // given
        String repeatedSupplementaryCharacter = "𠀀𠀀";
        String differentSupplementaryCharacters = "𠀀𠀁";

        // when
        NormalizedText collapsedText =
                textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize(repeatedSupplementaryCharacter));

        // then
        assertThat(collapsedText.getText()).isEqualTo("𠀀");
        assertSpans(collapsedText, 0, 0, 4, 0, 4);
        assertSpans(collapsedText, 1, 0, 4, 0, 4);
        assertThat(aggressivelyNormalizedText(differentSupplementaryCharacters))
                .isEqualTo(differentSupplementaryCharacters);
    }

    @Test
    @DisplayName("빈 문자열로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenEmptyString_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize(""));

        // then
        assertThat(normalizedText.length()).isZero();
    }

    @Test
    @DisplayName("구분 문자만 있는 문자열로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenOnlySeparators_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize(" .,\n🌱"));

        // then
        assertThat(normalizedText.length()).isZero();
    }

    @Test
    @DisplayName("한국어 문자열로 문자열 반환")
    void testNfdNormalize_givenKoreanString_willReturnString() {
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
    void testNfdNormalize_givenNonKoreanString_willReturnString() {
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
