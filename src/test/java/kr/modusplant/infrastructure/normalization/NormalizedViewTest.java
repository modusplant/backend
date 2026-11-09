package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.NormalizedView.JamoPosition.FINAL;
import static kr.modusplant.infrastructure.normalization.NormalizedView.JamoPosition.INITIAL;
import static kr.modusplant.infrastructure.normalization.NormalizedView.JamoPosition.MEDIAL;
import static kr.modusplant.infrastructure.normalization.NormalizedView.JamoPosition.STANDALONE;
import static kr.modusplant.infrastructure.normalization.NormalizedView.SourceCharacterKind.HANGUL_SYLLABLE;
import static kr.modusplant.infrastructure.normalization.NormalizedView.SourceCharacterKind.OTHER;
import static kr.modusplant.infrastructure.normalization.NormalizedView.SourceCharacterKind.STANDALONE_JAMO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NormalizedViewTest {
    @Test
    @DisplayName("코드 유닛 추가로 정규화 뷰 반환")
    void testBuild_givenAppendedUnits_willReturnNormalizedView() {
        // given
        NormalizedView.Builder builder = new NormalizedView.Builder(2);

        // when
        builder.append('a', 0, 1, 0, 2);
        builder.append('b', 1, 2, 2, 3);
        NormalizedView normalizedView = builder.build();

        // then
        assertThat(normalizedView.getText()).isEqualTo("ab");
        assertThat(normalizedView.length()).isEqualTo(2);
        assertThat(normalizedView.getLightlyNormalizedStart(1)).isEqualTo(1);
        assertThat(normalizedView.getLightlyNormalizedEnd(1)).isEqualTo(2);
        assertThat(normalizedView.getOriginalStart(0)).isZero();
        assertThat(normalizedView.getOriginalEnd(0)).isEqualTo(2);
    }

    @Test
    @DisplayName("초기 용량 초과로 정규화 뷰 반환")
    void testBuild_givenUnitsExceedingCapacity_willReturnNormalizedView() {
        // given
        NormalizedView.Builder builder = new NormalizedView.Builder(1);

        // when
        for (int index = 0; index < 5; index++) {
            builder.append((char) ('a' + index), index, index + 1, index, index + 1);
        }
        NormalizedView normalizedView = builder.build();

        // then
        assertThat(builder.length()).isEqualTo(5);
        assertThat(normalizedView.getText()).isEqualTo("abcde");
        assertThat(normalizedView.getOriginalEnd(4)).isEqualTo(5);
    }

    @Test
    @DisplayName("유닛 태그가 있는 코드 유닛으로 정규화 뷰 반환")
    void testBuild_givenUnitsWithTags_willReturnNormalizedView() {
        // given
        NormalizedView.Builder builder = new NormalizedView.Builder(1);

        // when
        builder.append('ㅅ', 0, 1, 0, 1, STANDALONE_JAMO, INITIAL, FINAL, true);
        builder.append('ㅏ', 1, 2, 1, 2, HANGUL_SYLLABLE, MEDIAL, MEDIAL, false);
        NormalizedView normalizedView = builder.build();

        // then
        assertThat(normalizedView.getSourceCharacterKind(0)).isEqualTo(STANDALONE_JAMO);
        assertThat(normalizedView.getFirstSourceJamoPosition(0)).isEqualTo(INITIAL);
        assertThat(normalizedView.getLastSourceJamoPosition(0)).isEqualTo(FINAL);
        assertThat(normalizedView.getSyllableEnds(0)).isTrue();
        assertThat(normalizedView.getSourceCharacterKind(1)).isEqualTo(HANGUL_SYLLABLE);
        assertThat(normalizedView.getSyllableEnds(1)).isFalse();
    }

    @Test
    @DisplayName("유닛 태그가 없는 코드 유닛으로 정규화 뷰 반환")
    void testBuild_givenUnitWithoutTags_willReturnNormalizedView() {
        // given
        NormalizedView.Builder builder = new NormalizedView.Builder(1);

        // when
        builder.append('a', 0, 1, 0, 1);
        NormalizedView normalizedView = builder.build();

        // then
        assertThat(normalizedView.getSourceCharacterKind(0)).isEqualTo(OTHER);
        assertThat(normalizedView.getFirstSourceJamoPosition(0)).isEqualTo(STANDALONE);
        assertThat(normalizedView.getLastSourceJamoPosition(0)).isEqualTo(STANDALONE);
        assertThat(normalizedView.getSyllableEnds(0)).isFalse();
    }

    @Test
    @DisplayName("0 용량으로 정규화 뷰 반환")
    void testBuild_givenZeroCapacityAndNoUnits_willReturnNormalizedView() {
        // given & when
        NormalizedView normalizedView = new NormalizedView.Builder(0).build();

        // then
        assertThat(normalizedView.getText()).isEmpty();
        assertThat(normalizedView.length()).isZero();
    }

    @Test
    @DisplayName("범위를 벗어난 인덱스로 예외 반환")
    void testGetOriginalStart_givenIndexOutOfRange_willThrowException() {
        // given
        NormalizedView.Builder builder = new NormalizedView.Builder(3);
        builder.append('a', 0, 1, 0, 1);
        NormalizedView normalizedView = builder.build();

        // when & then
        assertThatThrownBy(() -> normalizedView.getOriginalStart(1))
                .isInstanceOf(ArrayIndexOutOfBoundsException.class);
    }
}
