package kr.modusplant.infrastructure.normalization;

import lombok.Getter;

import java.util.Arrays;

// 정규화 뷰와, 각 코드 유닛이 경량 정규화 뷰와 원본 텍스트에서 차지하는 구간
public final class TextNormalizedView {
    @Getter
    private final String text;
    private final int[] lightlyNormalizedStarts;
    private final int[] lightlyNormalizedEnds;
    private final int[] originalStarts;
    private final int[] originalEnds;

    private TextNormalizedView(String text, int[] lightlyNormalizedStarts, int[] lightlyNormalizedEnds,
                               int[] originalStarts, int[] originalEnds) {
        this.text = text;
        this.lightlyNormalizedStarts = lightlyNormalizedStarts;
        this.lightlyNormalizedEnds = lightlyNormalizedEnds;
        this.originalStarts = originalStarts;
        this.originalEnds = originalEnds;
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

    static final class Builder {
        private final StringBuilder text;
        private int[] lightlyNormalizedStarts;
        private int[] lightlyNormalizedEnds;
        private int[] originalStarts;
        private int[] originalEnds;

        Builder(int capacity) {
            int initialCapacity = Math.max(capacity, 1);
            text = new StringBuilder(initialCapacity);
            lightlyNormalizedStarts = new int[initialCapacity];
            lightlyNormalizedEnds = new int[initialCapacity];
            originalStarts = new int[initialCapacity];
            originalEnds = new int[initialCapacity];
        }

        int length() {
            return text.length();
        }

        void append(char unit, int lightlyNormalizedStart, int lightlyNormalizedEnd,
                    int originalStart, int originalEnd) {
            int index = text.length();
            if (index == lightlyNormalizedStarts.length) {
                int grownCapacity = lightlyNormalizedStarts.length * 2;
                lightlyNormalizedStarts = Arrays.copyOf(lightlyNormalizedStarts, grownCapacity);
                lightlyNormalizedEnds = Arrays.copyOf(lightlyNormalizedEnds, grownCapacity);
                originalStarts = Arrays.copyOf(originalStarts, grownCapacity);
                originalEnds = Arrays.copyOf(originalEnds, grownCapacity);
            }
            text.append(unit);
            lightlyNormalizedStarts[index] = lightlyNormalizedStart;
            lightlyNormalizedEnds[index] = lightlyNormalizedEnd;
            originalStarts[index] = originalStart;
            originalEnds[index] = originalEnd;
        }

        TextNormalizedView build() {
            int size = text.length();
            return new TextNormalizedView(
                    text.toString(),
                    Arrays.copyOf(lightlyNormalizedStarts, size),
                    Arrays.copyOf(lightlyNormalizedEnds, size),
                    Arrays.copyOf(originalStarts, size),
                    Arrays.copyOf(originalEnds, size));
        }
    }
}
