package kr.modusplant.shared.framework.jdbc.holder;

import kr.modusplant.shared.framework.jdbc.common.util.SwearHolderTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SwearHolderTest {
    private final SwearHolder swearHolder = SwearHolderTestUtils.createSwearHolder();

    @Test
    @DisplayName("욕설 포함 문자열로 문자열 반환")
    public void testFilterSwear_givenStringWithSwear_willReturnString() {
        // given & when
        String result = swearHolder.filterSwear("애1미야");

        // then
        assertThat(result).isEqualTo("***야");
    }

    @Test
    @DisplayName("빈 문자열로 문자열 반환")
    public void testFilterSwear_givenBlankString_willReturnString() {
        // given & when
        String result = swearHolder.filterSwear(" ");

        // then
        assertThat(result).isEqualTo(" ");
    }

    @Test
    @DisplayName("null로 null 반환")
    public void testFilterSwear_givenNull_willReturnNull() {
        // given & when
        String result = swearHolder.filterSwear(null);

        // then
        assertThat(result).isEqualTo(null);
    }
}
