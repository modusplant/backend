package kr.modusplant.infrastructure.swear;

import kr.modusplant.infrastructure.config.exception.ConfigurationException;
import kr.modusplant.infrastructure.config.exception.enums.ConfigurationErrorCode;
import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import kr.modusplant.infrastructure.normalization.TextNormalizer;
import kr.modusplant.infrastructure.swear.common.util.SwearMatchTestUtils;
import kr.modusplant.shared.enums.SwearType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kr.modusplant.infrastructure.normalization.TextNormalizationType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SwearEngineTest implements SwearMatchTestUtils {
    private static final Icu4jConfig icu4jConfig = new Icu4jConfig();
    private static final TextNormalizer textNormalizer =
            new TextNormalizer(icu4jConfig.nfdNormalizer(), icu4jConfig.nfcNormalizer(),
                    icu4jConfig.nfkdNormalizer(), icu4jConfig.nfkcNormalizer());

    private static SwearEngine engineOf(List<SwearWord> swearWords, List<String> notSwearWords) {
        return SwearEngine.create(textNormalizer, swearWords, notSwearWords);
    }

    private static SwearWord swearWordOf(String word, SwearScript swearScript) {
        return new SwearWord(word, SwearType.GENERAL, 2, swearScript);
    }

    @Test
    @DisplayName("문자 체계가 일치하지 않는 단어로 예외 반환")
    void testCreate_givenMismatchedScripts_willThrowException() {
        // given
        List<SwearWord> swearWords = List.of(testSwearWord, swearWordOf("sibal", SwearScript.HANGUL),
                swearWordOf("🖕", SwearScript.LATIN));

        // when & then
        assertThatThrownBy(() -> engineOf(swearWords, List.of()))
                .isInstanceOf(ConfigurationException.class)
                .satisfies(exception -> assertThat(((ConfigurationException) exception).getErrorCode())
                        .isEqualTo(ConfigurationErrorCode.INCORRECT_SWEAR_SCRIPT))
                .hasMessageContainingAll("sibal(HANGUL → LATIN)", "🖕(LATIN → OTHER)")
                .hasMessageNotContaining("시발");
    }

    @Test
    @DisplayName("비속어로 List 반환")
    void testMatch_givenSwearWord_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testSwearWord), List.of());

        // when
        List<SwearMatch> swearMatches = swearEngine.match("시발");

        // then
        assertThat(swearMatches).containsExactly(testSwearMatch);
    }

    @Test
    @DisplayName("null 또는 빈 텍스트로 List 반환")
    void testMatch_givenNullOrEmptyText_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testSwearWord), List.of());

        // when
        List<SwearMatch> nullTextMatches = swearEngine.match(null);
        List<SwearMatch> emptyTextMatches = swearEngine.match("");

        // then
        assertThat(nullTextMatches).isEmpty();
        assertThat(emptyTextMatches).isEmpty();
    }

    @Test
    @DisplayName("글자 사이 기호로 List 반환")
    void testMatch_givenSymbolBetweenLetters_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testSwearWord), List.of());

        // when
        List<SwearMatch> swearMatches = swearEngine.match("시.발");

        // then
        assertThat(swearMatches).containsExactly(new SwearMatch(testSwearWord, AGGRESSIVE, 0, 3));
    }

    @Test
    @DisplayName("비속어가 아닌 단어로 List 반환")
    void testMatch_givenNotSwearWord_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testSwearWord), List.of("시발점"));

        // when
        List<SwearMatch> notSwearWordMatches = swearEngine.match("시발점");
        List<SwearMatch> separatedWordMatches = swearEngine.match("시발 점");

        // then
        assertThat(notSwearWordMatches).isEmpty();
        assertThat(separatedWordMatches).containsExactly(testSwearMatch);
    }

    @Test
    @DisplayName("공백을 넘는 매칭으로 List 반환")
    void testMatch_givenMatchBridgingWhitespace_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testSwearWord), List.of());

        // when
        List<SwearMatch> partialTokenMatches = swearEngine.match("검사 시 발생");
        List<SwearMatch> wholeTokenMatches = swearEngine.match("시 발 놈");

        // then
        assertThat(partialTokenMatches).isEmpty();
        assertThat(wholeTokenMatches).containsExactly(new SwearMatch(testSwearWord, AGGRESSIVE, 0, 3));
    }

    @Test
    @DisplayName("음절 안 자모의 키 입력 매칭으로 List 반환")
    void testMatch_givenKeystrokeKeywordInsideSyllables_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testLeadingConsonantSwearWord), List.of());

        // when
        List<SwearMatch> insideSyllableMatches = swearEngine.match("맛보다");
        List<SwearMatch> standaloneJamoMatches = swearEngine.match("ㅅ ㅂ");

        // then
        assertThat(insideSyllableMatches).isEmpty();
        assertThat(standaloneJamoMatches)
                .containsExactly(new SwearMatch(testLeadingConsonantSwearWord, AGGRESSIVE, 0, 3));
    }

    @Test
    @DisplayName("한글 자판으로 친 ASCII 소문자로 List 반환")
    void testMatch_givenKeyboardTypedAsciiLowerCaseLetters_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testSwearWord), List.of());

        // when
        List<SwearMatch> wholeRunMatches = swearEngine.match("tlqkf");
        List<SwearMatch> leadingLetterMatches = swearEngine.match("atlqkf");
        List<SwearMatch> trailingLetterMatches = swearEngine.match("tlqkfb");
        List<SwearMatch> surroundingLetterMatches = swearEngine.match("atlqkfb");
        List<SwearMatch> elongatedMatches = swearEngine.match("tlllqkf");
        List<SwearMatch> mixedMatches = swearEngine.match("ㅅl발");
        List<SwearMatch> hangulSurroundedRunMatches = swearEngine.match("개tlqkf놈");
        List<SwearMatch> leadingLetterBeforeSyllableMatches = swearEngine.match("atl발");
        List<SwearMatch> trailingLetterAfterJamoMatches = swearEngine.match("ㅅlqkfb");

        // then
        assertThat(wholeRunMatches).containsExactly(new SwearMatch(testSwearWord, KEYSTROKE, 0, 5));
        assertThat(elongatedMatches).containsExactly(new SwearMatch(testSwearWord, KEYSTROKE, 0, 7));
        assertThat(mixedMatches).containsExactly(new SwearMatch(testSwearWord, KEYSTROKE, 0, 3));
        assertThat(hangulSurroundedRunMatches).containsExactly(new SwearMatch(testSwearWord, KEYSTROKE, 1, 6));
        assertThat(leadingLetterMatches).isEmpty();
        assertThat(trailingLetterMatches).isEmpty();
        assertThat(surroundingLetterMatches).isEmpty();
        assertThat(leadingLetterBeforeSyllableMatches).isEmpty();
        assertThat(trailingLetterAfterJamoMatches).isEmpty();
    }

    @Test
    @DisplayName("라틴 단어의 키 입력 키워드 제외한 List 반환")
    void testMatch_givenLatinWord_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testEnglishSwearWord), List.of());

        // when
        List<SwearMatch> syllableTypedMatches = swearEngine.match("야차");
        List<SwearMatch> standaloneJamoMatches = swearEngine.match("ㅇㅑㅊㅏ");

        // then
        assertThat(syllableTypedMatches).isEmpty();
        assertThat(standaloneJamoMatches).isEmpty();
    }

    @Test
    @DisplayName("두 음절 사이 잡음의 음절 매칭으로 List 반환")
    void testMatch_givenNoiseBetweenSyllables_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testSwearWord), List.of());

        // when
        List<SwearMatch> asciiNoiseMatches = swearEngine.match("시X발");
        List<SwearMatch> trailingConsonantMatches = swearEngine.match("시바ㄹ놈");

        // then
        assertThat(asciiNoiseMatches).containsExactly(new SwearMatch(testSwearWord, SYLLABLE, 0, 3));
        assertThat(trailingConsonantMatches).containsExactly(new SwearMatch(testSwearWord, SYLLABLE, 0, 3));
    }

    @Test
    @DisplayName("음절 정규화가 바꾸는 단어로 음절 키워드 제외한 List 반환")
    void testMatch_givenWordChangedBySyllableNormalization_willReturnList() {
        // given
        SwearWord leadingConsonantSwearWord = swearWordOf("ㅅㅂ", SwearScript.HANGUL);
        SwearEngine swearEngine = engineOf(List.of(leadingConsonantSwearWord), List.of());

        // when
        List<SwearMatch> sameWordMatches = swearEngine.match("ㅅㅂ");
        List<SwearMatch> noisedMatches = swearEngine.match("ㅅㅈㅂ");

        // then
        assertThat(sameWordMatches).extracting(SwearMatch::textNormalizationType).doesNotContain(SYLLABLE);
        assertThat(noisedMatches).extracting(SwearMatch::textNormalizationType).doesNotContain(SYLLABLE);
    }

    @Test
    @DisplayName("행 첫 글자로 List 반환")
    void testMatch_givenAcrostic_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testSwearWord), List.of());

        // when
        List<SwearMatch> swearMatches = swearEngine.match("- 시원\n- 발코니");

        // then
        assertThat(swearMatches).containsExactly(new SwearMatch(testSwearWord, LINE_INITIAL, 2, 8));
    }

    @Test
    @DisplayName("행 첫 글자의 비속어가 아닌 단어로 List 반환")
    void testMatch_givenNotSwearWordInLineInitials_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(testSwearWord), List.of("시발점"));

        // when
        List<SwearMatch> swearMatches = swearEngine.match("- 시원\n- 발코니\n- 점심");

        // then
        assertThat(swearMatches).isEmpty();
    }

    @Test
    @DisplayName("행 첫 글자의 한 코드 포인트 단어로 List 반환")
    void testMatch_givenSingleCodePointWordInLineInitials_willReturnList() {
        // given
        SwearEngine swearEngine = engineOf(List.of(swearWordOf("씨", SwearScript.HANGUL)), List.of("씨앗"));

        // when
        List<SwearMatch> swearMatches = swearEngine.match("씨앗\n나무");

        // then
        assertThat(swearMatches).isEmpty();
    }

    @Test
    @DisplayName("공격적 정규화가 바꾸는 단어로 List 반환")
    void testMatch_givenWordChangedByAggressiveNormalization_willReturnList() {
        // given
        SwearWord swearWordWithDigit = swearWordOf("씨8", SwearScript.HANGUL);
        SwearWord swearWordWithSymbol = swearWordOf("🖕", SwearScript.OTHER);
        SwearEngine swearEngine = engineOf(List.of(swearWordWithDigit, swearWordWithSymbol), List.of());

        // when
        List<SwearMatch> digitWordMatches = swearEngine.match("씨8");
        List<SwearMatch> separatedDigitWordMatches = swearEngine.match("씨 8");
        List<SwearMatch> shortenedWordMatches = swearEngine.match("씨");
        List<SwearMatch> symbolWordMatches = swearEngine.match("🖕");

        // then
        assertThat(digitWordMatches).containsExactly(new SwearMatch(swearWordWithDigit, LIGHT, 0, 2));
        assertThat(separatedDigitWordMatches).isEmpty();
        assertThat(shortenedWordMatches).isEmpty();
        assertThat(symbolWordMatches).containsExactly(new SwearMatch(swearWordWithSymbol, LIGHT, 0, 2));
    }

    @Test
    @DisplayName("겹치는 서로 다른 단어로 List 반환")
    void testMatch_givenOverlappingDifferentWords_willReturnList() {
        // given
        SwearWord overlappingSwearWord = swearWordOf("발놈", SwearScript.HANGUL);
        SwearEngine swearEngine = engineOf(List.of(testSwearWord, overlappingSwearWord), List.of());

        // when
        List<SwearMatch> swearMatches = swearEngine.match("시발놈");

        // then
        assertThat(swearMatches)
                .containsExactlyInAnyOrder(testSwearMatch, new SwearMatch(overlappingSwearWord, LIGHT, 1, 3));
    }
}
