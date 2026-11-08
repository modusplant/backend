package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NormalizedTextTest {
    @Test
    @DisplayName("코드 유닛 추가로 정규화 텍스트 반환")
    void testBuild_givenAppendedUnits_willReturnNormalizedText() {
        // given
        NormalizedText.Builder builder = new NormalizedText.Builder(2);

        // when
        builder.append('a', 0, 1, 0, 2);
        builder.append('b', 1, 2, 2, 3);
        NormalizedText normalizedText = builder.build();

        // then
        assertThat(normalizedText.getText()).isEqualTo("ab");
        assertThat(normalizedText.length()).isEqualTo(2);
        assertThat(normalizedText.getLightlyNormalizedStart(1)).isEqualTo(1);
        assertThat(normalizedText.getLightlyNormalizedEnd(1)).isEqualTo(2);
        assertThat(normalizedText.getOriginalStart(0)).isZero();
        assertThat(normalizedText.getOriginalEnd(0)).isEqualTo(2);
    }

    @Test
    @DisplayName("초기 용량 초과로 정규화 텍스트 반환")
    void testBuild_givenUnitsExceedingCapacity_willReturnNormalizedText() {
        // given
        NormalizedText.Builder builder = new NormalizedText.Builder(1);

        // when
        for (int index = 0; index < 5; index++) {
            builder.append((char) ('a' + index), index, index + 1, index, index + 1);
        }
        NormalizedText normalizedText = builder.build();

        // then
        assertThat(builder.length()).isEqualTo(5);
        assertThat(normalizedText.getText()).isEqualTo("abcde");
        assertThat(normalizedText.getOriginalEnd(4)).isEqualTo(5);
    }

    @Test
    @DisplayName("0 용량으로 정규화 텍스트 반환")
    void testBuild_givenZeroCapacityAndNoUnits_willReturnNormalizedText() {
        // given & when
        NormalizedText normalizedText = new NormalizedText.Builder(0).build();

        // then
        assertThat(normalizedText.getText()).isEmpty();
        assertThat(normalizedText.length()).isZero();
    }

    @Test
    @DisplayName("범위를 벗어난 인덱스로 예외 반환")
    void testGetOriginalStart_givenIndexOutOfRange_willThrowException() {
        // given
        NormalizedText.Builder builder = new NormalizedText.Builder(3);
        builder.append('a', 0, 1, 0, 1);
        NormalizedText normalizedText = builder.build();

        // when & then
        assertThatThrownBy(() -> normalizedText.getOriginalStart(1))
                .isInstanceOf(ArrayIndexOutOfBoundsException.class);
    }
}
