package kr.modusplant.infrastructure.normalization;

import lombok.Getter;

import java.util.Arrays;

import static kr.modusplant.infrastructure.normalization.TextNormalizedView.JamoPosition.STANDALONE;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.SourceCharacterKind.OTHER;

// 정규화 뷰와, 각 UTF-16 코드 유닛이 경량 정규화 뷰와 원본 텍스트에서 차지하는 반열린 구간 및 키 입력 뷰의 유닛 태그
public final class TextNormalizedView {
    public enum SourceCharacterKind {
        HANGUL_SYLLABLE, STANDALONE_JAMO, LATIN_LETTER, OTHER
    }

    public enum JamoPosition {
        INITIAL, MEDIAL, FINAL, STANDALONE
    }

    @Getter
    private final String text;
    private final int[] lightlyNormalizedStarts;
    private final int[] lightlyNormalizedEnds;
    private final int[] originalStarts;
    private final int[] originalEnds;
    private final SourceCharacterKind[] sourceCharacterKinds;
    private final JamoPosition[] firstSourceJamoPositions;
    private final JamoPosition[] lastSourceJamoPositions;
    private final boolean[] syllableEnds;

    private TextNormalizedView(String text, int[] lightlyNormalizedStarts, int[] lightlyNormalizedEnds,
                               int[] originalStarts, int[] originalEnds, SourceCharacterKind[] sourceCharacterKinds,
                               JamoPosition[] firstSourceJamoPositions, JamoPosition[] lastSourceJamoPositions,
                               boolean[] syllableEnds) {
        this.text = text;
        this.lightlyNormalizedStarts = lightlyNormalizedStarts;
        this.lightlyNormalizedEnds = lightlyNormalizedEnds;
        this.originalStarts = originalStarts;
        this.originalEnds = originalEnds;
        this.sourceCharacterKinds = sourceCharacterKinds;
        this.firstSourceJamoPositions = firstSourceJamoPositions;
        this.lastSourceJamoPositions = lastSourceJamoPositions;
        this.syllableEnds = syllableEnds;
    }

    public int length() {
        return text.length();
    }

    public int getLightlyNormalizedStart(int index) {
        return lightlyNormalizedStarts[index];
    }

    public int getLightlyNormalizedEnd(int index) {
        return lightlyNormalizedEnds[index];
    }

    public int getOriginalStart(int index) {
        return originalStarts[index];
    }

    public int getOriginalEnd(int index) {
        return originalEnds[index];
    }

    public SourceCharacterKind getSourceCharacterKind(int index) {
        return sourceCharacterKinds[index];
    }

    public JamoPosition getFirstSourceJamoPosition(int index) {
        return firstSourceJamoPositions[index];
    }

    public JamoPosition getLastSourceJamoPosition(int index) {
        return lastSourceJamoPositions[index];
    }

    public boolean getSyllableEnds(int index) {
        return syllableEnds[index];
    }

    static final class Builder {
        private final StringBuilder text;
        private int[] lightlyNormalizedStarts;
        private int[] lightlyNormalizedEnds;
        private int[] originalStarts;
        private int[] originalEnds;
        private SourceCharacterKind[] sourceCharacterKinds;
        private JamoPosition[] firstSourceJamoPositions;
        private JamoPosition[] lastSourceJamoPositions;
        private boolean[] syllableEnds;

        Builder(int capacity) {
            int initialCapacity = Math.max(capacity, 1);
            this.text = new StringBuilder(initialCapacity);
            this.lightlyNormalizedStarts = new int[initialCapacity];
            this.lightlyNormalizedEnds = new int[initialCapacity];
            this.originalStarts = new int[initialCapacity];
            this.originalEnds = new int[initialCapacity];
            this.sourceCharacterKinds = new SourceCharacterKind[initialCapacity];
            this.firstSourceJamoPositions = new JamoPosition[initialCapacity];
            this.lastSourceJamoPositions = new JamoPosition[initialCapacity];
            this.syllableEnds = new boolean[initialCapacity];
        }

        int length() {
            return text.length();
        }

        void append(char unit, int lightlyNormalizedStart, int lightlyNormalizedEnd,
                    int originalStart, int originalEnd) {
            append(unit, lightlyNormalizedStart, lightlyNormalizedEnd, originalStart, originalEnd,
                    OTHER, STANDALONE, STANDALONE, false);
        }

        void append(char unit, int lightlyNormalizedStart, int lightlyNormalizedEnd,
                    int originalStart, int originalEnd, SourceCharacterKind sourceCharacterKind,
                    JamoPosition firstSourceJamoPosition, JamoPosition lastSourceJamoPosition, boolean syllableEnds) {
            int index = text.length();
            if (index == lightlyNormalizedStarts.length) {
                int grownCapacity = lightlyNormalizedStarts.length * 2;
                lightlyNormalizedStarts = Arrays.copyOf(lightlyNormalizedStarts, grownCapacity);
                lightlyNormalizedEnds = Arrays.copyOf(lightlyNormalizedEnds, grownCapacity);
                originalStarts = Arrays.copyOf(originalStarts, grownCapacity);
                originalEnds = Arrays.copyOf(originalEnds, grownCapacity);
                sourceCharacterKinds = Arrays.copyOf(sourceCharacterKinds, grownCapacity);
                firstSourceJamoPositions = Arrays.copyOf(firstSourceJamoPositions, grownCapacity);
                lastSourceJamoPositions = Arrays.copyOf(lastSourceJamoPositions, grownCapacity);
                this.syllableEnds = Arrays.copyOf(this.syllableEnds, grownCapacity);
            }
            text.append(unit);
            lightlyNormalizedStarts[index] = lightlyNormalizedStart;
            lightlyNormalizedEnds[index] = lightlyNormalizedEnd;
            originalStarts[index] = originalStart;
            originalEnds[index] = originalEnd;
            sourceCharacterKinds[index] = sourceCharacterKind;
            firstSourceJamoPositions[index] = firstSourceJamoPosition;
            lastSourceJamoPositions[index] = lastSourceJamoPosition;
            this.syllableEnds[index] = syllableEnds;
        }

        TextNormalizedView build() {
            int size = text.length();
            return new TextNormalizedView(
                    text.toString(),
                    Arrays.copyOf(lightlyNormalizedStarts, size),
                    Arrays.copyOf(lightlyNormalizedEnds, size),
                    Arrays.copyOf(originalStarts, size),
                    Arrays.copyOf(originalEnds, size),
                    Arrays.copyOf(sourceCharacterKinds, size),
                    Arrays.copyOf(firstSourceJamoPositions, size),
                    Arrays.copyOf(lastSourceJamoPositions, size),
                    Arrays.copyOf(syllableEnds, size));
        }
    }
}
