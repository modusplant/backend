package kr.modusplant.infrastructure.swear;

import kr.modusplant.infrastructure.swear.common.util.SwearHolderTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SwearHolderTest {
    private final SwearHolder swearHolder = SwearHolderTestUtils.createSwearHolder();

    @Test
    @DisplayName("구분 문자가 끼어든 비속어 포함 문자열로 문자열 반환")
    void testMaskSwear_givenSwearWithSeparator_willReturnString() {
        // given & when
        String result = swearHolder.maskSwear("시.발");

        // then
        assertThat(result).isEqualTo("***");
    }

    @Test
    @DisplayName("공백이 끼어든 비속어 포함 문자열로 문자열 반환")
    void testMaskSwear_givenSwearWithWhitespace_willReturnString() {
        // given & when
        String result = swearHolder.maskSwear("시 발");

        // then
        assertThat(result).isEqualTo("***");
    }

    @Test
    @DisplayName("떨어진 두 비속어 포함 문자열로 문자열 반환")
    void testMaskSwear_givenSeparateSwears_willReturnString() {
        // given & when
        String result = swearHolder.maskSwear("시발 바보");

        // then
        assertThat(result).isEqualTo("*** ***");
    }

    @Test
    @DisplayName("맞닿은 두 비속어 포함 문자열로 문자열 반환")
    void testMaskSwear_givenAdjacentSwears_willReturnString() {
        // given & when
        String result = swearHolder.maskSwear("시발바보");

        // then
        assertThat(result).isEqualTo("***");
    }

    @Test
    @DisplayName("행 첫 글자의 비속어 포함 문자열로 문자열 반환")
    void testMaskSwear_givenSwearInLineInitials_willReturnString() {
        // given
        String text = "- 시원\n- 발코니";

        // when
        String result = swearHolder.maskSwear(text);

        // then
        assertThat(result).isEqualTo(text);
    }

    @Test
    @DisplayName("빈 문자열로 문자열 반환")
    void testMaskSwear_givenBlankString_willReturnString() {
        // given & when
        String result = swearHolder.maskSwear(" ");

        // then
        assertThat(result).isEqualTo(" ");
    }

    @Test
    @DisplayName("null로 null 반환")
    void testMaskSwear_givenNull_willReturnNull() {
        // given & when
        String result = swearHolder.maskSwear(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("비속어 포함 문자열로 참 반환")
    void testContainsSwear_givenStringWithSwear_willReturnTrue() {
        // given & when
        boolean result = swearHolder.containsSwear("너 시발");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("비속어 미포함 문자열로 거짓 반환")
    void testContainsSwear_givenStringWithoutSwear_willReturnFalse() {
        // given & when
        boolean result = swearHolder.containsSwear("안녕하세요");

        // then
        assertThat(result).isFalse();
    }
}
