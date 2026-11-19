package kr.modusplant.infrastructure.normalization;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.Normalizer2;
import kr.modusplant.infrastructure.normalization.view.*;
import kr.modusplant.infrastructure.normalization.view.supers.TextNormalizedView;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;

import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.*;
import static kr.modusplant.infrastructure.normalization.TextNormalizationCharacterMap.*;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.*;

@Component
public class TextNormalizer {
    public static final int MINIMUM_CONTRIBUTING_LINE_COUNT = 2;

    private static final int NOT_CODE_POINT = -1;
    private static final char CONJOINING_CHOSEONG_IEUNG = 'ᄋ';

    private final Normalizer2 nfdNormalizer;
    private final Normalizer2 nfcNormalizer;
    private final Normalizer2 nfkdNormalizer;
    private final Normalizer2 nfkcNormalizer;

    public TextNormalizer(@Qualifier(NFD_NORMALIZER_BEAN_NAME) Normalizer2 nfdNormalizer,
                          @Qualifier(NFC_NORMALIZER_BEAN_NAME) Normalizer2 nfcNormalizer,
                          @Qualifier(NFKD_NORMALIZER_BEAN_NAME) Normalizer2 nfkdNormalizer,
                          @Qualifier(NFKC_NORMALIZER_BEAN_NAME) Normalizer2 nfkcNormalizer) {
        this.nfdNormalizer = nfdNormalizer;
        this.nfcNormalizer = nfcNormalizer;
        this.nfkdNormalizer = nfkdNormalizer;
        this.nfkcNormalizer = nfkcNormalizer;
    }

    public String nfdNormalize(String stringIncludingKorean) {
        return nfdNormalizer.normalize(stringIncludingKorean);
    }

    public String nfcNormalize(String stringIncludingKorean) {
        return nfcNormalizer.normalize(stringIncludingKorean);
    }

    // 문자 체계를 바꾸지 않고 인코딩 수준의 변형만 제거함
    public LightlyNormalizedView normalizeLightly(String originalText) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(originalText.length());
        BreakIterator originalClusters = BreakIterator.getCharacterInstance();
        originalClusters.setText(originalText);
        int originalStart = originalClusters.first();
        for (int originalEnd = originalClusters.next();
             originalEnd != BreakIterator.DONE;
             originalStart = originalEnd, originalEnd = originalClusters.next()) {
            String canonicalCluster = canonicalize(originalText.substring(originalStart, originalEnd));
            int lightlyNormalizedStart = builder.length();
            int lightlyNormalizedEnd = lightlyNormalizedStart + canonicalCluster.length();
            for (int index = 0; index < canonicalCluster.length(); index++) {
                builder.append(canonicalCluster.charAt(index),
                        lightlyNormalizedStart, lightlyNormalizedEnd, originalStart, originalEnd);
            }
        }
        return new LightlyNormalizedView(builder.build());
    }

    // 단어를 숨기려고 의도적으로 가한 변형(문자 끼워 넣기, 닮은 문자로 치환, 자모를 풀어 쓰거나 반복하거나 늘여 쓰기)을 되돌림
    public AggressivelyNormalizedView normalizeAggressively(LightlyNormalizedView lightlyNormalizedView) {
        TextNormalizedView confusableMappedView = mapCrossScriptConfusablesToLatin(lightlyNormalizedView);
        TextNormalizedView nonLetterNoiseRemovedView = removeNonLetterNoise(confusableMappedView);
        TextNormalizedView repeatCollapsedView = collapseRepeatedJamo(nonLetterNoiseRemovedView);
        TextNormalizedView jamoComposedView = composeCompatibilityRunIntoSyllables(repeatCollapsedView);
        return new AggressivelyNormalizedView(mergeElongatedJamo(jamoComposedView));
    }

    public SyllableNormalizedView normalizeWithSyllable(AggressivelyNormalizedView aggressivelyNormalizedView,
                                                        LightlyNormalizedView lightlyNormalizedView) {
        TextNormalizedView jamoComposedView = composeCompatibilityJamoWithinTokens(aggressivelyNormalizedView, lightlyNormalizedView);
        return new SyllableNormalizedView(removeCompatibilityJamoAndAsciiLowerCaseLetters(jamoComposedView));
    }

    public KeystrokeNormalizedView normalizeWithKeystroke(AggressivelyNormalizedView aggressivelyNormalizedView) {
        TextNormalizedView asciiLowerCaseConvertedView = convertAsciiLowerCaseLettersToDubeolsikHangul(aggressivelyNormalizedView);
        TextNormalizedView repeatCollapsedView = collapseRepeatedJamo(asciiLowerCaseConvertedView);
        TextNormalizedView jamoComposedView = composeCompatibilityRunIntoSyllables(repeatCollapsedView);
        return new KeystrokeNormalizedView(mergeElongatedJamo(jamoComposedView));
    }

    // 각 행의 첫 글자를 세로로 이어 읽어야 드러나는 단어를 한 줄로 만듦
    public LineInitialNormalizedView normalizeWithLineInitial(LightlyNormalizedView lightlyNormalizedView) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(lightlyNormalizedView.length());
        int contributingLineCount = 0;
        boolean isLineInitialFound = true;
        int codeUnitStart = 0;
        for (int codePoint : lightlyNormalizedView.getText().codePoints().toArray()) {
            int codeUnitEnd = codeUnitStart + Character.charCount(codePoint);
            if (isLineTerminator(codePoint)) {
                isLineInitialFound = true;
            } else if (isLineInitialFound && !isNonLetterNoiseCategory(codePoint)) {
                appendCodePoint(builder, codePoint, lightlyNormalizedView, codeUnitStart, codeUnitEnd);
                isLineInitialFound = false;
                contributingLineCount++;
            }
            codeUnitStart = codeUnitEnd;
        }
        return new LineInitialNormalizedView(contributingLineCount >= MINIMUM_CONTRIBUTING_LINE_COUNT ?
                builder.build() : new TextNormalizedView.Builder(0).build()
        );
    }

    private String canonicalize(String originalCluster) {
        String unmarkedText = nfkdNormalizer.normalize(originalCluster).codePoints()
                .filter(codePoint -> !isDICP(codePoint) && !isCombiningMark(codePoint))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return nfkcNormalizer.normalize(unmarkedText).codePoints()
                .map(codePoint -> CONJOINING_COMPATIBILITY_JAMO_MAP.getOrDefault(codePoint, codePoint))
                .map(codePoint -> LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.getOrDefault(codePoint, codePoint))
                .map(UCharacter::toLowerCase)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private TextNormalizedView mapCrossScriptConfusablesToLatin(TextNormalizedView sourceView) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codeUnitStart = 0;
        for (int codePoint : sourceView.getText().codePoints().toArray()) {
            int codeUnitEnd = codeUnitStart + Character.charCount(codePoint);
            appendCodePoint(builder, CROSS_SCRIPT_CONFUSABLE_LATIN_MAP.getOrDefault(codePoint, codePoint),
                    sourceView, codeUnitStart, codeUnitEnd);
            codeUnitStart = codeUnitEnd;
        }
        return builder.build();
    }

    private TextNormalizedView convertAsciiLowerCaseLettersToDubeolsikHangul(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        // ASCII 소문자가 아닌 위치는 NOT_CODE_POINT를 두어, 그 위치에서 음절 조합이 끊기게 함
        int[] convertedJamos = Arrays.stream(codePoints)
                .map(codePoint -> ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP.getOrDefault(codePoint, NOT_CODE_POINT))
                .toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codeUnitStart = 0;
        int codePointIndex = 0;
        while (codePointIndex < codePoints.length) {
            if (!isAsciiLowerCaseLetter(codePoints[codePointIndex])) {
                int codeUnitEnd = codeUnitStart + Character.charCount(codePoints[codePointIndex]);
                appendCodePoint(builder, codePoints[codePointIndex], sourceView,
                        codeUnitStart, codeUnitEnd);
                codeUnitStart = codeUnitEnd;
                codePointIndex++;
                continue;
            }
            // 음절은 convertedJamos로 조합하되, isVowel 판정은 codePoints를 읽음
            ComposedCodePoint composedCodePoint = composeLeadingJamo(convertedJamos, codePointIndex,
                    jamoIndex -> jamoIndex < codePoints.length && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(
                            ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP.getOrDefault(codePoints[jamoIndex], codePoints[jamoIndex])));
            // ASCII 소문자는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
            appendCodePoint(builder, composedCodePoint.codePoint(), sourceView,
                    codeUnitStart, codeUnitStart + composedCodePoint.jamoCount());
            codeUnitStart += composedCodePoint.jamoCount();
            codePointIndex += composedCodePoint.jamoCount();
        }
        return builder.build();
    }

    // 자모 배열의 시작 위치에서 음절 하나를 조합하며, 조합하지 못하면 첫 자모를 그대로 반환함
    private ComposedCodePoint composeLeadingJamo(int[] jamos, int jamoStartIndex, IntPredicate isVowel) {
        int firstJamo = jamoAt(jamos, jamoStartIndex);
        boolean startsWithLV = COMPATIBILITY_CONJOINING_CHOSEONG_MAP.containsKey(firstJamo)
                && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(jamoAt(jamos, jamoStartIndex + 1));
        if (!startsWithLV && !COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(firstJamo)) {
            return new ComposedCodePoint(firstJamo, 1);
        }
        int jamoIndex = startsWithLV ? jamoStartIndex + 1 : jamoStartIndex;
        int vowelJamo = jamoAt(jamos, jamoIndex);
        Integer compoundVowelJamo = composeCompoundJamo(vowelJamo, jamoAt(jamos, jamoIndex + 1));
        if (compoundVowelJamo != null) {
            vowelJamo = compoundVowelJamo;
            jamoIndex++;
        }
        jamoIndex++;
        if (!startsWithLV) {
            return new ComposedCodePoint(vowelJamo, jamoIndex - jamoStartIndex);
        }
        int finalJamo = NOT_CODE_POINT;
        int firstFinalJamo = jamoAt(jamos, jamoIndex);
        if (COMPATIBILITY_CONJOINING_JONGSEONG_MAP.containsKey(firstFinalJamo) && !isVowel.test(jamoIndex + 1)) {
            Integer compoundFinalJamo = composeCompoundJamo(firstFinalJamo, jamoAt(jamos, jamoIndex + 1));
            if (compoundFinalJamo != null && !isVowel.test(jamoIndex + 2)) {
                finalJamo = compoundFinalJamo;
                jamoIndex++;
            } else {
                finalJamo = firstFinalJamo;
            }
            jamoIndex++;
        }
        return new ComposedCodePoint(composeSyllable(firstJamo, vowelJamo, finalJamo), jamoIndex - jamoStartIndex);
    }

    private int jamoAt(int[] jamos, int jamoIndex) {
        return jamoIndex < jamos.length ? jamos[jamoIndex] : NOT_CODE_POINT;
    }

    private Integer composeCompoundJamo(int firstJamo, int secondJamo) {
        if (secondJamo == NOT_CODE_POINT) {
            return null;
        }
        return COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP
                .get(Character.toString(firstJamo) + Character.toString(secondJamo));
    }

    private TextNormalizedView composeCompatibilityJamoWithinTokens(TextNormalizedView sourceView,
                                                                    LightlyNormalizedView lightlyNormalizedView) {
        int[] tokenIndexes = getTokenIndexes(lightlyNormalizedView.getText());
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codeUnitStart = 0;
        int codePointIndex = 0;
        while (codePointIndex < codePoints.length) {
            int codePoint = codePoints[codePointIndex];
            if (isHangulSyllableWithoutFinal(codePoint)) {
                int syllableCodePointIndex = codePointIndex;
                int syllableCodeUnitStart = codeUnitStart;
                int syllableTokenIndex = tokenIndexes[sourceView.getLightlyNormalizedStart(syllableCodeUnitStart)];
                // 음절과 호환 자모는 모두 BMP 문자이므로, 음절부터 이어지는 호환 자모까지는 배열 인덱스 차이가 코드 유닛 차이와 같음
                ComposedCodePoint composedCodePoint = attachFollowingFinalJamo(codePoints, syllableCodePointIndex,
                        tempCodePointIndex ->
                                tempCodePointIndex < codePoints.length &&
                                        isCompatibilityJamo(codePoints[tempCodePointIndex]) &&
                                        tokenIndexes[sourceView.getLightlyNormalizedStart(
                                                syllableCodeUnitStart + tempCodePointIndex - syllableCodePointIndex)
                                                ] == syllableTokenIndex);
                appendCodePoint(builder, composedCodePoint.codePoint(), sourceView,
                        codeUnitStart, codeUnitStart + composedCodePoint.jamoCount());
                codeUnitStart += composedCodePoint.jamoCount();
                codePointIndex += composedCodePoint.jamoCount();
                continue;
            }
            if (!isCompatibilityJamo(codePoint)) {
                int codeUnitEnd = codeUnitStart + Character.charCount(codePoint);
                appendCodePoint(builder, codePoint, sourceView, codeUnitStart, codeUnitEnd);
                codeUnitStart = codeUnitEnd;
                codePointIndex++;
                continue;
            }
            // 호환 자모는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
            int tokenIndex = tokenIndexes[sourceView.getLightlyNormalizedStart(codeUnitStart)];
            int runEndIndex = codePointIndex + 1;
            while (runEndIndex < codePoints.length && isCompatibilityJamo(codePoints[runEndIndex])
                    && tokenIndexes[sourceView.getLightlyNormalizedStart(
                            codeUnitStart + runEndIndex - codePointIndex)] == tokenIndex) {
                runEndIndex++;
            }
            int[] runJamos = Arrays.copyOfRange(codePoints, codePointIndex, runEndIndex);
            int runJamoIndex = 0;
            while (runJamoIndex < runJamos.length) {
                ComposedCodePoint composedCodePoint = composeLeadingJamo(runJamos, runJamoIndex,
                        jamoIndex -> jamoIndex < runJamos.length
                                && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(runJamos[jamoIndex]));
                appendCodePoint(builder, composedCodePoint.codePoint(), sourceView,
                        codeUnitStart, codeUnitStart + composedCodePoint.jamoCount());
                codeUnitStart += composedCodePoint.jamoCount();
                runJamoIndex += composedCodePoint.jamoCount();
            }
            codePointIndex = runEndIndex;
        }
        return builder.build();
    }

    // 모든 위치의 자모 여부는 그 앞 위치가 같은 토큰의 자모일 때만 판정하므로, 판정 위치까지는 모두 BMP 문자임
    private ComposedCodePoint attachFollowingFinalJamo(int[] codePoints, int syllableCodePointIndex,
                                                  IntPredicate doesJamoIndexMatchWithCondition) {
        int syllable = codePoints[syllableCodePointIndex];
        int firstFinalIndex = syllableCodePointIndex + 1;
        if (!doesJamoIndexMatchWithCondition.test(firstFinalIndex)
                || !COMPATIBILITY_CONJOINING_JONGSEONG_MAP.containsKey(codePoints[firstFinalIndex])
                || doesVowelIndexMatch(codePoints, firstFinalIndex + 1, doesJamoIndexMatchWithCondition)) {
            return new ComposedCodePoint(syllable, 1);
        }
        int finalJamo = codePoints[firstFinalIndex];
        int jamoCount = 2;
        if (doesJamoIndexMatchWithCondition.test(firstFinalIndex + 1)) {
            Integer compoundFinalJamo = composeCompoundJamo(finalJamo, codePoints[firstFinalIndex + 1]);
            if (compoundFinalJamo != null
                    && !doesVowelIndexMatch(codePoints, firstFinalIndex + 2, doesJamoIndexMatchWithCondition)) {
                finalJamo = compoundFinalJamo;
                jamoCount = 3;
            }
        }
        StringBuilder conjoiningJamo = new StringBuilder(nfdNormalizer.normalize(Character.toString(syllable)))
                .appendCodePoint(COMPATIBILITY_CONJOINING_JONGSEONG_MAP.get(finalJamo));
        return new ComposedCodePoint(nfcNormalizer.normalize(conjoiningJamo).codePointAt(0), jamoCount);
    }

    private boolean doesVowelIndexMatch(int[] codePoints, int potentialVowelIndex, IntPredicate doesJamoIndexMatchWithCondition) {
        return doesJamoIndexMatchWithCondition.test(potentialVowelIndex)
                && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(codePoints[potentialVowelIndex]);
    }

    // 경량 정규화 텍스트의 각 코드 유닛에 대하여 공백으로 나뉜 토큰의 인덱스를 기록함
    private int[] getTokenIndexes(String lightlyNormalizedText) {
        int[] tokenIndexes = new int[lightlyNormalizedText.length()];
        int tokenIndex = 0;
        boolean pointsTokenBoundary = true;
        int codeUnitIndex = 0;
        while (codeUnitIndex < lightlyNormalizedText.length()) {
            int codePoint = lightlyNormalizedText.codePointAt(codeUnitIndex);
            int nextCodeUnitIndex = codeUnitIndex + Character.charCount(codePoint);
            if (UCharacter.isUWhiteSpace(codePoint)) {
                pointsTokenBoundary = true;
            } else if (pointsTokenBoundary) {
                tokenIndex++;
                pointsTokenBoundary = false;
            }
            Arrays.fill(tokenIndexes, codeUnitIndex, nextCodeUnitIndex, tokenIndex);
            codeUnitIndex = nextCodeUnitIndex;
        }
        return tokenIndexes;
    }

    private TextNormalizedView removeCompatibilityJamoAndAsciiLowerCaseLetters(TextNormalizedView sourceView) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codeUnitStart = 0;
        for (int codePoint : sourceView.getText().codePoints().toArray()) {
            int codeUnitEnd = codeUnitStart + Character.charCount(codePoint);
            if (!isCompatibilityJamo(codePoint) && !isAsciiLowerCaseLetter(codePoint)) {
                appendCodePoint(builder, codePoint, sourceView, codeUnitStart, codeUnitEnd);
            }
            codeUnitStart = codeUnitEnd;
        }
        return builder.build();
    }

    private TextNormalizedView removeNonLetterNoise(TextNormalizedView sourceView) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codeUnitStart = 0;
        for (int codePoint : sourceView.getText().codePoints().toArray()) {
            int codeUnitEnd = codeUnitStart + Character.charCount(codePoint);
            if (!isNonLetterNoiseCategory(codePoint)) {
                appendCodePoint(builder, codePoint, sourceView, codeUnitStart, codeUnitEnd);
            }
            codeUnitStart = codeUnitEnd;
        }
        return builder.build();
    }

    // 서로게이트 쌍인 코드 포인트도 자기 코드 유닛 수만큼 구간을 넘기도록 코드 포인트 단위로 순회
    private TextNormalizedView collapseRepeatedJamo(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codeUnitStart = 0;
        int codePointIndex = 0;
        while (codePointIndex < codePoints.length) {
            int codePoint = codePoints[codePointIndex];
            int codeUnitEnd = codeUnitStart + Character.charCount(codePoint);
            codePointIndex++;
            boolean isJamo = isCompatibilityJamo(codePoint) || isConjoiningJamo(codePoint);
            while (isJamo && codePointIndex < codePoints.length && codePoints[codePointIndex] == codePoint) {
                codeUnitEnd += Character.charCount(codePoint);
                codePointIndex++;
            }
            appendCodePoint(builder, codePoint, sourceView, codeUnitStart, codeUnitEnd);
            codeUnitStart = codeUnitEnd;
        }
        return builder.build();
    }

    private TextNormalizedView composeCompatibilityRunIntoSyllables(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codeUnitStart = 0;
        int codePointIndex = 0;
        while (codePointIndex < codePoints.length) {
            int runEndIndex = codePointIndex;
            while (runEndIndex < codePoints.length && isCompatibilityJamo(codePoints[runEndIndex])) {
                runEndIndex++;
            }
            List<Integer> syllableJamoCounts = segmentCompatibilityJamoRunIntoSyllables(codePoints, codePointIndex, runEndIndex);
            // 호환 자모는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
            for (int jamoCount : syllableJamoCounts) {
                int syllable = composeSyllable(codePoints[codePointIndex], codePoints[codePointIndex + 1],
                        jamoCount == 3 ? codePoints[codePointIndex + 2] : NOT_CODE_POINT);
                appendCodePoint(builder, syllable, sourceView, codeUnitStart, codeUnitStart + jamoCount);
                codeUnitStart += jamoCount;
                codePointIndex += jamoCount;
            }
            if (syllableJamoCounts.isEmpty()) {
                int terminalEndIndex = Math.max(runEndIndex, codePointIndex + 1);
                for (; codePointIndex < terminalEndIndex; codePointIndex++) {
                    int codeUnitEnd = codeUnitStart + Character.charCount(codePoints[codePointIndex]);
                    appendCodePoint(builder, codePoints[codePointIndex], sourceView, codeUnitStart, codeUnitEnd);
                    codeUnitStart = codeUnitEnd;
                }
            }
        }
        return builder.build();
    }

    // 연속 전체가 음절로 나뉘지 않으면 빈 목록을 반환함
    private List<Integer> segmentCompatibilityJamoRunIntoSyllables(int[] codePoints, int runStart, int runEnd) {
        List<Integer> syllableJamoCounts = new ArrayList<>();
        int cursor = runStart;
        while (cursor < runEnd) {
            if (cursor + 1 == runEnd
                    || !COMPATIBILITY_CONJOINING_CHOSEONG_MAP.containsKey(codePoints[cursor])
                    || !COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(codePoints[cursor + 1])) {
                return List.of();
            }
            int finalIndex = cursor + 2;
            boolean hasFinal = finalIndex < runEnd
                    && COMPATIBILITY_CONJOINING_JONGSEONG_MAP.containsKey(codePoints[finalIndex])
                    && !(finalIndex + 1 < runEnd && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(codePoints[finalIndex + 1]));
            int jamoCount = hasFinal ? 3 : 2;
            syllableJamoCounts.add(jamoCount);
            cursor += jamoCount;
        }
        return syllableJamoCounts;
    }

    private int composeSyllable(int initialJamo, int vowelJamo, int finalJamo) {
        StringBuilder conjoiningJamo = new StringBuilder(3)
                .appendCodePoint(COMPATIBILITY_CONJOINING_CHOSEONG_MAP.get(initialJamo))
                .appendCodePoint(COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.get(vowelJamo));
        if (finalJamo != NOT_CODE_POINT) {
            conjoiningJamo.appendCodePoint(COMPATIBILITY_CONJOINING_JONGSEONG_MAP.get(finalJamo));
        }
        return nfcNormalizer.normalize(conjoiningJamo).codePointAt(0);
    }

    private TextNormalizedView mergeElongatedJamo(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codePointIndex = 0;
        int codeUnitStart = 0;
        while (codePointIndex < codePoints.length) {
            int mergedCodePoint = codePoints[codePointIndex];
            int codeUnitEnd = codeUnitStart + Character.charCount(mergedCodePoint);
            codePointIndex++;
            while (codePointIndex < codePoints.length) {
                int afterNextCodePoint = codePointIndex + 1 < codePoints.length ? codePoints[codePointIndex + 1] : NOT_CODE_POINT;
                int absorbedCodePoint = absorbElongatedJamo(mergedCodePoint, codePoints[codePointIndex], afterNextCodePoint);
                if (absorbedCodePoint == NOT_CODE_POINT) {
                    break;
                }
                mergedCodePoint = absorbedCodePoint;
                codeUnitEnd += Character.charCount(codePoints[codePointIndex]);
                codePointIndex++;
            }
            appendCodePoint(builder, mergedCodePoint, sourceView, codeUnitStart, codeUnitEnd);
            codeUnitStart = codeUnitEnd;
        }
        return builder.build();
    }

    private int absorbElongatedJamo(int mergedCodePoint, int nextCodePoint, int afterNextCodePoint) {
        int absorbedCodePoint = absorbElongatedVowel(mergedCodePoint, nextCodePoint);
        if (absorbedCodePoint != NOT_CODE_POINT) {
            return absorbedCodePoint;
        }
        return isElongatedConsonant(mergedCodePoint, nextCodePoint, afterNextCodePoint)
                ? mergedCodePoint : NOT_CODE_POINT;
    }

    private int absorbElongatedVowel(int mergedCodePoint, int nextCodePoint) {
        if (!isHangulSyllableWithoutFinal(mergedCodePoint)) {
            return NOT_CODE_POINT;
        }
        String mergedJamo = nfdNormalizer.normalize(Character.toString(mergedCodePoint));
        int vowel = mergedJamo.charAt(1);
        if (isCompatibilityJamo(nextCodePoint)) {
            return CONJOINING_COMPATIBILITY_JAMO_MAP.get(vowel) == nextCodePoint ? mergedCodePoint : NOT_CODE_POINT;
        }
        if (!isHangulSyllable(nextCodePoint)) {
            return NOT_CODE_POINT;
        }
        String nextJamo = nfdNormalizer.normalize(Character.toString(nextCodePoint));
        if (nextJamo.charAt(0) != CONJOINING_CHOSEONG_IEUNG || nextJamo.charAt(1) != vowel) {
            return NOT_CODE_POINT;
        }
        if (nextJamo.length() == 2) {
            return mergedCodePoint;
        }
        return nfcNormalizer.normalize(mergedJamo + nextJamo.charAt(2)).codePointAt(0);
    }

    private boolean isElongatedConsonant(int mergedCodePoint, int nextCodePoint, int afterNextCodePoint) {
        if (afterNextCodePoint == NOT_CODE_POINT || !isHangulSyllable(mergedCodePoint)
                || !isCompatibilityJamo(nextCodePoint) || !isHangulSyllable(afterNextCodePoint)) {
            return false;
        }
        String mergedJamo = nfdNormalizer.normalize(Character.toString(mergedCodePoint));
        String afterNextJamo = nfdNormalizer.normalize(Character.toString(afterNextCodePoint));
        boolean isNextEqualToMergedFinal = mergedJamo.length() == 3
                && CONJOINING_COMPATIBILITY_JAMO_MAP.get((int) mergedJamo.charAt(2)) == nextCodePoint;
        boolean isNextEqualToAfterNextInitial =
                CONJOINING_COMPATIBILITY_JAMO_MAP.get((int) afterNextJamo.charAt(0)) == nextCodePoint;
        return isNextEqualToMergedFinal || isNextEqualToAfterNextInitial;
    }

    // 합쳐진 코드 유닛도 원본 순서를 유지하므로 구간 합은 첫 유닛의 시작과 마지막 유닛의 끝으로 결정됨
    private void appendCodePoint(TextNormalizedView.Builder builder, int codePoint, TextNormalizedView sourceView,
                                 int codeUnitStart, int codeUnitEnd) {
        int lightlyNormalizedStart = sourceView.getLightlyNormalizedStart(codeUnitStart);
        int lightlyNormalizedEnd = sourceView.getLightlyNormalizedEnd(codeUnitEnd - 1);
        int originalStart = sourceView.getOriginalStart(codeUnitStart);
        int originalEnd = sourceView.getOriginalEnd(codeUnitEnd - 1);
        for (char codeUnit : Character.toChars(codePoint)) {
            builder.append(codeUnit, lightlyNormalizedStart, lightlyNormalizedEnd, originalStart, originalEnd);
        }
    }

    // 조합한 음절 또는 조합하지 못한 자모 하나와 그것을 만든 자모 수
    private record ComposedCodePoint(int codePoint, int jamoCount) {
    }
}
