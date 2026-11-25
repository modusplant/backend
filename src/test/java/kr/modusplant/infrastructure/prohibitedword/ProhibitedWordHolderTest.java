package kr.modusplant.infrastructure.prohibitedword;

import kr.modusplant.infrastructure.prohibitedword.common.util.ProhibitedWordHolderTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProhibitedWordHolderTest {
    private final ProhibitedWordHolder prohibitedWordHolder = ProhibitedWordHolderTestUtils.createProhibitedWordHolder();

    @Test
    @DisplayName("구분 문자가 끼어든 금지된 말 포함 문자열로 문자열 반환")
    void testMaskProhibitedWord_givenProhibitedWordWithSeparator_willReturnString() {
        // given & when
        String result = prohibitedWordHolder.maskProhibitedWord("시.발");

        // then
        assertThat(result).isEqualTo("***");
    }

    @Test
    @DisplayName("공백이 끼어든 금지된 말 포함 문자열로 문자열 반환")
    void testMaskProhibitedWord_givenProhibitedWordWithWhitespace_willReturnString() {
        // given & when
        String result = prohibitedWordHolder.maskProhibitedWord("시 발");

        // then
        assertThat(result).isEqualTo("***");
    }

    @Test
    @DisplayName("떨어진 두 금지된 말 포함 문자열로 문자열 반환")
    void testMaskProhibitedWord_givenSeparateProhibitedWords_willReturnString() {
        // given & when
        String result = prohibitedWordHolder.maskProhibitedWord("시발 바보");

        // then
        assertThat(result).isEqualTo("*** ***");
    }

    @Test
    @DisplayName("맞닿은 두 금지된 말 포함 문자열로 문자열 반환")
    void testMaskProhibitedWord_givenAdjacentProhibitedWords_willReturnString() {
        // given & when
        String result = prohibitedWordHolder.maskProhibitedWord("시발바보");

        // then
        assertThat(result).isEqualTo("***");
    }

    @Test
    @DisplayName("행 첫 글자의 금지된 말 포함 문자열로 문자열 반환")
    void testMaskProhibitedWord_givenProhibitedWordInLineInitials_willReturnString() {
        // given
        String text = "- 시원\n- 발코니";

        // when
        String result = prohibitedWordHolder.maskProhibitedWord(text);

        // then
        assertThat(result).isEqualTo(text);
    }

    @Test
    @DisplayName("빈 문자열로 문자열 반환")
    void testMaskProhibitedWord_givenBlankString_willReturnString() {
        // given & when
        String result = prohibitedWordHolder.maskProhibitedWord(" ");

        // then
        assertThat(result).isEqualTo(" ");
    }

    @Test
    @DisplayName("null로 null 반환")
    void testMaskProhibitedWord_givenNull_willReturnNull() {
        // given & when
        String result = prohibitedWordHolder.maskProhibitedWord(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("금지된 말 포함 문자열로 참 반환")
    void testContainsProhibitedWord_givenStringWithProhibitedWord_willReturnTrue() {
        // given & when
        boolean result = prohibitedWordHolder.containsProhibitedWord("너 시발");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("금지된 말 미포함 문자열로 거짓 반환")
    void testContainsProhibitedWord_givenStringWithoutProhibitedWord_willReturnFalse() {
        // given & when
        boolean result = prohibitedWordHolder.containsProhibitedWord("안녕하세요");

        // then
        assertThat(result).isFalse();
    }
}
