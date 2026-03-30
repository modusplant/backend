package kr.modusplant.shared.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CastUtilsTest {
    @Test
    @DisplayName("downcastToStringList를 통해 List<String> 반환")
    void testDowncastToStringList_givenValidParameter_willReturnList() {
        // given & when
        List<String> stringList = Arrays.asList("a", "b");

        // then
        assertThat(CastUtils.downcastToStringList(stringList)).isEqualTo(stringList);
    }

    @Test
    @DisplayName("List<?> 타입이 아닌 매개변수로 인해 오류 발생")
    void testDowncastToStringList_givenNoListType_willThrowException() {
        // given
        Map<String, String> map = Map.of("a", "b");

        // when
        ClassCastException classCastException = assertThrows(ClassCastException.class, () -> CastUtils.downcastToStringList(map));

        // then
        assertThat(classCastException.getMessage()).isEqualTo("List<?>로의 다운캐스팅에 실패하였습니다. ");
    }

    @Test
    @DisplayName("List<String> 타입이 아닌 매개변수로 인해 오류 발생")
    void testDowncastToStringList_givenNoStringListType_willThrowException() {
        // given
        List<Integer> integerList = Arrays.asList(1, 2);

        // when
        ClassCastException classCastException = assertThrows(ClassCastException.class, () -> CastUtils.downcastToStringList(integerList));

        // then
        assertThat(classCastException.getMessage()).isEqualTo("String으로의 다운캐스팅에 실패하였습니다. ");
    }
}