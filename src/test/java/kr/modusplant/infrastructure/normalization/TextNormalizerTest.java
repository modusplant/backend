package kr.modusplant.infrastructure.normalization;

import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.JONGSEONG;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.CHOSEONG;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.JUNGSEONG;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.STANDALONE;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.SourceKind.HANGUL_SYLLABLE;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.SourceKind.ASCII_LOWER_CASE_LETTER;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.SourceKind.OTHER;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.SourceKind.STANDALONE_JAMO;
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

    private static TextNormalizedKeystrokeView keystrokeView(String original) {
        TextNormalizedView lightlyNormalizedView = textNormalizer.normalizeLightly(original);
        return textNormalizer.normalizeWithKeystroke(
                textNormalizer.normalizeAggressively(lightlyNormalizedView), lightlyNormalizedView);
    }

    private static String keystrokeText(String original) {
        return keystrokeView(original).getTextNormalizedView().getText();
    }

    private static TextNormalizedView lineInitialView(String original) {
        return textNormalizer.normalizeWithLineInitial(textNormalizer.normalizeLightly(original));
    }

    private static void assertUnitTags(TextNormalizedKeystrokeView keystrokeView, int index,
                                       TextNormalizedKeystrokeView.SourceKind sourceKind,
                                       TextNormalizedKeystrokeView.JamoPosition firstSourceJamoPosition,
                                       TextNormalizedKeystrokeView.JamoPosition lastSourceJamoPosition,
                                       boolean syllableEnd) {
        assertThat(keystrokeView.getSourceKind(index)).isEqualTo(sourceKind);
        assertThat(keystrokeView.getFirstSourceJamoPosition(index)).isEqualTo(firstSourceJamoPosition);
        assertThat(keystrokeView.getLastSourceJamoPosition(index)).isEqualTo(lastSourceJamoPosition);
        assertThat(keystrokeView.isSyllableEnd(index)).isEqualTo(syllableEnd);
    }

    private static void assertSpans(TextNormalizedView textNormalizedView, int index,
                                    int lightlyNormalizedStart, int lightlyNormalizedEnd,
                                    int originalStart, int originalEnd) {
        assertThat(textNormalizedView.getLightlyNormalizedStart(index)).isEqualTo(lightlyNormalizedStart);
        assertThat(textNormalizedView.getLightlyNormalizedEnd(index)).isEqualTo(lightlyNormalizedEnd);
        assertThat(textNormalizedView.getOriginalStart(index)).isEqualTo(originalStart);
        assertThat(textNormalizedView.getOriginalEnd(index)).isEqualTo(originalEnd);
    }

    @Test
    @DisplayName("음절로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenSyllables_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView view = keystrokeView("시발");

        // then
        assertThat(view.getTextNormalizedView().getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertUnitTags(view, 0, HANGUL_SYLLABLE, CHOSEONG, CHOSEONG, false);
        assertUnitTags(view, 1, HANGUL_SYLLABLE, JUNGSEONG, JUNGSEONG, true);
        assertUnitTags(view, 2, HANGUL_SYLLABLE, CHOSEONG, CHOSEONG, false);
        assertUnitTags(view, 3, HANGUL_SYLLABLE, JUNGSEONG, JUNGSEONG, false);
        assertUnitTags(view, 4, HANGUL_SYLLABLE, JONGSEONG, JONGSEONG, true);
        assertSpans(view.getTextNormalizedView(), 4, 1, 2, 1, 2);
    }

    @Test
    @DisplayName("완전 분해된 자모로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenFullyDecomposedJamo_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView view = keystrokeView("ㅅㅣㅂㅏㄹ");

        // then
        assertThat(view.getTextNormalizedView().getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertUnitTags(view, 0, STANDALONE_JAMO, CHOSEONG, CHOSEONG, false);
        assertUnitTags(view, 4, STANDALONE_JAMO, JONGSEONG, JONGSEONG, true);
        assertSpans(view.getTextNormalizedView(), 0, 0, 2, 0, 2);
        assertSpans(view.getTextNormalizedView(), 4, 2, 5, 2, 5);
    }

    @Test
    @DisplayName("음절과 자모가 섞인 문자열로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenMixedSyllablesAndJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("시바ㄹ")).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(keystrokeText("씨ㅂㅏㄹ")).isEqualTo("ㅆㅣㅂㅏㄹ");
        assertThat(keystrokeText("ㅂㅕㅇ신")).isEqualTo("ㅂㅕㅇㅅㅣㄴ");
        assertThat(keystrokeText("ㅅㅣ발")).isEqualTo("ㅅㅣㅂㅏㄹ");
    }

    @Test
    @DisplayName("겹모음으로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenCompoundVowel_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView view = keystrokeView("뒤져");

        // then
        assertThat(view.getTextNormalizedView().getText()).isEqualTo("ㄷㅜㅣㅈㅕ");
        assertThat(keystrokeText("ㄷㅜㅣㅈㅕ")).isEqualTo("ㄷㅜㅣㅈㅕ");
        assertUnitTags(view, 1, HANGUL_SYLLABLE, JUNGSEONG, JUNGSEONG, false);
        assertUnitTags(view, 2, HANGUL_SYLLABLE, JUNGSEONG, JUNGSEONG, true);
        assertSpans(view.getTextNormalizedView(), 1, 0, 1, 0, 1);
        assertSpans(view.getTextNormalizedView(), 2, 0, 1, 0, 1);
    }

    @Test
    @DisplayName("겹받침으로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenCompoundFinal_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView view = keystrokeView("값");

        // then
        assertThat(view.getTextNormalizedView().getText()).isEqualTo("ㄱㅏㅂㅅ");
        assertUnitTags(view, 2, HANGUL_SYLLABLE, JONGSEONG, JONGSEONG, false);
        assertUnitTags(view, 3, HANGUL_SYLLABLE, JONGSEONG, JONGSEONG, true);
    }

    @Test
    @DisplayName("겹자모 낱자로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenStandaloneCompoundJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("ㄳ")).isEqualTo("ㄱㅅ");
        assertThat(keystrokeText("ㅟ")).isEqualTo("ㅜㅣ");
    }

    @Test
    @DisplayName("된소리로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenTenseConsonant_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("까")).isEqualTo("ㄲㅏ");
        assertThat(keystrokeText("ㅆㅂ")).isEqualTo("ㅆㅂ");
    }

    @Test
    @DisplayName("결합되지 않은 낱자로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenUncomposedJamo_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView view = keystrokeView("ㅅㅂ");

        // then
        assertThat(view.getTextNormalizedView().getText()).isEqualTo("ㅅㅂ");
        assertUnitTags(view, 0, STANDALONE_JAMO, STANDALONE, STANDALONE, false);
        assertUnitTags(view, 1, STANDALONE_JAMO, STANDALONE, STANDALONE, false);
    }

    @Test
    @DisplayName("영문 자판 입력으로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenAsciiLowerCaseLetters_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView view = keystrokeView("tlqkf");

        // then
        assertThat(view.getTextNormalizedView().getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertUnitTags(view, 0, ASCII_LOWER_CASE_LETTER, CHOSEONG, CHOSEONG, false);
        assertUnitTags(view, 1, ASCII_LOWER_CASE_LETTER, JUNGSEONG, JUNGSEONG, true);
        assertUnitTags(view, 4, ASCII_LOWER_CASE_LETTER, JONGSEONG, JONGSEONG, true);
        assertSpans(view.getTextNormalizedView(), 4, 2, 5, 2, 5);
        assertThat(keystrokeText("qudtls")).isEqualTo("ㅂㅕㅇㅅㅣㄴ");
        assertThat(keystrokeText("시qkf")).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(keystrokeText("tl발")).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(keystrokeText("tlllqkf")).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(keystrokeText("ㅅl발")).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertUnitTags(keystrokeView("ㅅl발"), 0, STANDALONE_JAMO, CHOSEONG, CHOSEONG, false);
        assertThat(keystrokeText("시l발")).isEqualTo("ㅅㅣㅂㅏㄹ");
    }

    @Test
    @DisplayName("두 키로 이루는 겹모음과 겹받침의 영문 자판 입력으로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenAsciiLowerCaseLettersOfCompoundJamo_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("dnjs")).isEqualTo("ㅇㅜㅓㄴ");
        assertThat(keystrokeText("rkqt")).isEqualTo("ㄱㅏㅂㅅ");
    }

    @Test
    @DisplayName("뒤에 모음이 오는 영문 자판 자음으로 첫소리로 남은 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenAsciiLowerCaseConsonantBeforeVowel_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView view = keystrokeView("rkqtk");

        // then
        assertThat(view.getTextNormalizedView().getText()).isEqualTo("ㄱㅏㅂㅅㅏ");
        assertUnitTags(view, 3, ASCII_LOWER_CASE_LETTER, CHOSEONG, CHOSEONG, false);
        assertThat(keystrokeText("tlqㅏㄹ")).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(keystrokeText("tlq kf")).isEqualTo("ㅅㅣㅂㅏㄹ");
    }

    @Test
    @DisplayName("뒤 문자와 일부만 합쳐진 합자의 영문 자판 입력으로 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenLigaturePartlyJoinedWithNextLetter_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView view = keystrokeView("ﬁi");

        // then
        assertThat(view.getTextNormalizedView().getText()).isEqualTo("ㄹㅑ");
        assertSpans(view.getTextNormalizedView(), 1, 0, 3, 0, 2);
    }

    @Test
    @DisplayName("낱자로 입력한 음절과 음절 입력으로 서로 다른 출처 종류의 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenJamoTypedAndSyllableTypedWord_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView jamoTypedView = keystrokeView("ㄹㅕㅊㅏ");
        TextNormalizedKeystrokeView syllableTypedView = keystrokeView("려차");

        // then
        assertThat(jamoTypedView.getTextNormalizedView().getText()).isEqualTo("ㄹㅕㅊㅏ");
        assertThat(syllableTypedView.getTextNormalizedView().getText()).isEqualTo("ㄹㅕㅊㅏ");
        for (int index = 0; index < 4; index++) {
            assertThat(jamoTypedView.getSourceKind(index)).isEqualTo(STANDALONE_JAMO);
            assertThat(syllableTypedView.getSourceKind(index)).isEqualTo(HANGUL_SYLLABLE);
        }
    }

    @Test
    @DisplayName("끝소리 자모가 있는 단어와 낱자로 입력한 같은 단어로 같은 자모 위치의 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenWordWithFinal_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView syllableTypedView = keystrokeView("맛보다");
        TextNormalizedKeystrokeView jamoTypedView = keystrokeView("ㅁㅏㅅㅂㅗㄷㅏ");

        // then
        assertThat(syllableTypedView.getTextNormalizedView().getText()).isEqualTo("ㅁㅏㅅㅂㅗㄷㅏ");
        assertThat(jamoTypedView.getTextNormalizedView().getText()).isEqualTo("ㅁㅏㅅㅂㅗㄷㅏ");
        assertUnitTags(syllableTypedView, 2, HANGUL_SYLLABLE, JONGSEONG, JONGSEONG, true);
        assertUnitTags(jamoTypedView, 2, STANDALONE_JAMO, JONGSEONG, JONGSEONG, true);
    }

    @Test
    @DisplayName("받침과 다음 첫소리가 같은 자모로 병합되지 않은 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenFinalFollowedByIdenticalInitial_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView syllableTypedView = keystrokeView("존나");
        TextNormalizedKeystrokeView asciiLowerCaseTypedView = keystrokeView("whssk");

        // then
        assertThat(syllableTypedView.getTextNormalizedView().getText()).isEqualTo("ㅈㅗㄴㄴㅏ");
        assertThat(asciiLowerCaseTypedView.getTextNormalizedView().getText()).isEqualTo("ㅈㅗㄴㄴㅏ");
        assertThat(keystrokeText("whsssk")).isEqualTo("ㅈㅗㄴㄴㅏ");
        assertUnitTags(syllableTypedView, 2, HANGUL_SYLLABLE, JONGSEONG, JONGSEONG, true);
        assertUnitTags(syllableTypedView, 3, HANGUL_SYLLABLE, CHOSEONG, CHOSEONG, false);
        assertUnitTags(asciiLowerCaseTypedView, 2, ASCII_LOWER_CASE_LETTER, JONGSEONG, JONGSEONG, true);
        assertSpans(syllableTypedView.getTextNormalizedView(), 2, 0, 1, 0, 1);
        assertThat(keystrokeText("구조나")).isEqualTo("ㄱㅜㅈㅗㄴㅏ");
    }

    @Test
    @DisplayName("ASCII 소문자에서 바꾼 낱자로 ASCII 소문자 출처 종류의 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenJamoConvertedFromAsciiLowerCaseLetters_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView asciiLowerCaseTypedView = keystrokeView("tq");
        TextNormalizedKeystrokeView confusableTypedView = keystrokeView("ѕk");

        // then
        assertThat(asciiLowerCaseTypedView.getTextNormalizedView().getText()).isEqualTo("ㅅㅂ");
        assertUnitTags(asciiLowerCaseTypedView, 0, ASCII_LOWER_CASE_LETTER, STANDALONE, STANDALONE, false);
        assertUnitTags(asciiLowerCaseTypedView, 1, ASCII_LOWER_CASE_LETTER, STANDALONE, STANDALONE, false);
        assertThat(confusableTypedView.getTextNormalizedView().getText()).isEqualTo("ㄴㅏ");
        assertUnitTags(confusableTypedView, 0, ASCII_LOWER_CASE_LETTER, CHOSEONG, CHOSEONG, false);
    }

    @Test
    @DisplayName("합성 또는 흡수된 음절로 시작 문자에 따른 출처 종류의 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenComposedOrAbsorbedSyllable_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView noiseSeparatedJamoView = keystrokeView("ㄹ.ㅕ");
        TextNormalizedKeystrokeView absorbedJamoView = keystrokeView("시ㅣ발");

        // then
        assertThat(noiseSeparatedJamoView.getTextNormalizedView().getText()).isEqualTo("ㄹㅕ");
        assertUnitTags(noiseSeparatedJamoView, 0, STANDALONE_JAMO, CHOSEONG, CHOSEONG, false);
        assertSpans(noiseSeparatedJamoView.getTextNormalizedView(), 0, 0, 3, 0, 3);
        assertThat(absorbedJamoView.getTextNormalizedView().getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertUnitTags(absorbedJamoView, 0, HANGUL_SYLLABLE, CHOSEONG, CHOSEONG, false);
    }

    @Test
    @DisplayName("두 음절 사이 비음절 한 글자로 제거된 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenSingleNoiseBetweenSyllables_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView asciiNoiseView = keystrokeView("시x발");
        TextNormalizedKeystrokeView jamoNoiseView = keystrokeView("시ㅈ발");

        // then
        assertThat(asciiNoiseView.getTextNormalizedView().getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(jamoNoiseView.getTextNormalizedView().getText()).isEqualTo("ㅅㅣㅂㅏㄹ");
        assertSpans(asciiNoiseView.getTextNormalizedView(), 0, 0, 1, 0, 1);
        assertSpans(asciiNoiseView.getTextNormalizedView(), 2, 2, 3, 2, 3);
        assertSpans(jamoNoiseView.getTextNormalizedView(), 4, 2, 3, 2, 3);
        assertThat(keystrokeText("아h아")).isEqualTo("ㅇㅏㅇㅏ");
    }

    @Test
    @DisplayName("두 음절 사이 비음절 두 글자 이상으로 유지된 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenMultipleNoiseBetweenSyllables_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("시ㅈㅊ발")).isEqualTo("ㅅㅣㅈㅊㅂㅏㄹ");
        assertSpans(keystrokeView("시ㅈㅊ발").getTextNormalizedView(), 2, 1, 2, 1, 2);
        assertSpans(keystrokeView("시ㅈㅊ발").getTextNormalizedView(), 3, 2, 3, 2, 3);
    }

    @Test
    @DisplayName("음절 사이가 아닌 비음절로 유지된 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenNoiseNotBetweenSyllables_willReturnNormalizedView() {
        // given & when & then
        assertThat(keystrokeText("tlxqkf")).isNotEqualTo("ㅅㅣㅂㅏㄹ");
        assertThat(keystrokeText("x발")).isEqualTo(keystrokeText("ㅌ발"));
        assertThat(keystrokeText("시x")).isEqualTo(keystrokeText("시ㅌ"));
    }

    @Test
    @DisplayName("한자와 옛한글로 그대로 유지된 키 입력 뷰 반환")
    void testNormalizeWithKeystroke_givenOtherCharacters_willReturnNormalizedView() {
        // given & when
        TextNormalizedKeystrokeView hanjaView = keystrokeView("漢");
        TextNormalizedKeystrokeView supplementaryHanjaView = keystrokeView("𠀀");

        // then
        assertThat(hanjaView.getTextNormalizedView().getText()).isEqualTo("漢");
        assertUnitTags(hanjaView, 0, OTHER, STANDALONE, STANDALONE, false);
        assertThat(keystrokeText("ᄓ")).isEqualTo("ᄓ");
        assertThat(supplementaryHanjaView.getTextNormalizedView().getText()).isEqualTo("𠀀");
        assertSpans(supplementaryHanjaView.getTextNormalizedView(), 1, 0, 2, 0, 2);
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
        assertSpans(view, 0, 0, 1, 0, 1);
        assertSpans(view, 1, 4, 5, 4, 5);
    }

    @Test
    @DisplayName("글머리표와 들여쓰기가 있는 행으로 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenLinesWithBulletsAndIndents_willReturnNormalizedView() {
        // given & when
        TextNormalizedView view = lineInitialView("- 시원\n- 발코니");

        // then
        assertThat(view.getText()).isEqualTo("시발");
        assertSpans(view, 0, 2, 3, 2, 3);
        assertSpans(view, 1, 7, 8, 7, 8);
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
    @DisplayName("보충 평면 문자로 시작하는 행으로 서로게이트 쌍이 유지된 행 머리 뷰 반환")
    void testNormalizeWithLineInitial_givenSupplementaryLineInitial_willReturnNormalizedView() {
        // given & when
        TextNormalizedView view = lineInitialView("𠀀원\n발코니");

        // then
        assertThat(view.getText()).isEqualTo("𠀀발");
        assertSpans(view, 1, 0, 2, 0, 2);
        assertSpans(view, 2, 4, 5, 4, 5);
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
    @DisplayName("호환 분해가 없는 문자형 기호로 정규화 뷰 반환")
    void testNormalizeLightly_givenLetterLikeSymbols_willReturnNormalizedView() {
        // given & when & then
        assertThat(lightlyNormalizedText("🇸🇮🇧🇦🇱")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("🅢🅘🅑🅐🅛")).isEqualTo("sibal");
        assertThat(lightlyNormalizedText("🆂🅸🅱🅰🅻")).isEqualTo("sibal");
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
        assertSpans(textNormalizedView, 0, 0, 1, 0, 2);
        assertSpans(textNormalizedView, 1, 1, 2, 2, 3);
    }

    @Test
    @DisplayName("조합형 자모 군집으로 정규화 뷰 반환")
    void testNormalizeLightly_givenConjoiningJamoCluster_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("시발!");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("시발!");
        assertSpans(textNormalizedView, 0, 0, 1, 0, 2);
        assertSpans(textNormalizedView, 1, 1, 2, 2, 5);
        assertSpans(textNormalizedView, 2, 2, 3, 5, 6);
    }

    @Test
    @DisplayName("지역 표시 기호 쌍으로 정규화 뷰 반환")
    void testNormalizeLightly_givenRegionalIndicatorPair_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("🇸🇮🇧");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("sib");
        assertSpans(textNormalizedView, 0, 0, 2, 0, 4);
        assertSpans(textNormalizedView, 1, 0, 2, 0, 4);
        assertSpans(textNormalizedView, 2, 2, 3, 4, 6);
    }

    @Test
    @DisplayName("결합 문자와 보이지 않는 문자가 붙은 군집으로 정규화 뷰 반환")
    void testNormalizeLightly_givenClusterWithMarkAndInvisibleCharacter_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("s̈i시‍발");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("si시발");
        assertSpans(textNormalizedView, 0, 0, 1, 0, 2);
        assertSpans(textNormalizedView, 1, 1, 2, 2, 3);
        assertSpans(textNormalizedView, 2, 2, 3, 3, 5);
        assertSpans(textNormalizedView, 3, 3, 4, 5, 6);
    }

    @Test
    @DisplayName("여러 단위로 펼쳐지는 문자로 정규화 뷰 반환")
    void testNormalizeLightly_givenCharacterExpandingToSeveralUnits_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("ﬁ시");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("fi시");
        assertSpans(textNormalizedView, 0, 0, 2, 0, 1);
        assertSpans(textNormalizedView, 1, 0, 2, 0, 1);
        assertSpans(textNormalizedView, 2, 2, 3, 1, 2);
    }

    @Test
    @DisplayName("보이지 않는 문자 사이의 단위로 정규화 뷰 반환")
    void testNormalizeLightly_givenUnitsAroundRemovedCharacter_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeLightly("시​발");

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("시발");
        assertSpans(textNormalizedView, 0, 0, 1, 0, 1);
        assertSpans(textNormalizedView, 1, 1, 2, 2, 3);
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
    @DisplayName("라틴 문자와 혼동하기 쉬운 키릴 문자로 정규화 뷰 반환")
    void testNormalizeAggressively_givenCrossScriptConfusables_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("ѕіbаl")).isEqualTo("sibal");
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
    @DisplayName("문자 반복으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenRepeatedCharacters_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시발발발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("ssiiibal")).isEqualTo("sibal");
        assertThat(aggressivelyNormalizedText("fuuuck")).isEqualTo("fuck");
    }
    @Test
    @DisplayName("모음 늘이기로 정규화 뷰 반환")
    void testNormalizeAggressively_givenElongatedVowels_willReturnNormalizedView() {
        // given & when & then
        assertThat(aggressivelyNormalizedText("시이발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("시이이발")).isEqualTo("시발");
        assertThat(aggressivelyNormalizedText("씨ㅣ발")).isEqualTo("씨발");
        // 뒤 음절의 받침을 앞 음절로 옮김
        assertThat(aggressivelyNormalizedText("씨이바알")).isEqualTo("씨발");
    }

    @Test
    @DisplayName("늘인 모음이 아닌 음절로 정규화 뷰 반환")
    void testNormalizeAggressively_givenSyllablesWithoutElongation_willReturnNormalizedView() {
        // given & when & then
        // 모음이 다름
        assertThat(aggressivelyNormalizedText("시아")).isEqualTo("시아");
        // 앞 음절에 받침이 있음
        assertThat(aggressivelyNormalizedText("발아")).isEqualTo("발아");
        // 뒤 음절의 초성이 ㅇ이 아님
        assertThat(aggressivelyNormalizedText("시미")).isEqualTo("시미");
        // 자음 낱자가 앞 음절의 받침과도 뒤 음절의 첫소리와도 다름
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
        assertSpans(textNormalizedView, 0, 0, 2, 0, 2);
        assertSpans(textNormalizedView, 1, 2, 3, 2, 3);
        assertThat(aggressivelyNormalizedText("시ㅂ발")).isEqualTo("시발");
        // 자음 낱자가 앞 음절의 받침과만 같고 뒤 음절의 첫소리와는 다름
        assertThat(aggressivelyNormalizedText("존ㄴ맛")).isEqualTo("존맛");
    }

    @Test
    @DisplayName("제거된 문자 양옆의 단위로 정규화 뷰 반환")
    void testNormalizeAggressively_givenUnitsAroundRemovedSeparator_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly("시 발"));

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("시발");
        assertSpans(textNormalizedView, 0, 0, 1, 0, 1);
        assertSpans(textNormalizedView, 1, 2, 3, 2, 3);
    }

    @Test
    @DisplayName("결합된 자모로 정규화 뷰 반환")
    void testNormalizeAggressively_givenComposedJamo_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly("ㅂㅕㅇ신"));

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("병신");
        assertSpans(textNormalizedView, 0, 0, 3, 0, 3);
        assertSpans(textNormalizedView, 1, 3, 4, 3, 4);
    }

    @Test
    @DisplayName("접힌 반복 문자로 정규화 뷰 반환")
    void testNormalizeAggressively_givenCollapsedRepeats_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly("시발발발"));

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("시발");
        assertSpans(textNormalizedView, 0, 0, 1, 0, 1);
        assertSpans(textNormalizedView, 1, 1, 4, 1, 4);
    }

    @Test
    @DisplayName("병합된 모음으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenMergedVowels_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly("씨이바알"));

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("씨발");
        assertSpans(textNormalizedView, 0, 0, 2, 0, 2);
        assertSpans(textNormalizedView, 1, 2, 4, 2, 4);
    }

    @Test
    @DisplayName("서로게이트 쌍으로 정규화 뷰 반환")
    void testNormalizeAggressively_givenSurrogatePairs_willReturnNormalizedView() {
        // given
        String repeatedSupplementaryCharacter = "𠀀𠀀";
        String differentSupplementaryCharacters = "𠀀𠀁";

        // when
        TextNormalizedView collapsedView =
                textNormalizer.normalizeAggressively(textNormalizer.normalizeLightly(repeatedSupplementaryCharacter));

        // then
        assertThat(collapsedView.getText()).isEqualTo("𠀀");
        assertSpans(collapsedView, 0, 0, 4, 0, 4);
        assertSpans(collapsedView, 1, 0, 4, 0, 4);
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
