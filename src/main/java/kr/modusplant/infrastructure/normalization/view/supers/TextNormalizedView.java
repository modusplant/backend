package kr.modusplant.infrastructure.normalization.view.supers;

import lombok.Getter;

import java.util.Arrays;

// 정규화된 텍스트와, 인덱스 i의 코드 유닛이 유래한 경량 정규화 뷰 · 원본 텍스트의 구간 [start, end)을 담는 4개의 int[] 배열
// - 경량 정규화: 한 그래핌 클러스터가 만든 코드 유닛 모두가 그 클러스터의 원본 구간과, 그 클러스터가 경량 정규화 뷰에서 만든 코드 유닛 전체의 구간을 가짐 (ﬁ → fi: 두 유닛 모두 경량 [0, 2))
// - 강화·음절·키 입력 정규화: 한 코드 포인트를 이루는 코드 유닛 모두가, 그 코드 포인트를 만든 원천 코드 유닛들(병합·조합·흡수 시 여러 개)의 구간 합을 가짐
// - 행 머리 정규화: 각 코드 포인트가 경량 정규화 뷰에서 가진 구간을 그대로 유지함
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

    // 하위 클래스가 정규화 방식을 나타내도록, 원본 뷰의 불변 텍스트와 배열을 복사 없이 공유함
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
