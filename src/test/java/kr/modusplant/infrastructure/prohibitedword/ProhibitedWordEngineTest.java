package kr.modusplant.infrastructure.prohibitedword;

import kr.modusplant.infrastructure.config.exception.ConfigurationException;
import kr.modusplant.infrastructure.config.exception.enums.ConfigurationErrorCode;
import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import kr.modusplant.infrastructure.normalization.TextNormalizer;
import kr.modusplant.infrastructure.prohibitedword.common.util.ProhibitedWordMatchTestUtils;
import kr.modusplant.shared.enums.ProhibitedWordType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kr.modusplant.infrastructure.normalization.TextNormalizationType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProhibitedWordEngineTest implements ProhibitedWordMatchTestUtils {
    private static final Icu4jConfig icu4jConfig = new Icu4jConfig();
    private static final TextNormalizer textNormalizer =
            new TextNormalizer(icu4jConfig.nfdNormalizer(), icu4jConfig.nfcNormalizer(),
                    icu4jConfig.nfkdNormalizer(), icu4jConfig.nfkcNormalizer());

    private static ProhibitedWordEngine engineOf(List<ProhibitedWord> prohibitedWords, List<String> allowedWords) {
        return ProhibitedWordEngine.create(textNormalizer, prohibitedWords, allowedWords);
    }

    private static ProhibitedWord prohibitedWordOf(String word, ProhibitedWordScript prohibitedWordScript) {
        return new ProhibitedWord(word, ProhibitedWordType.GENERAL, 2, prohibitedWordScript);
    }

    @Test
    @DisplayName("문자 집합가 일치하지 않는 단어로 예외 반환")
    void testCreate_givenMismatchedScripts_willThrowException() {
        // given
        List<ProhibitedWord> prohibitedWords = List.of(testProhibitedWord, prohibitedWordOf("sibal", ProhibitedWordScript.HANGUL),
                prohibitedWordOf("🖕", ProhibitedWordScript.LATIN));

        // when & then
        assertThatThrownBy(() -> engineOf(prohibitedWords, List.of()))
                .isInstanceOf(ConfigurationException.class)
                .satisfies(exception -> assertThat(((ConfigurationException) exception).getErrorCode())
                        .isEqualTo(ConfigurationErrorCode.INCORRECT_PROHIBITED_WORD_SCRIPT))
                .hasMessageContainingAll("sibal(HANGUL → LATIN)", "🖕(LATIN → OTHER)")
                .hasMessageNotContaining("시발");
    }

    @Test
    @DisplayName("금지된 말로 List 반환")
    void testMatch_givenProhibitedWord_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> prohibitedWordMatches = prohibitedWordEngine.match("시발");

        // then
        assertThat(prohibitedWordMatches).containsExactly(testProhibitedWordMatch);
    }

    @Test
    @DisplayName("null 또는 빈 텍스트로 List 반환")
    void testMatch_givenNullOrEmptyText_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> nullTextMatches = prohibitedWordEngine.match(null);
        List<ProhibitedWordMatch> emptyTextMatches = prohibitedWordEngine.match("");

        // then
        assertThat(nullTextMatches).isEmpty();
        assertThat(emptyTextMatches).isEmpty();
    }

    @Test
    @DisplayName("글자 사이 기호로 List 반환")
    void testMatch_givenSymbolBetweenLetters_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> prohibitedWordMatches = prohibitedWordEngine.match("시.발");

        // then
        assertThat(prohibitedWordMatches).containsExactly(new ProhibitedWordMatch(testProhibitedWord, AGGRESSIVE, 0, 3));
    }

    @Test
    @DisplayName("허용된 말로 List 반환")
    void testMatch_givenAllowedWord_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord), List.of("시발점"));

        // when
        List<ProhibitedWordMatch> allowedWordMatches = prohibitedWordEngine.match("시발점");
        List<ProhibitedWordMatch> separatedWordMatches = prohibitedWordEngine.match("시발 점");

        // then
        assertThat(allowedWordMatches).isEmpty();
        assertThat(separatedWordMatches).containsExactly(testProhibitedWordMatch);
    }

    @Test
    @DisplayName("공백을 넘는 매칭으로 List 반환")
    void testMatch_givenMatchBridgingWhitespace_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> partialTokenMatches = prohibitedWordEngine.match("검사 시 발생");
        List<ProhibitedWordMatch> wholeTokenMatches = prohibitedWordEngine.match("시 발 놈");

        // then
        assertThat(partialTokenMatches).isEmpty();
        assertThat(wholeTokenMatches).containsExactly(new ProhibitedWordMatch(testProhibitedWord, AGGRESSIVE, 0, 3));
    }

    @Test
    @DisplayName("음절 안 자모의 키 입력 매칭으로 List 반환")
    void testMatch_givenKeystrokeKeywordInsideSyllables_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testLeadingConsonantProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> insideSyllableMatches = prohibitedWordEngine.match("맛보다");
        List<ProhibitedWordMatch> standaloneJamoMatches = prohibitedWordEngine.match("ㅅ ㅂ");

        // then
        assertThat(insideSyllableMatches).isEmpty();
        assertThat(standaloneJamoMatches)
                .containsExactly(new ProhibitedWordMatch(testLeadingConsonantProhibitedWord, AGGRESSIVE, 0, 3));
    }

    @Test
    @DisplayName("한글 자판으로 친 ASCII 소문자로 List 반환")
    void testMatch_givenKeyboardTypedAsciiLowerCaseLetters_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> wholeRunMatches = prohibitedWordEngine.match("tlqkf");
        List<ProhibitedWordMatch> leadingLetterMatches = prohibitedWordEngine.match("atlqkf");
        List<ProhibitedWordMatch> trailingLetterMatches = prohibitedWordEngine.match("tlqkfb");
        List<ProhibitedWordMatch> surroundingLetterMatches = prohibitedWordEngine.match("atlqkfb");
        List<ProhibitedWordMatch> elongatedMatches = prohibitedWordEngine.match("tlllqkf");
        List<ProhibitedWordMatch> mixedMatches = prohibitedWordEngine.match("ㅅl발");
        List<ProhibitedWordMatch> hangulSurroundedRunMatches = prohibitedWordEngine.match("개tlqkf놈");
        List<ProhibitedWordMatch> leadingLetterBeforeSyllableMatches = prohibitedWordEngine.match("atl발");
        List<ProhibitedWordMatch> trailingLetterAfterJamoMatches = prohibitedWordEngine.match("ㅅlqkfb");

        // then
        assertThat(wholeRunMatches).containsExactly(new ProhibitedWordMatch(testProhibitedWord, KEYSTROKE, 0, 5));
        assertThat(elongatedMatches).containsExactly(new ProhibitedWordMatch(testProhibitedWord, KEYSTROKE, 0, 7));
        assertThat(mixedMatches).containsExactly(new ProhibitedWordMatch(testProhibitedWord, KEYSTROKE, 0, 3));
        assertThat(hangulSurroundedRunMatches).containsExactly(new ProhibitedWordMatch(testProhibitedWord, KEYSTROKE, 1, 6));
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
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testEnglishProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> syllableTypedMatches = prohibitedWordEngine.match("야차");
        List<ProhibitedWordMatch> standaloneJamoMatches = prohibitedWordEngine.match("ㅇㅑㅊㅏ");

        // then
        assertThat(syllableTypedMatches).isEmpty();
        assertThat(standaloneJamoMatches).isEmpty();
    }

    @Test
    @DisplayName("두 음절 사이 잡음의 음절 매칭으로 List 반환")
    void testMatch_givenNoiseBetweenSyllables_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> asciiNoiseMatches = prohibitedWordEngine.match("시X발");
        List<ProhibitedWordMatch> trailingConsonantMatches = prohibitedWordEngine.match("시바ㄹ놈");

        // then
        assertThat(asciiNoiseMatches).containsExactly(new ProhibitedWordMatch(testProhibitedWord, SYLLABLE, 0, 3));
        assertThat(trailingConsonantMatches).containsExactly(new ProhibitedWordMatch(testProhibitedWord, SYLLABLE, 0, 3));
    }

    @Test
    @DisplayName("음절 정규화가 바꾸는 단어로 음절 키워드 제외한 List 반환")
    void testMatch_givenWordChangedBySyllableNormalization_willReturnList() {
        // given
        ProhibitedWord leadingConsonantProhibitedWord = prohibitedWordOf("ㅅㅂ", ProhibitedWordScript.HANGUL);
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(leadingConsonantProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> sameWordMatches = prohibitedWordEngine.match("ㅅㅂ");
        List<ProhibitedWordMatch> noisedMatches = prohibitedWordEngine.match("ㅅㅈㅂ");

        // then
        assertThat(sameWordMatches).extracting(ProhibitedWordMatch::textNormalizationType).doesNotContain(SYLLABLE);
        assertThat(noisedMatches).extracting(ProhibitedWordMatch::textNormalizationType).doesNotContain(SYLLABLE);
    }

    @Test
    @DisplayName("행 첫 글자로 List 반환")
    void testMatch_givenAcrostic_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> prohibitedWordMatches = prohibitedWordEngine.match("- 시원\n- 발코니");

        // then
        assertThat(prohibitedWordMatches).containsExactly(new ProhibitedWordMatch(testProhibitedWord, LINE_INITIAL, 2, 8));
    }

    @Test
    @DisplayName("행 첫 글자의 허용된 말로 List 반환")
    void testMatch_givenAllowedWordInLineInitials_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord), List.of("시발점"));

        // when
        List<ProhibitedWordMatch> prohibitedWordMatches = prohibitedWordEngine.match("- 시원\n- 발코니\n- 점심");

        // then
        assertThat(prohibitedWordMatches).isEmpty();
    }

    @Test
    @DisplayName("행 첫 글자의 한 코드 포인트 단어로 List 반환")
    void testMatch_givenSingleCodePointWordInLineInitials_willReturnList() {
        // given
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(prohibitedWordOf("씨", ProhibitedWordScript.HANGUL)), List.of("씨앗"));

        // when
        List<ProhibitedWordMatch> prohibitedWordMatches = prohibitedWordEngine.match("씨앗\n나무");

        // then
        assertThat(prohibitedWordMatches).isEmpty();
    }

    @Test
    @DisplayName("공격적 정규화가 바꾸는 단어로 List 반환")
    void testMatch_givenWordChangedByAggressiveNormalization_willReturnList() {
        // given
        ProhibitedWord prohibitedWordWithDigit = prohibitedWordOf("씨8", ProhibitedWordScript.HANGUL);
        ProhibitedWord prohibitedWordWithSymbol = prohibitedWordOf("🖕", ProhibitedWordScript.OTHER);
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(prohibitedWordWithDigit, prohibitedWordWithSymbol), List.of());

        // when
        List<ProhibitedWordMatch> digitWordMatches = prohibitedWordEngine.match("씨8");
        List<ProhibitedWordMatch> separatedDigitWordMatches = prohibitedWordEngine.match("씨 8");
        List<ProhibitedWordMatch> shortenedWordMatches = prohibitedWordEngine.match("씨");
        List<ProhibitedWordMatch> symbolWordMatches = prohibitedWordEngine.match("🖕");

        // then
        assertThat(digitWordMatches).containsExactly(new ProhibitedWordMatch(prohibitedWordWithDigit, LIGHT, 0, 2));
        assertThat(separatedDigitWordMatches).isEmpty();
        assertThat(shortenedWordMatches).isEmpty();
        assertThat(symbolWordMatches).containsExactly(new ProhibitedWordMatch(prohibitedWordWithSymbol, LIGHT, 0, 2));
    }

    @Test
    @DisplayName("겹치는 서로 다른 단어로 List 반환")
    void testMatch_givenOverlappingDifferentWords_willReturnList() {
        // given
        ProhibitedWord overlappingProhibitedWord = prohibitedWordOf("발놈", ProhibitedWordScript.HANGUL);
        ProhibitedWordEngine prohibitedWordEngine = engineOf(List.of(testProhibitedWord, overlappingProhibitedWord), List.of());

        // when
        List<ProhibitedWordMatch> prohibitedWordMatches = prohibitedWordEngine.match("시발놈");

        // then
        assertThat(prohibitedWordMatches)
                .containsExactlyInAnyOrder(testProhibitedWordMatch, new ProhibitedWordMatch(overlappingProhibitedWord, LIGHT, 1, 3));
    }
}
