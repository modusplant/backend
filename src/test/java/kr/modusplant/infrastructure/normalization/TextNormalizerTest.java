package kr.modusplant.infrastructure.normalization;

import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.NormalizedView.JamoPosition.FINAL;
import static kr.modusplant.infrastructure.normalization.NormalizedView.JamoPosition.INITIAL;
import static kr.modusplant.infrastructure.normalization.NormalizedView.JamoPosition.MEDIAL;
import static kr.modusplant.infrastructure.normalization.NormalizedView.JamoPosition.STANDALONE;
import static kr.modusplant.infrastructure.normalization.NormalizedView.SourceCharacterKind.HANGUL_SYLLABLE;
import static kr.modusplant.infrastructure.normalization.NormalizedView.SourceCharacterKind.LATIN_LETTER;
import static kr.modusplant.infrastructure.normalization.NormalizedView.SourceCharacterKind.OTHER;
import static kr.modusplant.infrastructure.normalization.NormalizedView.SourceCharacterKind.STANDALONE_JAMO;
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

    private static NormalizedView keystrokeView(String original) {
        NormalizedView lightlyNormalizedView = textNormalizer.lightlyNormalize(original);
        return textNormalizer.decomposeIntoKeystrokes(
                textNormalizer.aggressivelyNormalize(lightlyNormalizedView), lightlyNormalizedView);
    }

    private static NormalizedView lineInitialView(String original) {
        return textNormalizer.extractLineInitials(textNormalizer.lightlyNormalize(original));
    }

    private static void assertUnitTags(NormalizedView normalizedView, int index,
                                       NormalizedView.SourceCharacterKind sourceCharacterKind,
                                       NormalizedView.JamoPosition firstSourceJamoPosition,
                                       NormalizedView.JamoPosition lastSourceJamoPosition,
                                       boolean endsSyllable) {
        assertThat(normalizedView.getSourceCharacterKind(index)).isEqualTo(sourceCharacterKind);
        assertThat(normalizedView.getFirstSourceJamoPosition(index)).isEqualTo(firstSourceJamoPosition);
        assertThat(normalizedView.getLastSourceJamoPosition(index)).isEqualTo(lastSourceJamoPosition);
        assertThat(normalizedView.getSyllableEnds(index)).isEqualTo(endsSyllable);
    }

    private static void assertSpans(NormalizedView normalizedView, int index,
                                    int lightlyNormalizedStart, int lightlyNormalizedEnd,
                                    int originalStart, int originalEnd) {
        assertThat(normalizedView.getLightlyNormalizedStart(index)).isEqualTo(lightlyNormalizedStart);
        assertThat(normalizedView.getLightlyNormalizedEnd(index)).isEqualTo(lightlyNormalizedEnd);
        assertThat(normalizedView.getOriginalStart(index)).isEqualTo(originalStart);
        assertThat(normalizedView.getOriginalEnd(index)).isEqualTo(originalEnd);
    }

    @Test
    @DisplayName("음절로 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenSyllables_willReturnNormalizedView() {
        // given & when
        NormalizedView view = keystrokeView("시발");

        // then
        assertThat(view.getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertUnitTags(view, 0, HANGUL_SYLLABLE, INITIAL, INITIAL, false);
        assertUnitTags(view, 1, HANGUL_SYLLABLE, MEDIAL, MEDIAL, true);
        assertUnitTags(view, 2, HANGUL_SYLLABLE, INITIAL, INITIAL, false);
        assertUnitTags(view, 3, HANGUL_SYLLABLE, MEDIAL, MEDIAL, false);
        assertUnitTags(view, 4, HANGUL_SYLLABLE, FINAL, FINAL, true);
        assertSpans(view, 4, 1, 2, 1, 2);
    }

    @Test
    @DisplayName("완전 분해된 자모로 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenFullyDecomposedJamo_willReturnNormalizedView() {
        // given & when
        NormalizedView view = keystrokeView("ㅅㅣㅂㅏㄹ");

        // then
        assertThat(view.getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertUnitTags(view, 0, STANDALONE_JAMO, INITIAL, INITIAL, false);
        assertUnitTags(view, 4, STANDALONE_JAMO, FINAL, FINAL, true);
        assertSpans(view, 0, 0, 2, 0, 2);
        assertSpans(view, 4, 2, 5, 2, 5);
    }

    @Test
    @DisplayName("음절과 자모가 섞인 문자열로 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenMixedSyllablesAndJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeView("시바ㄹ").getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(keystrokeView("씨ㅂㅏㄹ").getText()).isEqualTo("ㅆㅣㅂㅏㄹ");
        assertThat(keystrokeView("ㅂㅕㅇ신").getText()).isEqualTo("ㅂㅕㅇㅅㅣㄴ");
        assertThat(keystrokeView("ㅅㅣ발").getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
    }

    @Test
    @DisplayName("겹모음으로 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenCompoundVowel_willReturnNormalizedView() {
        // given & when
        NormalizedView view = keystrokeView("뒤져");

        // then
        assertThat(view.getText()).isEqualTo("ㄷㅜㅣㅈㅕ");
        assertThat(keystrokeView("ㄷㅜㅣㅈㅕ").getText()).isEqualTo("ㄷㅜㅣㅈㅕ");
        assertUnitTags(view, 1, HANGUL_SYLLABLE, MEDIAL, MEDIAL, false);
        assertUnitTags(view, 2, HANGUL_SYLLABLE, MEDIAL, MEDIAL, true);
        assertSpans(view, 1, 0, 1, 0, 1);
        assertSpans(view, 2, 0, 1, 0, 1);
    }

    @Test
    @DisplayName("겹받침으로 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenCompoundFinal_willReturnNormalizedView() {
        // given & when
        NormalizedView view = keystrokeView("값");

        // then
        assertThat(view.getText()).isEqualTo("ㄱㅏㅂㅅ");
        assertUnitTags(view, 2, HANGUL_SYLLABLE, FINAL, FINAL, false);
        assertUnitTags(view, 3, HANGUL_SYLLABLE, FINAL, FINAL, true);
    }

    @Test
    @DisplayName("겹자모 낱자로 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenStandaloneCompoundJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeView("ㄳ").getText()).isEqualTo("ㄱㅅ");
        assertThat(keystrokeView("ㅟ").getText()).isEqualTo("ㅜㅣ");
    }

    @Test
    @DisplayName("된소리로 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenTenseConsonant_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeView("까").getText()).isEqualTo("ㄲㅏ");
        assertThat(keystrokeView("ㅆㅂ").getText()).isEqualTo("ㅆㅂ");
    }

    @Test
    @DisplayName("결합되지 않은 낱자로 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenUncomposedJamo_willReturnNormalizedView() {
        // given & when
        NormalizedView view = keystrokeView("ㅅㅂ");

        // then
        assertThat(view.getText()).isEqualTo("ㅅㅂ");
        assertUnitTags(view, 0, STANDALONE_JAMO, STANDALONE, STANDALONE, false);
        assertUnitTags(view, 1, STANDALONE_JAMO, STANDALONE, STANDALONE, false);
    }

    @Test
    @DisplayName("영문 자판 입력으로 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenLatinLetters_willReturnNormalizedView() {
        // given & when
        NormalizedView view = keystrokeView("tlqkf");

        // then
        assertThat(view.getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertUnitTags(view, 0, LATIN_LETTER, STANDALONE, STANDALONE, false);
        assertUnitTags(view, 4, LATIN_LETTER, STANDALONE, STANDALONE, false);
        assertThat(keystrokeView("qudtls").getText()).isEqualTo("ㅂㅕㅇㅅㅣㄴ");
        assertThat(keystrokeView("tl발").getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(keystrokeView("ㅅl발").getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(keystrokeView("시l발").getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
    }

    @Test
    @DisplayName("낱자로 입력한 음절과 음절 입력으로 서로 다른 출처 종류의 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenJamoTypedAndSyllableTypedWord_willReturnNormalizedView() {
        // given & when
        NormalizedView jamoTypedView = keystrokeView("ㄹㅕㅊㅏ");
        NormalizedView syllableTypedView = keystrokeView("려차");

        // then
        assertThat(jamoTypedView.getText()).isEqualTo("ㄹㅕㅊㅏ");
        assertThat(syllableTypedView.getText()).isEqualTo("ㄹㅕㅊㅏ");
        for (int index = 0; index < 4; index++) {
            assertThat(jamoTypedView.getSourceCharacterKind(index)).isEqualTo(STANDALONE_JAMO);
            assertThat(syllableTypedView.getSourceCharacterKind(index)).isEqualTo(HANGUL_SYLLABLE);
        }
    }

    @Test
    @DisplayName("끝소리 자모가 있는 단어와 낱자로 입력한 같은 단어로 같은 자모 위치의 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenWordWithFinal_willReturnNormalizedView() {
        // given & when
        NormalizedView syllableTypedView = keystrokeView("맛보다");
        NormalizedView jamoTypedView = keystrokeView("ㅁㅏㅅㅂㅗㄷㅏ");

        // then
        assertThat(syllableTypedView.getText()).isEqualTo("ㅁㅏㅅㅂㅗㄷㅏ");
        assertThat(jamoTypedView.getText()).isEqualTo("ㅁㅏㅅㅂㅗㄷㅏ");
        assertUnitTags(syllableTypedView, 2, HANGUL_SYLLABLE, FINAL, FINAL, true);
        assertUnitTags(jamoTypedView, 2, STANDALONE_JAMO, FINAL, FINAL, true);
    }

    @Test
    @DisplayName("연속한 같은 자모로 병합된 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenIdenticalJamoRun_willReturnNormalizedView() {
        // given & when
        NormalizedView view = keystrokeView("존나");

        // then
        assertThat(view.getText()).isEqualTo("ㅈㅗㄴㅏ");
        assertThat(keystrokeView("whssk").getText()).isEqualTo("ㅈㅗㄴㅏ");
        assertUnitTags(view, 2, HANGUL_SYLLABLE, FINAL, INITIAL, false);
        assertSpans(view, 2, 0, 2, 0, 2);
    }

    @Test
    @DisplayName("출처 종류가 다른 같은 자모로 첫 출처 종류의 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenIdenticalJamoOfDifferentKinds_willReturnNormalizedView() {
        // given & when
        NormalizedView view = keystrokeView("발f");

        // then
        assertThat(view.getText()).isEqualTo("ㅂㅏㄹ");
        assertUnitTags(view, 2, HANGUL_SYLLABLE, FINAL, STANDALONE, false);
    }

    @Test
    @DisplayName("합성 또는 흡수된 음절로 시작 문자에 따른 출처 종류의 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenComposedOrAbsorbedSyllable_willReturnNormalizedView() {
        // given & when
        NormalizedView noiseSeparatedJamoView = keystrokeView("ㄹ.ㅕ");
        NormalizedView absorbedJamoView = keystrokeView("시ㅣ발");

        // then
        assertThat(noiseSeparatedJamoView.getText()).isEqualTo("ㄹㅕ");
        assertUnitTags(noiseSeparatedJamoView, 0, STANDALONE_JAMO, INITIAL, INITIAL, false);
        assertSpans(noiseSeparatedJamoView, 0, 0, 3, 0, 3);
        assertThat(absorbedJamoView.getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertUnitTags(absorbedJamoView, 0, HANGUL_SYLLABLE, INITIAL, INITIAL, false);
    }

    @Test
    @DisplayName("한자와 옛한글로 그대로 유지된 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenOtherCharacters_willReturnNormalizedView() {
        // given & when
        NormalizedView hanjaView = keystrokeView("漢");
        NormalizedView supplementaryHanjaView = keystrokeView("𠀀");

        // then
        assertThat(hanjaView.getText()).isEqualTo("漢");
        assertUnitTags(hanjaView, 0, OTHER, STANDALONE, STANDALONE, false);
        assertThat(keystrokeView("ᄓ").getText()).isEqualTo("ᄓ");
        assertThat(supplementaryHanjaView.getText()).isEqualTo("𠀀");
        assertSpans(supplementaryHanjaView, 1, 0, 2, 0, 2);
    }

    @Test
    @DisplayName("빈 문자열로 빈 키 입력 뷰 반환")
    void testDecomposeIntoKeystrokes_givenEmptyView_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeView("").length()).isZero();
    }

    @Test
    @DisplayName("여러 행으로 행 머리 뷰 반환")
    void testExtractLineInitials_givenMultipleLines_willReturnNormalizedView() {
        // given & when
        NormalizedView view = lineInitialView("시원한\n발코니");

        // then
        assertThat(view.getText()).isEqualTo("시발");
        assertSpans(view, 0, 0, 1, 0, 1);
        assertSpans(view, 1, 4, 5, 4, 5);
    }

    @Test
    @DisplayName("글머리표와 들여쓰기가 있는 행으로 행 머리 뷰 반환")
    void testExtractLineInitials_givenLinesWithBulletsAndIndents_willReturnNormalizedView() {
        // given & when
        NormalizedView view = lineInitialView("- 시원\n- 발코니");

        // then
        assertThat(view.getText()).isEqualTo("시발");
        assertSpans(view, 0, 2, 3, 2, 3);
        assertSpans(view, 1, 7, 8, 7, 8);
        assertThat(lineInitialView("😀시원\n  1. 발코니").getText()).isEqualTo("시발");
    }

    @Test
    @DisplayName("다양한 줄 종결 문자로 행 머리 뷰 반환")
    void testExtractLineInitials_givenVariousLineTerminators_willReturnNormalizedView() {
        // given & when & then
        assertThat(lineInitialView("시원\r\n발코니").getText()).isEqualTo("시발");
        assertThat(lineInitialView("시원 발코니").getText()).isEqualTo("시발");
    }

    @Test
    @DisplayName("문자가 없는 행 사이의 행으로 행 머리 뷰 반환")
    void testExtractLineInitials_givenLinesWithoutLetters_willReturnNormalizedView() {
        // given & when & then
        assertThat(lineInitialView("시원\n\n123 !!\n발코니").getText()).isEqualTo("시발");
    }

    @Test
    @DisplayName("보충 평면 문자로 시작하는 행으로 서로게이트 쌍이 유지된 행 머리 뷰 반환")
    void testExtractLineInitials_givenSupplementaryLineInitial_willReturnNormalizedView() {
        // given & when
        NormalizedView view = lineInitialView("𠀀원\n발코니");

        // then
        assertThat(view.getText()).isEqualTo("𠀀발");
        assertSpans(view, 1, 0, 2, 0, 2);
        assertSpans(view, 2, 4, 5, 4, 5);
    }

    @Test
    @DisplayName("기여한 행이 2개 미만인 문자열로 빈 행 머리 뷰 반환")
    void testExtractLineInitials_givenFewerThanTwoContributingLines_willReturnNormalizedView() {
        // given & when & then
        assertThat(lineInitialView("시원한 발코니").length()).isZero();
        assertThat(lineInitialView("시원\n\n123 !!\n").length()).isZero();
        assertThat(lineInitialView("").length()).isZero();
    }

    @Test
    @DisplayName("전각 및 호환 문자로 정규화 뷰 반환")
    void testLightlyNormalize_givenFullWidthCharacters_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("시１발")).isEqualTo("시1발");
        assertThat(lightlyNormalizedText("ｓｉｂａｌ")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("수학 및 원문자 영숫자로 정규화 뷰 반환")
    void testLightlyNormalize_givenMathematicalAndEnclosedAlphanumerics_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("𝐬𝐢𝐛𝐚𝐥")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("ⓢⓘⓑⓐⓛ")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("호환 분해가 없는 문자형 기호로 정규화 뷰 반환")
    void testLightlyNormalize_givenLetterLikeSymbols_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("🇸🇮🇧🇦🇱")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("🅢🅘🅑🅐🅛")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("🆂🅸🅱🅰🅻")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("반각 및 원문자 한글로 정규화 뷰 반환")
    void testLightlyNormalize_givenHalfWidthAndEnclosedHangul_willReturnNormalizedView() {
        // given & when & then
        // 반각 ㄱ, 반각 ㅅ, 반각 ㅂ
        assertThat(lightlyNormalizedText("ﾡ")).isEqualTo("ㄱ");
        assertThat(lightlyNormalizedText("ﾵﾲ")).isEqualTo("ㅅㅂ");
        assertThat(lightlyNormalizedText("㉦㉥")).isEqualTo("ㅅㅂ");
        assertThat(lightlyNormalizedText("㉮")).isEqualTo("가");
    }

    @Test
    @DisplayName("조합형 자모 입력으로 정규화 뷰 반환")
    void testLightlyNormalize_givenConjoiningJamo_willReturnNormalizedView() {
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
    void testLightlyNormalize_givenArchaicJamoWithoutCompatibilityForm_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("ᄓ")).isEqualTo("ᄓ");
        assertThat(lightlyNormalizedText("ꥠ")).isEqualTo("ꥠ");
    }

    @Test
    @DisplayName("보이지 않는 문자 삽입으로 정규화 뷰 반환")
    void testLightlyNormalize_givenInvisibleCharacters_willReturnNormalizedView() {
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
    void testLightlyNormalize_givenHangulFillers_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("시ㅤ발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시ᅟ발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시ᅠ발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시ﾠ발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("결합 문자 부착으로 정규화 뷰 반환")
    void testLightlyNormalize_givenCombiningMarks_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("시̈발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("s̈ibal")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("śíbàl")).isEqualTo("sibal");
        // 한글 방점(공백 결합 문자)
        assertThat(lightlyNormalizedText("시〮발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("대소문자 혼용으로 정규화 뷰 반환")
    void testLightlyNormalize_givenMixedCase_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("SiBaL")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("QUDTLS")).isEqualTo("qudtls");
        // 소문자 변환 시 길이가 바뀌는 문자도 결합 문자 제거 후 한 단위로 유지
        assertThat(lightlyNormalizedText("İ")).isEqualTo("i");
    }

    @Test
    @DisplayName("공백과 문장 부호로 정규화 뷰 반환")
    void testLightlyNormalize_givenWhitespaceAndPunctuation_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("시 발.")).isEqualTo("시 발.");
    }

    @Test
    @DisplayName("보이지 않는 문자만 있는 문자열로 정규화 뷰 반환")
    void testLightlyNormalize_givenOnlyInvisibleCharacters_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.lightlyNormalize("​ㅤ﻿󠁡");

        // then
        assertThat(normalizedView.getText()).isEmpty();
        assertThat(normalizedView.length()).isZero();
    }

    @Test
    @DisplayName("빈 문자열로 정규화 뷰 반환")
    void testLightlyNormalize_givenEmptyString_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.lightlyNormalize("");

        // then
        assertThat(normalizedView.length()).isZero();
    }

    @Test
    @DisplayName("서로게이트 쌍으로 정규화 뷰 반환")
    void testLightlyNormalize_givenSurrogatePair_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.lightlyNormalize("𝐬i");

        // then
        assertThat(normalizedView.getText()).isEqualTo("si");
        assertSpans(normalizedView, 0, 0, 1, 0, 2);
        assertSpans(normalizedView, 1, 1, 2, 2, 3);
    }

    @Test
    @DisplayName("조합형 자모 군집으로 정규화 뷰 반환")
    void testLightlyNormalize_givenConjoiningJamoCluster_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.lightlyNormalize("시발!");

        // then
        assertThat(normalizedView.getText()).isEqualTo("시발!");
        assertSpans(normalizedView, 0, 0, 1, 0, 2);
        assertSpans(normalizedView, 1, 1, 2, 2, 5);
        assertSpans(normalizedView, 2, 2, 3, 5, 6);
    }

    @Test
    @DisplayName("지역 표시 기호 쌍으로 정규화 뷰 반환")
    void testLightlyNormalize_givenRegionalIndicatorPair_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.lightlyNormalize("🇸🇮🇧");

        // then
        assertThat(normalizedView.getText()).isEqualTo("sib");
        assertSpans(normalizedView, 0, 0, 2, 0, 4);
        assertSpans(normalizedView, 1, 0, 2, 0, 4);
        assertSpans(normalizedView, 2, 2, 3, 4, 6);
    }

    @Test
    @DisplayName("결합 문자와 보이지 않는 문자가 붙은 군집으로 정규화 뷰 반환")
    void testLightlyNormalize_givenClusterWithMarkAndInvisibleCharacter_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.lightlyNormalize("s̈i시‍발");

        // then
        assertThat(normalizedView.getText()).isEqualTo("si시발");
        assertSpans(normalizedView, 0, 0, 1, 0, 2);
        assertSpans(normalizedView, 1, 1, 2, 2, 3);
        assertSpans(normalizedView, 2, 2, 3, 3, 5);
        assertSpans(normalizedView, 3, 3, 4, 5, 6);
    }

    @Test
    @DisplayName("여러 단위로 펼쳐지는 문자로 정규화 뷰 반환")
    void testLightlyNormalize_givenCharacterExpandingToSeveralUnits_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.lightlyNormalize("ﬁ시");

        // then
        assertThat(normalizedView.getText()).isEqualTo("fi시");
        assertSpans(normalizedView, 0, 0, 2, 0, 1);
        assertSpans(normalizedView, 1, 0, 2, 0, 1);
        assertSpans(normalizedView, 2, 2, 3, 1, 2);
    }

    @Test
    @DisplayName("보이지 않는 문자 사이의 단위로 정규화 뷰 반환")
    void testLightlyNormalize_givenUnitsAroundRemovedCharacter_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.lightlyNormalize("시​발");

        // then
        assertThat(normalizedView.getText()).isEqualTo("시발");
        assertSpans(normalizedView, 0, 0, 1, 0, 1);
        assertSpans(normalizedView, 1, 1, 2, 2, 3);
    }

    @Test
    @DisplayName("공백과 줄바꿈 삽입으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenWhitespaceInsertion_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시 발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시\n발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시\t발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("f u c k")).isEqualTo("fuck");
    }

    @Test
    @DisplayName("문장 부호와 기호 삽입으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenPunctuationAndSymbolInsertion_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시.발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시*발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("**시**발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시~~발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("숫자와 이모지 삽입으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenDigitAndEmojiInsertion_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("씨1발")).isEqualTo("씨발");
        assertThat(aggressivelyNormalizedText("시2발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시🌱발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("잡음 자모 삽입으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenNoiseJamoInsertion_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시ㅋ발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시ㅠ발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시ㅋㅎ발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("음절 사이가 아닌 잡음 자모로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenNoiseJamoOutsideSyllables_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅㅋ")).isEqualTo("ㅅㅋ");
        assertThat(aggressivelyNormalizedText("시ㅋ")).isEqualTo("시ㅋ");
        assertThat(aggressivelyNormalizedText("ㅋ발")).isEqualTo("ㅋ발");
    }

    @Test
    @DisplayName("음절로 나뉘는 자모 연속으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenComposableCompatibilityJamoRun_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅂㅕㅇ신")).isEqualTo("병신");
        assertThat(aggressivelyNormalizedText("ㅅㅣ발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("ㅅㅣㅂㅏㄹ")).isEqualTo("시발");
    }

    @Test
    @DisplayName("음절로 나뉘지 않는 자모 연속으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenNonComposableCompatibilityJamoRun_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅㅂ")).isEqualTo("ㅅㅂ");
        assertThat(aggressivelyNormalizedText("ㅋㅋ")).isEqualTo("ㅋ");
        assertThat(aggressivelyNormalizedText("시바ㄹ")).isEqualTo("시바ㄹ");
        assertThat(aggressivelyNormalizedText("ㅅㅂㅠㅠ")).isEqualTo("ㅅㅂㅠ");
        assertThat(aggressivelyNormalizedText("ㄷㅜㅣㅈㅕ")).isEqualTo("ㄷㅜㅣㅈㅕ");
    }

    @Test
    @DisplayName("결합 후 모음 늘이기가 있는 자모 연속으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenComposedJamoWithElongation_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅㅣ이발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("한글 사이의 문자와 숫자 및 기호로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenLettersAndNoiseNextToHangul_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅl발")).isEqualTo("ㅅl발");
        assertThat(aggressivelyNormalizedText("시l발")).isEqualTo("시l발");
        assertThat(aggressivelyNormalizedText("人l발")).isEqualTo("人l발");
        assertThat(aggressivelyNormalizedText("ㅅ|발")).isEqualTo("ㅅ발");
        assertThat(aggressivelyNormalizedText("시 1 발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("라틴 문자와 혼동하기 쉬운 키릴 문자로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenCrossScriptConfusables_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ѕіbаl")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("라틴 문자 사이의 숫자와 기호로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenNoiseInLatinWord_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("s1bal")).isEqualTo("sbal");
        assertThat(aggressivelyNormalizedText("$ibal")).isEqualTo("ibal");
        assertThat(aggressivelyNormalizedText("5hit")).isEqualTo("hit");
    }

    @Test
    @DisplayName("라틴 문자와 숫자 및 기호만으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenLatinLettersOrOnlyNoise_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("tl발")).isEqualTo("tl발");
        assertThat(aggressivelyNormalizedText("helo")).isEqualTo("helo");
        assertThat(aggressivelyNormalizedText("1^0")).isEmpty();
    }

    @Test
    @DisplayName("문자 반복으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenRepeatedCharacters_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시발발발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("ssiiibal")).isEqualTo("sibal");
        assertThat(aggressivelyNormalizedText("fuuuck")).isEqualTo("fuck");
    }

    @Test
    @DisplayName("모음 늘이기로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenElongatedVowels_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시이발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시이이발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("씨ㅣ발")).isEqualTo("씨발");
        // 뒤 음절의 받침을 앞 음절로 옮김
        assertThat(aggressivelyNormalizedText("씨이바알")).isEqualTo("씨발");
    }

    @Test
    @DisplayName("늘인 모음이 아닌 음절로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenSyllablesWithoutElongation_willReturnNormalizedView() {
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
    void testAggressivelyNormalize_givenUnitsAroundRemovedSeparator_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize("시 발"));

        // then
        assertThat(normalizedView.getText()).isEqualTo("시발");
        assertSpans(normalizedView, 0, 0, 1, 0, 1);
        assertSpans(normalizedView, 1, 2, 3, 2, 3);
    }

    @Test
    @DisplayName("결합된 자모로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenComposedJamo_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize("ㅂㅕㅇ신"));

        // then
        assertThat(normalizedView.getText()).isEqualTo("병신");
        assertSpans(normalizedView, 0, 0, 3, 0, 3);
        assertSpans(normalizedView, 1, 3, 4, 3, 4);
    }

    @Test
    @DisplayName("접힌 반복 문자로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenCollapsedRepeats_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize("시발발발"));

        // then
        assertThat(normalizedView.getText()).isEqualTo("시발");
        assertSpans(normalizedView, 0, 0, 1, 0, 1);
        assertSpans(normalizedView, 1, 1, 4, 1, 4);
    }

    @Test
    @DisplayName("병합된 모음으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenMergedVowels_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize("씨이바알"));

        // then
        assertThat(normalizedView.getText()).isEqualTo("씨발");
        assertSpans(normalizedView, 0, 0, 2, 0, 2);
        assertSpans(normalizedView, 1, 2, 4, 2, 4);
    }

    @Test
    @DisplayName("서로게이트 쌍으로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenSurrogatePairs_willReturnNormalizedView() {
        // given
        String repeatedSupplementaryCharacter = "𠀀𠀀";
        String differentSupplementaryCharacters = "𠀀𠀁";

        // when
        NormalizedView collapsedView =
                textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize(repeatedSupplementaryCharacter));

        // then
        assertThat(collapsedView.getText()).isEqualTo("𠀀");
        assertSpans(collapsedView, 0, 0, 4, 0, 4);
        assertSpans(collapsedView, 1, 0, 4, 0, 4);
        assertThat(aggressivelyNormalizedText(differentSupplementaryCharacters))
                .isEqualTo(differentSupplementaryCharacters);
    }

    @Test
    @DisplayName("빈 문자열로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenEmptyString_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize(""));

        // then
        assertThat(normalizedView.length()).isZero();
    }

    @Test
    @DisplayName("구분 문자만 있는 문자열로 정규화 뷰 반환")
    void testAggressivelyNormalize_givenOnlySeparators_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = textNormalizer.aggressivelyNormalize(textNormalizer.lightlyNormalize(" .,\n🌱"));

        // then
        assertThat(normalizedView.length()).isZero();
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
