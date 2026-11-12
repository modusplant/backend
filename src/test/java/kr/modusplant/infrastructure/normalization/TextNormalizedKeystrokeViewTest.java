package kr.modusplant.infrastructure.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.CHOSEONG;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.JONGSEONG;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.JUNGSEONG;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.STANDALONE;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.SourceKind.HANGUL_SYLLABLE;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.SourceKind.STANDALONE_JAMO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextNormalizedKeystrokeViewTest {
    @Test
    @DisplayName("유닛 태그가 있는 코드 유닛 추가로 키 입력 뷰 반환")
    void testBuild_givenUnitsWithTags_willReturnKeystrokeView() {
        // given
        TextNormalizedKeystrokeView.Builder builder = new TextNormalizedKeystrokeView.Builder(2);

        // when
        builder.append('ㅅ', 0, 1, 0, 2, STANDALONE_JAMO, CHOSEONG, JONGSEONG, true);
        builder.append('ㅏ', 1, 2, 2, 3, HANGUL_SYLLABLE, JUNGSEONG, JUNGSEONG, false);
        TextNormalizedKeystrokeView keystrokeView = builder.build();

        // then
        TextNormalizedView textNormalizedView = keystrokeView.getTextNormalizedView();
        assertThat(textNormalizedView.getText()).isEqualTo("ㅅㅏ");
        assertThat(textNormalizedView.getLightlyNormalizedEnd(1)).isEqualTo(2);
        assertThat(textNormalizedView.getOriginalEnd(0)).isEqualTo(2);
        assertThat(keystrokeView.getSourceKind(0)).isEqualTo(STANDALONE_JAMO);
        assertThat(keystrokeView.getFirstSourceJamoPosition(0)).isEqualTo(CHOSEONG);
        assertThat(keystrokeView.getLastSourceJamoPosition(0)).isEqualTo(JONGSEONG);
        assertThat(keystrokeView.isSyllableEnd(0)).isTrue();
        assertThat(keystrokeView.getSourceKind(1)).isEqualTo(HANGUL_SYLLABLE);
        assertThat(keystrokeView.isSyllableEnd(1)).isFalse();
    }

    @Test
    @DisplayName("초기 용량 초과로 키 입력 뷰 반환")
    void testBuild_givenUnitsExceedingCapacity_willReturnKeystrokeView() {
        // given
        TextNormalizedKeystrokeView.Builder builder = new TextNormalizedKeystrokeView.Builder(1);

        // when
        for (int index = 0; index < 5; index++) {
            builder.append((char) ('a' + index), index, index + 1, index, index + 1,
                    STANDALONE_JAMO, STANDALONE, STANDALONE, index == 4);
        }
        TextNormalizedKeystrokeView keystrokeView = builder.build();

        // then
        assertThat(builder.length()).isEqualTo(5);
        assertThat(keystrokeView.getTextNormalizedView().getText()).isEqualTo("abcde");
        assertThat(keystrokeView.getSourceKind(4)).isEqualTo(STANDALONE_JAMO);
        assertThat(keystrokeView.isSyllableEnd(4)).isTrue();
    }

    @Test
    @DisplayName("0 용량으로 키 입력 뷰 반환")
    void testBuild_givenZeroCapacityAndNoUnits_willReturnKeystrokeView() {
        // given & when
        TextNormalizedKeystrokeView keystrokeView = new TextNormalizedKeystrokeView.Builder(0).build();

        // then
        assertThat(keystrokeView.getTextNormalizedView().getText()).isEmpty();
    }

    @Test
    @DisplayName("범위를 벗어난 인덱스로 예외 반환")
    void testGetSourceKind_givenIndexOutOfRange_willThrowException() {
        // given
        TextNormalizedKeystrokeView.Builder builder = new TextNormalizedKeystrokeView.Builder(3);
        builder.append('ㅅ', 0, 1, 0, 1, STANDALONE_JAMO, STANDALONE, STANDALONE, false);
        TextNormalizedKeystrokeView keystrokeView = builder.build();

        // when & then
        assertThatThrownBy(() -> keystrokeView.getSourceKind(1))
                .isInstanceOf(ArrayIndexOutOfBoundsException.class);
    }
}
