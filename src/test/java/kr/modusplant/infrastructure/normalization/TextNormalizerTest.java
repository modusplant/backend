package kr.modusplant.infrastructure.normalization;

import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import kr.modusplant.infrastructure.normalization.view.LightlyNormalizedView;
import kr.modusplant.infrastructure.normalization.view.supers.TextNormalizedView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextNormalizerTest {
    private static final Icu4jConfig icu4jConfig = new Icu4jConfig();
    private static final TextNormalizer textNormalizer =
            new TextNormalizer(icu4jConfig.nfdNormalizer(), icu4jConfig.nfcNormalizer(),
                    icu4jConfig.nfkdNormalizer(), icu4jConfig.nfkcNormalizer());

    private static String lightlyNormalizedText(String original) {
        return textNormalizer.normalizeLightly(original).getText();
    }

    private static String aggressivelyNormalizedText(String original) {
        return textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly(original)).getText();
    }

    private static TextNormalizedView keystrokeView(String original) {
        return textNormalizer.normalizeWithKeystroke(
                textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly(original)));
    }

    private static String keystrokeText(String original) {
        return keystrokeView(original).getText();
    }

    private static TextNormalizedView syllableView(String original) {
        LightlyNormalizedView lightlyNormalizedView = textNormalizer.normalizeLightly(original);
        return textNormalizer.normalizeWithSyllable(
                textNormalizer.normalizeAggressively(lightlyNormalizedView), lightlyNormalizedView);
    }

    private static String syllableText(String original) {
        return syllableView(original).getText();
    }

    private static TextNormalizedView lineInitialView(String original) {
        return textNormalizer.normalizeWithLineInitial(textNormalizer.normalizeLightly(original));
    }

    private static void assertRanges(TextNormalizedView textNormalizedView, int index,
                                     int lightlyNormalizedStart, int lightlyNormalizedEnd,
                                     int originalStart, int originalEnd) {
        assertThat(textNormalizedView.getLightlyNormalizedStart(index)).isEqualTo(lightlyNormalizedStart);
        assertThat(textNormalizedView.getLightlyNormalizedEnd(index)).isEqualTo(lightlyNormalizedEnd);
        assertThat(textNormalizedView.getOriginalStart(index)).isEqualTo(originalStart);
        assertThat(textNormalizedView.getOriginalEnd(index)).isEqualTo(originalEnd);
    }

    // 모든 정규화 단계를 거치는 난독화 문자열
    private static final String[] OBFUSCATED_TEXTS = {
            "시 발 놈", "ㅅㅣㅂㅂㅏㄹ", "ㅂㅕㅇ신", "바알", "존ㄴ맛", "시ㅂ발", "ㅅㅣ이발", "시바ㄹ놈", "ㄷㅜㅔ져",
            "시ㅠ발 ok 시 ㅋ 발", "tlllqkf", "dnjs", "whssk", "ㅅl발", "𝐬ﬁ​시é발 🖕", "ㄱㅏㄳㅣ ㅂㅏㄺ",
            "- 시원\n- 발코니", "𝐬ㅣ\r\n바알 ㅅㅂ\n놈"
    };

    // ProhibitedWordEngine은 구간을 첫 유닛의 시작과 마지막 유닛의 끝으로 계산하므로, 네 범위 배열이 단조 비감소여야 함
    private static void assertRangesNonDecreasing(TextNormalizedView textNormalizedView) {
        for (int index = 1; index < textNormalizedView.length(); index++) {
            assertThat(textNormalizedView.getLightlyNormalizedStart(index))
                    .isGreaterThanOrEqualTo(textNormalizedView.getLightlyNormalizedStart(index - 1));
            assertThat(textNormalizedView.getLightlyNormalizedEnd(index))
                    .isGreaterThanOrEqualTo(textNormalizedView.getLightlyNormalizedEnd(index - 1));
            assertThat(textNormalizedView.getOriginalStart(index))
                    .isGreaterThanOrEqualTo(textNormalizedView.getOriginalStart(index - 1));
            assertThat(textNormalizedView.getOriginalEnd(index))
                    .isGreaterThanOrEqualTo(textNormalizedView.getOriginalEnd(index - 1));
        }
    }

    @Test
    @DisplayName("난독화 문자열로 정규화 뷰 반환")
    void testNormalizeLightly_givenObfuscatedTexts_willReturnNormalizedView() {
        for (String original : OBFUSCATED_TEXTS) {
            // given & when
            TextNormalizedView view = textNormalizer.normalizeLightly(original);

            // then
            assertRangesNonDecreasing(view);
        }
    }

    @Test
    @DisplayName("난독화 문자열로 정규화 뷰 반환")
    void testNormalizeAggressively_givenObfuscatedTexts_willReturnNormalizedView() {
        for (String original : OBFUSCATED_TEXTS) {
            // given & when
            TextNormalizedView view = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly(original));

            // then
            assertRangesNonDecreasing(view);
        }
    }

    @Test
    @DisplayName("난독화 문자열로 음절 뷰 반환")
    void testNormalizeWithSyllable_givenObfuscatedTexts_willReturnNormalizedView() {
        for (String original : OBFUSCATED_TEXTS) {
            // given & when
            TextNormalizedView view = syllableView(original);

            // then
            assertRangesNonDecreasing(view);
        }
    }

    @Test
    @DisplayName("난독화 문자열로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenObfuscatedTexts_willReturnNormalizedView() {
        for (String original : OBFUSCATED_TEXTS) {
            // given & when
            TextNormalizedView view = keystrokeView(original);

            // then
            assertRangesNonDecreasing(view);
        }
    }

    @Test
    @DisplayName("난독화 문자열로 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenObfuscatedTexts_willReturnNormalizedView() {
        for (String original : OBFUSCATED_TEXTS) {
            // given & when
            TextNormalizedView view = lineInitialView(original);

            // then
            assertRangesNonDecreasing(view);
        }
    }

    @Test
    @DisplayName("음절로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenSyllables_willReturnNormalizedView() {
        // given & when
        TextNormalizedView view = keystrokeView("시발");

        // then
        assertThat(view.getText()).isEqualTo("시발");
        assertRanges(view, 0, 0, 1, 0, 1);
        assertRanges(view, 1, 1, 2, 1, 2);
    }

    @Test
    @DisplayName("낱자와 음절이 섞인 문자열로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenMixedSyllablesAndJamo_willReturnNormalizedView() {
        // given
        TextNormalizedView jamoView = keystrokeView("ㅅㅣㅂㅏㄹ");

        // when & then
        assertThat(jamoView.getText()).isEqualTo("시발");
        assertRanges(jamoView, 0, 0, 2, 0, 2);
        assertRanges(jamoView, 1, 2, 5, 2, 5);
        assertThat(keystrokeText("씨ㅂㅏㄹ")).isEqualTo("씨발");
        assertThat(keystrokeText("ㅂㅕㅇ신")).isEqualTo("병신");
        assertThat(keystrokeText("ㅅㅣ발")).isEqualTo("시발");
        assertThat(keystrokeText("ㄷㅜㅣㅈㅕ")).isEqualTo("ㄷㅜㅣㅈㅕ");
        assertThat(keystrokeText("뒤져")).isEqualTo("뒤져");
        assertThat(keystrokeText("값")).isEqualTo("값");
        assertThat(keystrokeText("까")).isEqualTo("까");
    }

    @Test
    @DisplayName("결합되지 않은 낱자로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenUncomposedJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("ㅅㅂ")).isEqualTo("ㅅㅂ");
        assertThat(keystrokeText("ㅆㅂ")).isEqualTo("ㅆㅂ");
        assertThat(keystrokeText("ㄳ")).isEqualTo("ㄳ");
        assertThat(keystrokeText("ㅟ")).isEqualTo("ㅟ");
        assertThat(keystrokeText("tq")).isEqualTo("ㅅㅂ");
        assertThat(keystrokeText("시ㅈ발")).isEqualTo("시ㅈ발");
        assertThat(keystrokeText("시바ㄹ")).isEqualTo("시바ㄹ");
    }

    @Test
    @DisplayName("영문 자판 입력으로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenAsciiLowerCaseLetters_willReturnNormalizedView() {
        // given
        TextNormalizedView view = keystrokeView("tlqkf");

        // when & then
        assertThat(view.getText()).isEqualTo("시발");
        assertRanges(view, 0, 0, 2, 0, 2);
        assertRanges(view, 1, 2, 5, 2, 5);
        assertThat(keystrokeText("qudtls")).isEqualTo("병신");
        assertThat(keystrokeText("시qkf")).isEqualTo("시발");
        assertThat(keystrokeText("tl발")).isEqualTo("시발");
        assertThat(keystrokeText("ㅅl발")).isEqualTo("시발");
        assertThat(keystrokeText("tlllqkf")).isEqualTo("시발");
        assertThat(keystrokeText("시l발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("두 키로 이루는 겹모음과 겹종성의 영문 자판 입력으로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenAsciiLowerCaseLettersOfCompoundJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("dnjs")).isEqualTo("원");
        assertThat(keystrokeText("rkqt")).isEqualTo("값");
    }

    @Test
    @DisplayName("뒤에 모음이 오는 영문 자판 자음으로 초성으로 남은 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenAsciiLowerCaseConsonantBeforeVowel_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("rkqtk")).isEqualTo("갑사");
        assertThat(keystrokeText("tlqㅏㄹ")).isEqualTo("시발");
        assertThat(keystrokeText("tlq kf")).isEqualTo("시발");
    }

    @Test
    @DisplayName("뒤 문자와 일부만 합쳐진 합자의 영문 자판 입력으로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenLigaturePartlyJoinedWithNextLetter_willReturnNormalizedView() {
        // given
        TextNormalizedView partlyJoinedView = keystrokeView("ﬁi");
        TextNormalizedView separateView = keystrokeView("ﬁo");

        // when & then
        assertThat(partlyJoinedView.getText()).isEqualTo("랴");
        assertRanges(partlyJoinedView, 0, 0, 3, 0, 2);
        assertThat(separateView.getText()).isEqualTo("랴ㅐ");
        assertRanges(separateView, 0, 0, 2, 0, 1);
        assertRanges(separateView, 1, 2, 3, 1, 2);
    }

    @Test
    @DisplayName("낱자로 입력한 음절과 음절 입력으로 같은 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenJamoTypedAndSyllableTypedWord_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("ㄹㅕㅊㅏ")).isEqualTo("려차");
        assertThat(keystrokeText("려차")).isEqualTo("려차");
        assertThat(keystrokeText("ㅁㅏㅅㅂㅗㄷㅏ")).isEqualTo("맛보다");
        assertThat(keystrokeText("맛보다")).isEqualTo("맛보다");
    }

    @Test
    @DisplayName("종성과 다음 초성이 같은 자모로 병합되지 않은 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenTrailingConsonantFollowedByIdenticalLeadingConsonant_willReturnNormalizedView() {
        // given
        TextNormalizedView syllableTypedView = keystrokeView("존나");
        TextNormalizedView asciiLowerCaseTypedView = keystrokeView("whssk");

        // when & then
        assertThat(syllableTypedView.getText()).isEqualTo("존나");
        assertThat(asciiLowerCaseTypedView.getText()).isEqualTo("존나");
        assertRanges(syllableTypedView, 0, 0, 1, 0, 1);
        assertRanges(asciiLowerCaseTypedView, 0, 0, 3, 0, 3);
        assertRanges(asciiLowerCaseTypedView, 1, 3, 5, 3, 5);
        assertThat(keystrokeText("구조나")).isEqualTo("구조나");
    }

    @Test
    @DisplayName("교차 문자 혼동 문자에서 바꾼 영문 자판 입력으로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenJamoConvertedFromConfusableLetters_willReturnNormalizedView() {
        // given
        TextNormalizedView confusableTypedView = keystrokeView("ѕk");

        // when & then
        assertThat(confusableTypedView.getText()).isEqualTo("나");
        assertRanges(confusableTypedView, 0, 0, 2, 0, 2);
    }

    @Test
    @DisplayName("비문자 잡음으로 떨어진 낱자와 흡수된 낱자로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenComposedOrAbsorbedSyllable_willReturnNormalizedView() {
        // given
        TextNormalizedView noiseSeparatedJamoView = keystrokeView("ㄹ.ㅕ");

        // when & then
        assertThat(noiseSeparatedJamoView.getText()).isEqualTo("려");
        assertRanges(noiseSeparatedJamoView, 0, 0, 3, 0, 3);
        assertThat(keystrokeText("시ㅣ발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("두 음절 사이 호환 자모와 ASCII 소문자로 제거된 음절 뷰 반환")
    void testNormalizeWithSyllable_givenNoiseBetweenSyllables_willReturnNormalizedView() {
        // given
        TextNormalizedView asciiNoiseView = syllableView("시x발");

        // when & then
        assertThat(asciiNoiseView.getText()).isEqualTo("시발");
        assertRanges(asciiNoiseView, 0, 0, 1, 0, 1);
        assertRanges(asciiNoiseView, 1, 2, 3, 2, 3);
        assertThat(syllableText("시l발")).isEqualTo("시발");
        assertThat(syllableText("시xx발")).isEqualTo("시발");
        assertThat(syllableText("시 ㅋ 발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("종성 없는 음절 뒤 같은 토큰의 자음 낱자를 종성으로 붙인 음절 뷰 반환")
    void testNormalizeWithSyllable_givenConsonantAfterSyllableWithoutTrailingConsonant_willReturnNormalizedView() {
        // given
        TextNormalizedView trailingConsonantAttachedView = syllableView("시바ㄹ놈");
        TextNormalizedView compoundTrailingConsonantView = syllableView("바ㄹㄱ");

        // when & then
        assertThat(trailingConsonantAttachedView.getText()).isEqualTo("시발놈");
        assertRanges(trailingConsonantAttachedView, 1, 1, 3, 1, 3);
        assertThat(syllableText("시ㅈ발")).isEqualTo("싲발");
        assertThat(compoundTrailingConsonantView.getText()).isEqualTo("밝");
        assertRanges(compoundTrailingConsonantView, 0, 0, 3, 0, 3);
    }

    @Test
    @DisplayName("모음 낱자가 뒤따르거나 다른 토큰의 자음 낱자를 붙이지 않은 음절 뷰 반환")
    void testNormalizeWithSyllable_givenConsonantNotAttachable_willReturnNormalizedView() {
        // given & when & then
        assertThat(syllableText("시바ㄹㅏ")).isEqualTo("시바라");
        assertThat(syllableText("시바 ㄹ")).isEqualTo("시바");
    }

    @Test
    @DisplayName("모음 낱자 앞 겹종성 낱자로 겹종성을 붙인 음절 뷰 반환")
    void testNormalizeWithSyllable_givenCompoundTrailingConsonantBeforeVowel_willReturnNormalizedView() {
        // given
        TextNormalizedView compoundTrailingConsonantAttachedView = syllableView("가ㄳㅣ");

        // when & then
        assertThat(syllableText("ㄱㅏㄳㅣ")).isEqualTo("갃");
        assertThat(compoundTrailingConsonantAttachedView.getText()).isEqualTo("갃");
        assertRanges(compoundTrailingConsonantAttachedView, 0, 0, 2, 0, 2);
    }

    @Test
    @DisplayName("같은 토큰의 호환 자모 연속으로 조합된 음절 뷰 반환")
    void testNormalizeWithSyllable_givenCompatibilityJamoRun_willReturnNormalizedView() {
        // given & when & then
        assertThat(syllableText("ㅅㅣㅂㅏㄹ")).isEqualTo("시발");
        assertThat(syllableText("ㅅㅣ발")).isEqualTo("시발");
        assertThat(syllableText("ㅅㅂ")).isEmpty();
    }

    @Test
    @DisplayName("토큰 경계에서 끊긴 호환 자모 연속으로 조합된 음절 뷰 반환")
    void testNormalizeWithSyllable_givenCompatibilityJamoRunAcrossTokens_willReturnNormalizedView() {
        // given
        TextNormalizedView tokenBoundedView = syllableView("ㅂㅠ ㅅㅂ");

        // when & then
        assertThat(tokenBoundedView.getText()).isEqualTo("뷰");
        assertRanges(tokenBoundedView, 0, 0, 2, 0, 2);
        assertThat(syllableText("ㅂ ㅠㅅㅂ")).isEmpty();
    }

    @Test
    @DisplayName("ASCII 소문자와 한글 음절 외 문자로 유지된 음절 뷰 반환")
    void testNormalizeWithSyllable_givenOtherCharacters_willReturnNormalizedView() {
        // given & when & then
        assertThat(syllableText("fuck")).isEmpty();
        assertThat(syllableText("漢字")).isEqualTo("漢字");
        assertThat(syllableText("시발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("빈 문자열로 빈 음절 뷰 반환")
    void testNormalizeWithSyllable_givenEmptyView_willReturnNormalizedView() {
        // given & when & then
        assertThat(syllableText("")).isEmpty();
    }

    @Test
    @DisplayName("한자와 옛한글로 그대로 유지된 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenOtherCharacters_willReturnNormalizedView() {
        // given
        TextNormalizedView supplementaryHanjaView = keystrokeView("𠀀");

        // when & then
        assertThat(keystrokeText("漢")).isEqualTo("漢");
        assertThat(keystrokeText("ᄓ")).isEqualTo("ᄓ");
        assertThat(supplementaryHanjaView.getText()).isEqualTo("𠀀");
        assertRanges(supplementaryHanjaView, 1, 0, 2, 0, 2);
    }

    @Test
    @DisplayName("빈 문자열로 빈 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenEmptyView_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("")).isEmpty();
    }

    @Test
    @DisplayName("여러 행으로 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenMultipleLines_willReturnNormalizedView() {
        // given & when
        TextNormalizedView view = lineInitialView("시원한\n발코니");

        // then
        assertThat(view.getText()).isEqualTo("시발");
        assertRanges(view, 0, 0, 1, 0, 1);
        assertRanges(view, 1, 4, 5, 4, 5);
    }

    @Test
    @DisplayName("글머리표와 들여쓰기가 있는 행으로 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenLinesWithBulletsAndIndents_willReturnNormalizedView() {
        // given & when
        TextNormalizedView view = lineInitialView("- 시원\n- 발코니");

        // then
        assertThat(view.getText()).isEqualTo("시발");
        assertRanges(view, 0, 2, 3, 2, 3);
        assertRanges(view, 1, 7, 8, 7, 8);
        assertThat(lineInitialView("😀시원\n  1. 발코니").getText()).isEqualTo("시발");
    }

    @Test
    @DisplayName("다양한 줄 종결 문자로 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenVariousLineTerminators_willReturnNormalizedView() {
        // given & when & then
        assertThat(lineInitialView("시원\r\n발코니").getText()).isEqualTo("시발");
        assertThat(lineInitialView("시원 발코니").getText()).isEqualTo("시발");
    }

    @Test
    @DisplayName("문자가 없는 행 사이의 행으로 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenLinesWithoutLetters_willReturnNormalizedView() {
        // given & when & then
        assertThat(lineInitialView("시원\n\n123 !!\n발코니").getText()).isEqualTo("시발");
    }

    @Test
    @DisplayName("사용자 정의 문자로 시작하는 행으로 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenLineStartingWithPrivateUseCharacter_willReturnNormalizedView() {
        // given & when & then
        assertThat(lineInitialView("시원\n발코니").getText()).isEqualTo("시발");
    }

    @Test
    @DisplayName("보충 평면 문자로 시작하는 행으로 서로게이트 쌍이 유지된 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenSupplementaryLineInitial_willReturnNormalizedView() {
        // given & when
        TextNormalizedView view = lineInitialView("𠀀원\n발코니");

        // then
        assertThat(view.getText()).isEqualTo("𠀀발");
        assertRanges(view, 1, 0, 2, 0, 2);
        assertRanges(view, 2, 4, 5, 4, 5);
    }

    @Test
    @DisplayName("기여한 행이 2개 미만인 문자열로 빈 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenFewerThanTwoContributingLines_willReturnNormalizedView() {
        // given & when & then
        assertThat(lineInitialView("시원한 발코니").length()).isZero();
        assertThat(lineInitialView("시원\n\n123 !!\n").length()).isZero();
        assertThat(lineInitialView("").length()).isZero();
    }

    @Test
    @DisplayName("전각 및 호환 문자로 정규화 뷰 반환")
    void testNormalizeLightly_givenFullWidthCharacters_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("시１발")).isEqualTo("시1발");
        assertThat(lightlyNormalizedText("ｓｉｂａｌ")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("수학 및 원문자 영숫자로 정규화 뷰 반환")
    void testNormalizeLightly_givenMathematicalAndEnclosedAlphanumerics_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("𝐬𝐢𝐛𝐚𝐥")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("ⓢⓘⓑⓐⓛ")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("호환 분해가 없고 라틴 문자와 혼동되는 기호로 정규화 뷰 반환")
    void testNormalizeLightly_givenSymbolConfusables_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("🇸🇮🇧🇦🇱")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("🅢🅘🅑🅐🅛")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("🆂🅸🅱🅰🅻")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("라틴 문자와 혼동하기 쉬운 키릴 및 그리스 문자로 정규화 뷰 반환")
    void testNormalizeLightly_givenCrossScriptConfusables_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("ѕіbаl")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("ЅІbАl")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("ѕіβаl")).isEqualTo("sibal");
    }

    @Test
    @DisplayName("반각 및 원문자 한글로 정규화 뷰 반환")
    void testNormalizeLightly_givenHalfWidthAndEnclosedHangul_willReturnNormalizedView() {
        // given & when & then
        // 반각 ㄱ, 반각 ㅅ, 반각 ㅂ
        assertThat(lightlyNormalizedText("ﾡ")).isEqualTo("ㄱ");
        assertThat(lightlyNormalizedText("ﾵﾲ")).isEqualTo("ㅅㅂ");
        assertThat(lightlyNormalizedText("㉦㉥")).isEqualTo("ㅅㅂ");
        assertThat(lightlyNormalizedText("㉮")).isEqualTo("가");
    }

    @Test
    @DisplayName("조합형 자모 입력으로 정규화 뷰 반환")
    void testNormalizeLightly_givenConjoiningJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("시발")).isEqualTo("시발");
        // 짝이 없는 초성
        assertThat(lightlyNormalizedText("ᄉᄇ")).isEqualTo("ㅅㅂ");
        // 짝이 없는 중성과 종성
        assertThat(lightlyNormalizedText("ᅵ ᆯ")).isEqualTo("ㅣ ㄹ");
        // 확장 블록의 옛한글 자모 중 호환 자모가 있는 경우
        assertThat(lightlyNormalizedText("ᅀ")).isEqualTo("ㅿ");
        assertThat(lightlyNormalizedText("ᆡ")).isEqualTo("ㆎ");
    }

    @Test
    @DisplayName("호환 자모가 없는 옛한글 자모로 정규화 뷰 반환")
    void testNormalizeLightly_givenArchaicJamoWithoutCompatibilityForm_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("ᄓ")).isEqualTo("ᄓ");
        assertThat(lightlyNormalizedText("ꥠ")).isEqualTo("ꥠ");
    }

    @Test
    @DisplayName("보이지 않는 문자 삽입으로 정규화 뷰 반환")
    void testNormalizeLightly_givenInvisibleCharacters_willReturnNormalizedView() {
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
    void testNormalizeLightly_givenHangulFillers_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("시ㅤ발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시ᅟ발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시ᅠ발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("시ﾠ발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("결합 문자 부착으로 정규화 뷰 반환")
    void testNormalizeLightly_givenCombiningMarks_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("시̈발")).isEqualTo("시발");
        assertThat(lightlyNormalizedText("s̈ibal")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("śíbàl")).isEqualTo("sibal");
        // 한글 방점(공백 결합 문자)
        assertThat(lightlyNormalizedText("시〮발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("대소문자 혼용으로 정규화 뷰 반환")
    void testNormalizeLightly_givenMixedCase_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("SiBaL")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("QUDTLS")).isEqualTo("qudtls");
        // 소문자 변환 시 길이가 바뀌는 문자도 결합 문자 제거 후 한 단위로 유지
        assertThat(lightlyNormalizedText("İ")).isEqualTo("i");
    }

    @Test
    @DisplayName("공백과 문장 부호로 정규화 뷰 반환")
    void testNormalizeLightly_givenWhitespaceAndPunctuation_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("시 발.")).isEqualTo("시 발.");
    }

    @Test
    @DisplayName("보이지 않는 문자만 있는 문자열로 정규화 뷰 반환")
    void testNormalizeLightly_givenOnlyInvisibleCharacters_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("​ㅤ﻿󠁡");

        // then
        assertThat(textNormalizedView.getText()).isEmpty();
        assertThat(textNormalizedView.length()).isZero();
    }

    @Test
    @DisplayName("빈 문자열로 정규화 뷰 반환")
    void testNormalizeLightly_givenEmptyString_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("");

        // then
        assertThat(textNormalizedView.length()).isZero();
    }

    @Test
    @DisplayName("서로게이트 쌍으로 정규화 뷰 반환")
    void testNormalizeLightly_givenSurrogatePair_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("𝐬i");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("si");
        assertRanges(textNormalizedView, 0, 0, 1, 0, 2);
        assertRanges(textNormalizedView, 1, 1, 2, 2, 3);
    }

    @Test
    @DisplayName("조합형 자모 클러스터로 정규화 뷰 반환")
    void testNormalizeLightly_givenConjoiningJamoCluster_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("시발!");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("시발!");
        assertRanges(textNormalizedView, 0, 0, 1, 0, 2);
        assertRanges(textNormalizedView, 1, 1, 2, 2, 5);
        assertRanges(textNormalizedView, 2, 2, 3, 5, 6);
    }

    @Test
    @DisplayName("지역 표시 기호 쌍으로 정규화 뷰 반환")
    void testNormalizeLightly_givenRegionalIndicatorPair_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("🇸🇮🇧");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("sib");
        assertRanges(textNormalizedView, 0, 0, 2, 0, 4);
        assertRanges(textNormalizedView, 1, 0, 2, 0, 4);
        assertRanges(textNormalizedView, 2, 2, 3, 4, 6);
    }

    @Test
    @DisplayName("결합 문자와 보이지 않는 문자가 붙은 클러스터로 정규화 뷰 반환")
    void testNormalizeLightly_givenClusterWithMarkAndInvisibleCharacter_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("s̈i시‍발");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("si시발");
        assertRanges(textNormalizedView, 0, 0, 1, 0, 2);
        assertRanges(textNormalizedView, 1, 1, 2, 2, 3);
        assertRanges(textNormalizedView, 2, 2, 3, 3, 5);
        assertRanges(textNormalizedView, 3, 3, 4, 5, 6);
    }

    @Test
    @DisplayName("여러 단위로 펼쳐지는 문자로 정규화 뷰 반환")
    void testNormalizeLightly_givenCharacterExpandingToSeveralUnits_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("ﬁ시");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("fi시");
        assertRanges(textNormalizedView, 0, 0, 2, 0, 1);
        assertRanges(textNormalizedView, 1, 0, 2, 0, 1);
        assertRanges(textNormalizedView, 2, 2, 3, 1, 2);
    }

    @Test
    @DisplayName("보이지 않는 문자 사이의 단위로 정규화 뷰 반환")
    void testNormalizeLightly_givenUnitsAroundRemovedCharacter_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("시​발");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("시발");
        assertRanges(textNormalizedView, 0, 0, 1, 0, 1);
        assertRanges(textNormalizedView, 1, 1, 2, 2, 3);
    }

    @Test
    @DisplayName("공백과 줄바꿈 삽입으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenWhitespaceInsertion_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시 발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시\n발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시\t발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("f u c k")).isEqualTo("fuck");
    }

    @Test
    @DisplayName("문장 부호와 기호 삽입으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenPunctuationAndSymbolInsertion_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시.발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시*발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("**시**발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시~~발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("숫자와 이모지 삽입으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenDigitAndEmojiInsertion_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("씨1발")).isEqualTo("씨발");
        assertThat(aggressivelyNormalizedText("시2발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시🌱발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("음절로 나뉘는 자모 연속으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenComposableCompatibilityJamoRun_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅂㅕㅇ신")).isEqualTo("병신");
        assertThat(aggressivelyNormalizedText("ㅅㅣ발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("ㅅㅣㅂㅏㄹ")).isEqualTo("시발");
    }

    @Test
    @DisplayName("음절로 나뉘지 않는 자모 연속으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenNonComposableCompatibilityJamoRun_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅㅂ")).isEqualTo("ㅅㅂ");
        assertThat(aggressivelyNormalizedText("ㅋㅋ")).isEqualTo("ㅋ");
        assertThat(aggressivelyNormalizedText("시바ㄹ")).isEqualTo("시바ㄹ");
        assertThat(aggressivelyNormalizedText("ㅅㅂㅠㅠ")).isEqualTo("ㅅㅂㅠ");
        assertThat(aggressivelyNormalizedText("ㄷㅜㅣㅈㅕ")).isEqualTo("ㄷㅜㅣㅈㅕ");
    }

    @Test
    @DisplayName("낱자 하나로 된 겹모음과 겹종성이 있는 자모 연속으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenSingleCompoundJamoRun_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅂㅏㄺ")).isEqualTo("밝");
        assertThat(aggressivelyNormalizedText("ㅂㅘㄹ")).isEqualTo("봘");
    }

    @Test
    @DisplayName("두 낱자로 된 겹종성이나 모음 앞 겹종성이 있는 자모 연속으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenUnjoinedOrUnsplitCompoundTrailingConsonantRun_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅂㅏㄹㄱ")).isEqualTo("ㅂㅏㄹㄱ");
        assertThat(aggressivelyNormalizedText("ㄱㅏㄳㅣ")).isEqualTo("ㄱㅏㄳㅣ");
    }

    @Test
    @DisplayName("결합 후 모음 늘이기가 있는 자모 연속으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenComposedJamoWithElongation_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅㅣ이발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("한글 사이의 문자와 숫자 및 기호로 정규화 뷰 반환")
    void testNormalizeAggressively_givenLettersAndNoiseNextToHangul_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅅl발")).isEqualTo("ㅅl발");
        assertThat(aggressivelyNormalizedText("시l발")).isEqualTo("시l발");
        assertThat(aggressivelyNormalizedText("人l발")).isEqualTo("人l발");
        assertThat(aggressivelyNormalizedText("ㅅ|발")).isEqualTo("ㅅ발");
        assertThat(aggressivelyNormalizedText("시 1 발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("라틴 문자 사이의 숫자와 기호로 정규화 뷰 반환")
    void testNormalizeAggressively_givenNoiseInLatinWord_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("s1bal")).isEqualTo("sbal");
        assertThat(aggressivelyNormalizedText("$ibal")).isEqualTo("ibal");
        assertThat(aggressivelyNormalizedText("5hit")).isEqualTo("hit");
    }

    @Test
    @DisplayName("라틴 문자와 숫자 및 기호만으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenLatinLettersOrOnlyNoise_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("tl발")).isEqualTo("tl발");
        assertThat(aggressivelyNormalizedText("helo")).isEqualTo("helo");
        assertThat(aggressivelyNormalizedText("1^0")).isEmpty();
    }

    @Test
    @DisplayName("한글 사이의 서식 문자와 사용자 정의 문자로 정규화 뷰 반환")
    void testNormalizeAggressively_givenFormatOrPrivateUseCharacterNextToHangul_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시؀발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시발")).isEqualTo("시발");
    }

    @Test
    @DisplayName("낱자 자모 반복으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenRepeatedJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ㅋㅋㅋ")).isEqualTo("ㅋ");
        assertThat(aggressivelyNormalizedText("ㅅㅣㅂㅂㅏㄹ")).isEqualTo("시발");
    }

    @Test
    @DisplayName("호환 자모가 없는 조합형 자모 반복으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenRepeatedConjoiningJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ᄓᄓᄓ")).isEqualTo("ᄓᄓᄓ");
    }

    @Test
    @DisplayName("음절과 라틴 문자 반복으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenRepeatedSyllablesOrLatinLetters_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시발발발")).isEqualTo("시발발발");
        assertThat(aggressivelyNormalizedText("ssiiibal")).isEqualTo("ssiiibal");
        assertThat(aggressivelyNormalizedText("fuuuck")).isEqualTo("fuuuck");
    }
    @Test
    @DisplayName("모음 늘이기로 정규화 뷰 반환")
    void testNormalizeAggressively_givenElongatedVowels_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시이발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시이이발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("씨ㅣ발")).isEqualTo("씨발");
        // 뒤 음절의 종성을 앞 음절로 옮김
        assertThat(aggressivelyNormalizedText("씨이바알")).isEqualTo("씨발");
    }

    @Test
    @DisplayName("늘인 모음이 아닌 음절로 정규화 뷰 반환")
    void testNormalizeAggressively_givenSyllablesWithoutElongation_willReturnNormalizedView() {
        // given & when & then
        // 모음이 다름
        assertThat(aggressivelyNormalizedText("시아")).isEqualTo("시아");
        // 앞 음절에 종성이 있음
        assertThat(aggressivelyNormalizedText("발아")).isEqualTo("발아");
        // 뒤 음절의 초성이 ㅇ이 아님
        assertThat(aggressivelyNormalizedText("시미")).isEqualTo("시미");
        // 자음 낱자가 앞 음절의 종성과도 뒤 음절의 초성과도 다름
        assertThat(aggressivelyNormalizedText("시ㄱ발")).isEqualTo("시ㄱ발");
        // 자음 낱자 뒤에 음절이 없음
        assertThat(aggressivelyNormalizedText("발ㄹ")).isEqualTo("발ㄹ");
    }

    @Test
    @DisplayName("자음 늘이기로 정규화 뷰 반환")
    void testNormalizeAggressively_givenElongatedConsonants_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly("존ㄴ나"));

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("존나");
        assertRanges(textNormalizedView, 0, 0, 2, 0, 2);
        assertRanges(textNormalizedView, 1, 2, 3, 2, 3);
        assertThat(aggressivelyNormalizedText("시ㅂ발")).isEqualTo("시발");
        // 자음 낱자가 앞 음절의 종성과만 같고 뒤 음절의 초성과는 다름
        assertThat(aggressivelyNormalizedText("존ㄴ맛")).isEqualTo("존맛");
    }

    @Test
    @DisplayName("제거된 문자 양옆의 단위로 정규화 뷰 반환")
    void testNormalizeAggressively_givenUnitsAroundRemovedSeparator_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly("시 발"));

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("시발");
        assertRanges(textNormalizedView, 0, 0, 1, 0, 1);
        assertRanges(textNormalizedView, 1, 2, 3, 2, 3);
    }

    @Test
    @DisplayName("결합된 자모로 정규화 뷰 반환")
    void testNormalizeAggressively_givenComposedJamo_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly("ㅂㅕㅇ신"));

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("병신");
        assertRanges(textNormalizedView, 0, 0, 3, 0, 3);
        assertRanges(textNormalizedView, 1, 3, 4, 3, 4);
    }

    @Test
    @DisplayName("접힌 반복 자모로 정규화 뷰 반환")
    void testNormalizeAggressively_givenCollapsedRepeats_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly("시ㅋㅋㅋ"));

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("시ㅋ");
        assertRanges(textNormalizedView, 0, 0, 1, 0, 1);
        assertRanges(textNormalizedView, 1, 1, 4, 1, 4);
    }

    @Test
    @DisplayName("병합된 모음으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenMergedVowels_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly("씨이바알"));

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("씨발");
        assertRanges(textNormalizedView, 0, 0, 2, 0, 2);
        assertRanges(textNormalizedView, 1, 2, 4, 2, 4);
    }

    @Test
    @DisplayName("서로게이트 쌍으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenSurrogatePairs_willReturnNormalizedView() {
        // given
        String repeatedSupplementaryCharacter = "𠀀𠀀";
        String differentSupplementaryCharacters = "𠀀𠀁";

        // when
        TextNormalizedView repeatedView =
                textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly(repeatedSupplementaryCharacter));

        // then
        assertThat(repeatedView.getText()).isEqualTo(repeatedSupplementaryCharacter);
        assertRanges(repeatedView, 0, 0, 2, 0, 2);
        assertRanges(repeatedView, 1, 0, 2, 0, 2);
        assertRanges(repeatedView, 2, 2, 4, 2, 4);
        assertRanges(repeatedView, 3, 2, 4, 2, 4);
        assertThat(aggressivelyNormalizedText(differentSupplementaryCharacters))
                .isEqualTo(differentSupplementaryCharacters);
    }

    @Test
    @DisplayName("빈 문자열로 정규화 뷰 반환")
    void testNormalizeAggressively_givenEmptyString_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly(""));

        // then
        assertThat(textNormalizedView.length()).isZero();
    }

    @Test
    @DisplayName("구분 문자만 있는 문자열로 정규화 뷰 반환")
    void testNormalizeAggressively_givenOnlySeparators_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly(" .,\n🌱"));

        // then
        assertThat(textNormalizedView.length()).isZero();
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
