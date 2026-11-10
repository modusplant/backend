package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.TextNormalizedView.JamoPosition.FINAL;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.JamoPosition.INITIAL;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.JamoPosition.MEDIAL;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.JamoPosition.STANDALONE;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.SourceCharacterKind.HANGUL_SYLLABLE;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.SourceCharacterKind.OTHER;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.SourceCharacterKind.STANDALONE_JAMO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextNormalizedViewTest {
    @Test
    @DisplayName("코드 유닛 추가로 정규화 뷰 반환")
    void testBuild_givenAppendedUnits_willReturnNormalizedView() {
        // given
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(2);

        // when
        builder.append('a', 0, 1, 0, 2);
        builder.append('b', 1, 2, 2, 3);
        TextNormalizedView textNormalizedView = builder.build();

        // then
        assertThat(textNormalizedView.getText()).isEqualTo("ab");
        assertThat(textNormalizedView.length()).isEqualTo(2);
        assertThat(textNormalizedView.getLightlyNormalizedStart(1)).isEqualTo(1);
        assertThat(textNormalizedView.getLightlyNormalizedEnd(1)).isEqualTo(2);
        assertThat(textNormalizedView.getOriginalStart(0)).isZero();
        assertThat(textNormalizedView.getOriginalEnd(0)).isEqualTo(2);
    }

    @Test
    @DisplayName("초기 용량 초과로 정규화 뷰 반환")
    void testBuild_givenUnitsExceedingCapacity_willReturnNormalizedView() {
        // given
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(1);

        // when
        for (int index = 0; index < 5; index++) {
            builder.append((char) ('a' + index), index, index + 1, index, index + 1);
        }
        TextNormalizedView textNormalizedView = builder.build();

        // then
        assertThat(builder.length()).isEqualTo(5);
        assertThat(textNormalizedView.getText()).isEqualTo("abcde");
        assertThat(textNormalizedView.getOriginalEnd(4)).isEqualTo(5);
    }

    @Test
    @DisplayName("유닛 태그가 있는 코드 유닛으로 정규화 뷰 반환")
    void testBuild_givenUnitsWithTags_willReturnNormalizedView() {
        // given
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(1);

        // when
        builder.append('ㅅ', 0, 1, 0, 1, STANDALONE_JAMO, INITIAL, FINAL, true);
        builder.append('ㅏ', 1, 2, 1, 2, HANGUL_SYLLABLE, MEDIAL, MEDIAL, false);
        TextNormalizedView textNormalizedView = builder.build();

        // then
        assertThat(textNormalizedView.getSourceCharacterKind(0)).isEqualTo(STANDALONE_JAMO);
        assertThat(textNormalizedView.getFirstSourceJamoPosition(0)).isEqualTo(INITIAL);
        assertThat(textNormalizedView.getLastSourceJamoPosition(0)).isEqualTo(FINAL);
        assertThat(textNormalizedView.getSyllableEnds(0)).isTrue();
        assertThat(textNormalizedView.getSourceCharacterKind(1)).isEqualTo(HANGUL_SYLLABLE);
        assertThat(textNormalizedView.getSyllableEnds(1)).isFalse();
    }

    @Test
    @DisplayName("유닛 태그가 없는 코드 유닛으로 정규화 뷰 반환")
    void testBuild_givenUnitWithoutTags_willReturnNormalizedView() {
        // given
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(1);

        // when
        builder.append('a', 0, 1, 0, 1);
        TextNormalizedView textNormalizedView = builder.build();

        // then
        assertThat(textNormalizedView.getSourceCharacterKind(0)).isEqualTo(OTHER);
        assertThat(textNormalizedView.getFirstSourceJamoPosition(0)).isEqualTo(STANDALONE);
        assertThat(textNormalizedView.getLastSourceJamoPosition(0)).isEqualTo(STANDALONE);
        assertThat(textNormalizedView.getSyllableEnds(0)).isFalse();
    }

    @Test
    @DisplayName("0 용량으로 정규화 뷰 반환")
    void testBuild_givenZeroCapacityAndNoUnits_willReturnNormalizedView() {
        // given & when
        TextNormalizedView textNormalizedView = new TextNormalizedView.Builder(0).build();

        // then
        assertThat(textNormalizedView.getText()).isEmpty();
        assertThat(textNormalizedView.length()).isZero();
    }

    @Test
    @DisplayName("범위를 벗어난 인덱스로 예외 반환")
    void testGetOriginalStart_givenIndexOutOfRange_willThrowException() {
        // given
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(3);
        builder.append('a', 0, 1, 0, 1);
        TextNormalizedView textNormalizedView = builder.build();

        // when & then
        assertThatThrownBy(() -> textNormalizedView.getOriginalStart(1))
                .isInstanceOf(ArrayIndexOutOfBoundsException.class);
    }
}
