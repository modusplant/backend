package kr.modusplant.infrastructure.normalization.view.supers;

import lombok.Getter;

import java.util.Arrays;

public class TextNormalizedView {
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

    protected TextNormalizedView(TextNormalizedView source) {
        this(source.text, source.lightlyNormalizedStarts, source.lightlyNormalizedEnds,
                source.originalStarts, source.originalEnds);
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

    public static final class Builder {
        private final StringBuilder text;
        private int[] lightlyNormalizedStarts;
        private int[] lightlyNormalizedEnds;
        private int[] originalStarts;
        private int[] originalEnds;

        public Builder(int capacity) {
            int initialCapacity = Math.max(capacity, 1);
            text = new StringBuilder(initialCapacity);
            lightlyNormalizedStarts = new int[initialCapacity];
            lightlyNormalizedEnds = new int[initialCapacity];
            originalStarts = new int[initialCapacity];
            originalEnds = new int[initialCapacity];
        }

        public int length() {
            return text.length();
        }

        public void append(char codeUnit, int lightlyNormalizedStart, int lightlyNormalizedEnd,
                    int originalStart, int originalEnd) {
            int indexLength = text.length();
            if (indexLength == lightlyNormalizedStarts.length) {
                int grownCapacity = lightlyNormalizedStarts.length * 2;
                lightlyNormalizedStarts = Arrays.copyOf(lightlyNormalizedStarts, grownCapacity);
                lightlyNormalizedEnds = Arrays.copyOf(lightlyNormalizedEnds, grownCapacity);
                originalStarts = Arrays.copyOf(originalStarts, grownCapacity);
                originalEnds = Arrays.copyOf(originalEnds, grownCapacity);
            }
            text.append(codeUnit);
            lightlyNormalizedStarts[indexLength] = lightlyNormalizedStart;
            lightlyNormalizedEnds[indexLength] = lightlyNormalizedEnd;
            originalStarts[indexLength] = originalStart;
            originalEnds[indexLength] = originalEnd;
        }

        public TextNormalizedView build() {
            int indexLength = text.length();
            return new TextNormalizedView(
                    text.toString(),
                    Arrays.copyOf(lightlyNormalizedStarts, indexLength),
                    Arrays.copyOf(lightlyNormalizedEnds, indexLength),
                    Arrays.copyOf(originalStarts, indexLength),
                    Arrays.copyOf(originalEnds, indexLength));
        }
    }
}
