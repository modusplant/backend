package kr.modusplant.infrastructure.normalization;

import lombok.Getter;

import java.util.Arrays;

// 키 입력 뷰: 텍스트와 구간을 담은 정규화 뷰와, 각 코드 유닛의 원천 문자 종류, 첫·마지막 원천 자모 위치, 음절 끝 여부
public final class TextNormalizedKeystrokeView {
    public enum SourceKind {
        HANGUL_SYLLABLE, STANDALONE_JAMO, ASCII_LOWER_CASE_LETTER, OTHER
    }

    public enum JamoPosition {
        CHOSEONG, JUNGSEONG, JONGSEONG, STANDALONE
    }

    @Getter
    private final TextNormalizedView textNormalizedView;
    private final SourceKind[] sourceKinds;
    private final JamoPosition[] firstSourceJamoPositions;
    private final JamoPosition[] lastSourceJamoPositions;
    private final boolean[] syllableEnds;

    private TextNormalizedKeystrokeView(TextNormalizedView textNormalizedView,
                                        SourceKind[] sourceKinds,
                                        JamoPosition[] firstSourceJamoPositions,
                                        JamoPosition[] lastSourceJamoPositions,
                                        boolean[] syllableEnds) {
        this.textNormalizedView = textNormalizedView;
        this.sourceKinds = sourceKinds;
        this.firstSourceJamoPositions = firstSourceJamoPositions;
        this.lastSourceJamoPositions = lastSourceJamoPositions;
        this.syllableEnds = syllableEnds;
    }

    public SourceKind getSourceKind(int index) {
        return sourceKinds[index];
    }

    public JamoPosition getFirstSourceJamoPosition(int index) {
        return firstSourceJamoPositions[index];
    }

    public JamoPosition getLastSourceJamoPosition(int index) {
        return lastSourceJamoPositions[index];
    }

    public boolean isSyllableEnd(int index) {
        return syllableEnds[index];
    }

    static final class Builder {
        private final TextNormalizedView.Builder textNormalizedViewBuilder;
        private SourceKind[] sourceKinds;
        private JamoPosition[] firstSourceJamoPositions;
        private JamoPosition[] lastSourceJamoPositions;
        private boolean[] syllableEnds;

        Builder(int capacity) {
            int initialCapacity = Math.max(capacity, 1);
            textNormalizedViewBuilder = new TextNormalizedView.Builder(initialCapacity);
            sourceKinds = new SourceKind[initialCapacity];
            firstSourceJamoPositions = new JamoPosition[initialCapacity];
            lastSourceJamoPositions = new JamoPosition[initialCapacity];
            syllableEnds = new boolean[initialCapacity];
        }

        int length() {
            return textNormalizedViewBuilder.length();
        }

        void append(char unit, int lightlyNormalizedStart, int lightlyNormalizedEnd,
                    int originalStart, int originalEnd, SourceKind sourceKind,
                    JamoPosition firstSourceJamoPosition, JamoPosition lastSourceJamoPosition, boolean syllableEnd) {
            int index = textNormalizedViewBuilder.length();
            if (index == sourceKinds.length) {
                int grownCapacity = sourceKinds.length * 2;
                sourceKinds = Arrays.copyOf(sourceKinds, grownCapacity);
                firstSourceJamoPositions = Arrays.copyOf(firstSourceJamoPositions, grownCapacity);
                lastSourceJamoPositions = Arrays.copyOf(lastSourceJamoPositions, grownCapacity);
                syllableEnds = Arrays.copyOf(syllableEnds, grownCapacity);
            }
            textNormalizedViewBuilder.append(unit, lightlyNormalizedStart, lightlyNormalizedEnd,
                    originalStart, originalEnd);
            sourceKinds[index] = sourceKind;
            firstSourceJamoPositions[index] = firstSourceJamoPosition;
            lastSourceJamoPositions[index] = lastSourceJamoPosition;
            syllableEnds[index] = syllableEnd;
        }

        TextNormalizedKeystrokeView build() {
            int size = textNormalizedViewBuilder.length();
            return new TextNormalizedKeystrokeView(
                    textNormalizedViewBuilder.build(),
                    Arrays.copyOf(sourceKinds, size),
                    Arrays.copyOf(firstSourceJamoPositions, size),
                    Arrays.copyOf(lastSourceJamoPositions, size),
                    Arrays.copyOf(syllableEnds, size));
        }
    }
}
