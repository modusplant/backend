package kr.modusplant.infrastructure.normalization;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.Normalizer2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.*;
import static kr.modusplant.infrastructure.normalization.TextNormalizationCharacterMap.*;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.*;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.JamoPosition.*;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.SourceCharacterKind.*;

@Component
public class TextNormalizer {
    private static final int NOT_CONVERTED_JAMO = -1;
    private static final int NOT_FINAL_JAMO = -1;
    private static final int NOT_MERGED_CODE_POINT = -1;
    private static final int MINIMUM_CONTRIBUTING_LINE_COUNT = 2;
    private static final char CHOSEONG_IEUNG = 'ᄋ';
    private static final List<TextNormalizedView.JamoPosition> SYLLABLE_JAMO_POSITIONS = List.of(INITIAL, MEDIAL, FINAL);

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

    // 경량 정규화: 문자 체계를 바꾸지 않고 인코딩 수준의 변형만 제거하며, 각 코드 유닛은 그 유닛을 생성한 원본 문자소 군집의 전체 구간에 대응함
    public TextNormalizedView lightlyNormalize(String originalText) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(originalText.length());
        BreakIterator originalClusters = BreakIterator.getCharacterInstance();
        originalClusters.setText(originalText);
        int start = originalClusters.first();
        for (int end = originalClusters.next(); end != BreakIterator.DONE; start = end, end = originalClusters.next()) {
            String canonical = canonicalize(originalText.substring(start, end));
            int lightlyNormalizedStart = builder.length();
            int lightlyNormalizedEnd = lightlyNormalizedStart + canonical.length();
            for (int index = 0; index < canonical.length(); index++) {
                builder.append(canonical.charAt(index), lightlyNormalizedStart, lightlyNormalizedEnd, start, end);
            }
        }
        return builder.build();
    }

    // 강화 정규화: 단어 사이에 끼워 넣은 문자는 제거하고, 치환한 문자는 대응 문자로 변환하고, 반복한 문자는 병합하며,
    // 병합된 코드 유닛은 그 유닛을 만든 코드 유닛들의 구간 합에 대응함
    public TextNormalizedView aggressivelyNormalize(TextNormalizedView lightlyNormalizedView) {
        TextNormalizedView confusableMappedView = mapCrossScriptConfusablesToLatin(lightlyNormalizedView);
        TextNormalizedView nonLetterNoiseRemovedView = removeNonLetterNoise(confusableMappedView);
        TextNormalizedView noiseJamoRemovedView = removeNoiseJamoBetweenSyllables(nonLetterNoiseRemovedView);
        TextNormalizedView repeatCollapsedView = collapseRepeats(noiseJamoRemovedView);
        TextNormalizedView jamoComposedView = composeCompatibilityRunIntoSyllables(repeatCollapsedView);
        return mergeElongatedJamo(jamoComposedView);
    }

    // 키 입력 정규화: 라틴 문자를 두벌식 한글로 바꾼 뒤 음절과 낱자를 두벌식 키 입력 자모로 통일하며, 각 코드 유닛에 유닛 태그를 기록함
    // 강화 정규화가 병합한 라틴 문자는 경량 정규화 뷰에서 되살려, 두 번 누른 키가 받침과 다음 첫소리로 나뉘게 함 (whssk → 존나)
    public TextNormalizedView decomposeIntoKeystrokes(TextNormalizedView aggressivelyNormalizedView,
                                                      TextNormalizedView lightlyNormalizedView) {
        TextNormalizedView latinExpandedView = expandLatinLettersFromLightView(aggressivelyNormalizedView, lightlyNormalizedView);
        TextNormalizedView latinConvertedView = convertLatinLettersToTwoBeolsikHangul(latinExpandedView);
        TextNormalizedView jamoComposedView = composeCompatibilityRunIntoSyllables(latinConvertedView);
        TextNormalizedView elongationMergedView = mergeElongatedJamo(jamoComposedView);
        return mapToKeystrokeJamo(elongationMergedView, lightlyNormalizedView);
    }

    // 행 머리 추출: 비문자 잡음을 건너뛴 각 행의 첫 문자를 이어 붙이며, 기여한 행이 2개 미만이면 빈 뷰를 반환함
    public TextNormalizedView extractLineInitials(TextNormalizedView lightlyNormalizedView) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(lightlyNormalizedView.length());
        int contributingLineCount = 0;
        boolean isLineInitialFound = false;
        int sourceCodeUnitStart = 0;
        for (int codePoint : lightlyNormalizedView.getText().codePoints().toArray()) {
            int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoint);
            if (isLineTerminator(codePoint)) {
                isLineInitialFound = false;
            } else if (!isLineInitialFound && !isNonLetterNoiseCategory(codePoint)) {
                appendCodePoint(builder, codePoint, lightlyNormalizedView, sourceCodeUnitStart, sourceCodeUnitEnd);
                isLineInitialFound = true;
                contributingLineCount++;
            }
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return contributingLineCount >= MINIMUM_CONTRIBUTING_LINE_COUNT ? builder.build() : new TextNormalizedView.Builder(0).build();
    }

    private String canonicalize(String originalCluster) {
        String visibleText = removeCodePointIf(originalCluster, isInDICP());
        if (visibleText.isEmpty()) {
            return visibleText;
        }
        String decomposedText = nfkdNormalizer.normalize(visibleText);
        String unmarkedText = removeCodePointIf(decomposedText, TextNormalizationHelper::isInCombiningMark);
        String composedText = nfkcNormalizer.normalize(unmarkedText);

        StringBuilder canonicalText = new StringBuilder(composedText.length());
        composedText.codePoints()
                .map(codePoint -> CONJOINING_COMPATIBILITY_JAMO_MAP.getOrDefault(codePoint, codePoint))
                .map(codePoint -> LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.getOrDefault(codePoint, codePoint))
                .map(UCharacter::toLowerCase) // String.toLowerCase는 길이를 바꿀 수 있어 위치 대응이 깨지므로 코드 포인트 단위로 변환
                .forEach(canonicalText::appendCodePoint);
        return canonicalText.toString();
    }

    private String removeCodePointIf(String text, IntPredicate removalPredicate) {
        StringBuilder keptText = new StringBuilder(text.length());
        text.codePoints().filter(removalPredicate.negate()).forEach(keptText::appendCodePoint);
        return keptText.toString();
    }

    private TextNormalizedView mapCrossScriptConfusablesToLatin(TextNormalizedView sourceView) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int sourceCodeUnitStart = 0;
        for (int codePoint : sourceView.getText().codePoints().toArray()) {
            int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoint);
            appendCodePoint(builder, CROSS_SCRIPT_CONFUSABLE_LATIN_MAP.getOrDefault(codePoint, codePoint),
                    sourceView, sourceCodeUnitStart, sourceCodeUnitEnd);
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return builder.build();
    }

    // 라틴 문자 코드 유닛을 그 경량 정규화 구간의 라틴 문자들로 되돌리며, 비문자 잡음은 건너뛰고 각 문자는 자신의 구간을 유지함
    // 한 문자 묶음이 만든 코드 유닛들은 같은 경량 정규화 구간을 공유하므로, 이미 되돌린 구간에 속한 코드 유닛은 건너뜀 (ﬁ → fi)
    private TextNormalizedView expandLatinLettersFromLightView(TextNormalizedView aggressivelyView,
                                                               TextNormalizedView lightlyView) {
        String lightlyText = lightlyView.getText();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(lightlyView.length());
        int expandedLightlyEnd = 0;
        int aggressivelyCodeUnitStart = 0;
        for (int codePoint : aggressivelyView.getText().codePoints().toArray()) {
            int aggressivelyCodeUnitEnd = aggressivelyCodeUnitStart + Character.charCount(codePoint);
            if (!LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.containsKey(codePoint)) { // 두벌식 키가 아닌 문자(한글 등): 확장 없이 유지
                appendCodePoint(builder, codePoint, aggressivelyView, aggressivelyCodeUnitStart, aggressivelyCodeUnitEnd);
            } else if (aggressivelyView.getLightlyNormalizedStart(aggressivelyCodeUnitStart) >= expandedLightlyEnd) {
                int lightlyNormalizedEnd = aggressivelyView.getLightlyNormalizedEnd(aggressivelyCodeUnitStart);
                int lightlyNormalizedIndex = aggressivelyView.getLightlyNormalizedStart(aggressivelyCodeUnitStart);
                while (lightlyNormalizedIndex < lightlyNormalizedEnd) {
                    int lightlyCodePointStart = lightlyText.codePointAt(lightlyNormalizedIndex);
                    int lightlyCodePointEnd = lightlyNormalizedIndex + Character.charCount(lightlyCodePointStart);
                    int latinCodePoint = CROSS_SCRIPT_CONFUSABLE_LATIN_MAP.getOrDefault(lightlyCodePointStart, lightlyCodePointStart);
                    if (LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.containsKey(latinCodePoint)) { // 비문자 잡음 등 두벌식 키가 아닌 문자 제외
                        appendCodePoint(builder, latinCodePoint, lightlyView,
                                lightlyNormalizedIndex, lightlyCodePointEnd);
                    }
                    lightlyNormalizedIndex = lightlyCodePointEnd;
                }
                expandedLightlyEnd = lightlyNormalizedEnd;
            }
            aggressivelyCodeUnitStart = aggressivelyCodeUnitEnd;
        }
        return builder.build();
    }

    // 라틴 문자만 두벌식 자모로 바꾸며, 바꾼 자모끼리만 두벌식 입력기처럼 초성, 중성, 선택적 종성 순으로 음절을 조합함
    private TextNormalizedView convertLatinLettersToTwoBeolsikHangul(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int sourceCodeUnitStart = 0;
        int codePointArrayIndex = 0;
        while (codePointArrayIndex < codePoints.length) {
            if (!LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.containsKey(codePoints[codePointArrayIndex])) {
                int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoints[codePointArrayIndex]);
                appendCodePoint(builder, codePoints[codePointArrayIndex], sourceView,
                        sourceCodeUnitStart, sourceCodeUnitEnd);
                sourceCodeUnitStart = sourceCodeUnitEnd;
                codePointArrayIndex++;
                continue;
            }
            ConvertedLatinLetters convertedLatinLetters = convertLatinLetters(codePoints, codePointArrayIndex);
            // 라틴 소문자는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
            appendCodePoint(builder, convertedLatinLetters.codePoint(), sourceView,
                    sourceCodeUnitStart, sourceCodeUnitStart + convertedLatinLetters.letterCount());
            sourceCodeUnitStart += convertedLatinLetters.letterCount();
            codePointArrayIndex += convertedLatinLetters.letterCount();
        }
        return builder.build();
    }

    // 뒤에 모음이 오는 자음은 받침이 되지 않고 다음 음절의 첫소리로 남으며, 이웃한 두 키가 겹모음·겹받침을 이루면 하나로 합침
    private ConvertedLatinLetters convertLatinLetters(int[] codePoints, int letterStartIndex) {
        int firstJamo = convertLatinLetterAt(codePoints, letterStartIndex);
        boolean startsWithInitial = COMPATIBILITY_CONJOINING_CHOSEONG_MAP.containsKey(firstJamo)
                && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(
                        convertLatinLetterAt(codePoints, letterStartIndex + 1));
        if (!startsWithInitial && !COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(firstJamo)) {
            return new ConvertedLatinLetters(firstJamo, 1);
        }
        int vowelLatinIndex = startsWithInitial ? letterStartIndex + 1 : letterStartIndex;
        int vowelJamoIndex = convertLatinLetterAt(codePoints, vowelLatinIndex);
        Integer compoundVowelJamoIndex = composeCompoundJamo(
                vowelJamoIndex, convertLatinLetterAt(codePoints, vowelLatinIndex + 1));
        if (compoundVowelJamoIndex != null) {
            vowelJamoIndex = compoundVowelJamoIndex;
            vowelLatinIndex++;
        }
        vowelLatinIndex++;
        if (!startsWithInitial) {
            return new ConvertedLatinLetters(vowelJamoIndex, vowelLatinIndex - letterStartIndex);
        }
        int finalJamo = NOT_FINAL_JAMO;
        int firstFinalJamo = convertLatinLetterAt(codePoints, vowelLatinIndex);
        // 받침이 될 수 있는 자음이고 뒤에 모음이 없을 때만 받침으로 붙임
        if (COMPATIBILITY_CONJOINING_JONGSEONG_MAP.containsKey(firstFinalJamo) && isNotFollowedByVowel(codePoints, vowelLatinIndex)) {
            Integer compoundFinalJamo = composeCompoundJamo(firstFinalJamo, convertLatinLetterAt(codePoints, vowelLatinIndex + 1));
            if (compoundFinalJamo != null && isNotFollowedByVowel(codePoints, vowelLatinIndex + 1)) {
                finalJamo = compoundFinalJamo;
                vowelLatinIndex++;
            } else {
                finalJamo = firstFinalJamo;
            }
            vowelLatinIndex++;
        }
        return new ConvertedLatinLetters(composeSyllable(firstJamo, vowelJamoIndex, finalJamo), vowelLatinIndex - letterStartIndex);
    }

    private int convertLatinLetterAt(int[] codePoints, int codePointArrayIndex) {
        return codePointArrayIndex < codePoints.length
                ? LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.getOrDefault(codePoints[codePointArrayIndex], NOT_CONVERTED_JAMO)
                : NOT_CONVERTED_JAMO;
    }

    private Integer composeCompoundJamo(int firstJamo, int secondJamo) {
        if (firstJamo == NOT_CONVERTED_JAMO || secondJamo == NOT_CONVERTED_JAMO) {
            return null;
        }
        return KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP
                .get(Character.toString(firstJamo) + Character.toString(secondJamo));
    }

    // 다음 문자가 라틴 문자에서 바꾼 모음이거나 입력한 모음 낱자인지 판단함
    private boolean isNotFollowedByVowel(int[] codePoints, int codePointArrayIndex) {
        int nextArrayIndex = codePointArrayIndex + 1;
        if (nextArrayIndex >= codePoints.length) {
            return true;
        }
        int nextCodePoint = codePoints[nextArrayIndex];
        return !COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(
                LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.getOrDefault(nextCodePoint, nextCodePoint));
    }

    private TextNormalizedView removeNonLetterNoise(TextNormalizedView sourceView) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int sourceCodeUnitStart = 0;
        for (int codePoint : sourceView.getText().codePoints().toArray()) {
            int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoint);
            if (!isNonLetterNoiseCategory(codePoint)) {
                appendCodePoint(builder, codePoint, sourceView, sourceCodeUnitStart, sourceCodeUnitEnd);
            }
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return builder.build();
    }

    private TextNormalizedView removeNoiseJamoBetweenSyllables(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int sourceCodeUnitStart = 0;
        int codePointArrayIndex = 0;
        while (codePointArrayIndex < codePoints.length) {
            int runEndArrayIndex = codePointArrayIndex;
            while (runEndArrayIndex < codePoints.length && isNoiseJamo(codePoints[runEndArrayIndex])) {
                runEndArrayIndex++;
            }
            boolean isRunBetweenSyllables = runEndArrayIndex > codePointArrayIndex
                    && codePointArrayIndex > 0 && runEndArrayIndex < codePoints.length
                    && isHangulSyllable(codePoints[codePointArrayIndex - 1])
                    && isHangulSyllable(codePoints[runEndArrayIndex]);
            if (isRunBetweenSyllables) {
                // 잡음 자모는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
                sourceCodeUnitStart += runEndArrayIndex - codePointArrayIndex;
                codePointArrayIndex = runEndArrayIndex;
                continue;
            }
            int keptEndArrayIndex = Math.max(runEndArrayIndex, codePointArrayIndex + 1);
            for (; codePointArrayIndex < keptEndArrayIndex; codePointArrayIndex++) {
                int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoints[codePointArrayIndex]);
                appendCodePoint(builder, codePoints[codePointArrayIndex], sourceView,
                        sourceCodeUnitStart, sourceCodeUnitEnd);
                sourceCodeUnitStart = sourceCodeUnitEnd;
            }
        }
        return builder.build();
    }

    // 서로게이트 절반이 합쳐지지 않도록 코드 포인트 단위로 비교
    private TextNormalizedView collapseRepeats(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int sourceCodeUnitStart = 0;
        int codePointArrayIndex = 0;
        while (codePointArrayIndex < codePoints.length) {
            int codePoint = codePoints[codePointArrayIndex];
            int sourceCodeUnitEnd = sourceCodeUnitStart;
            while (codePointArrayIndex < codePoints.length && codePoints[codePointArrayIndex] == codePoint) {
                sourceCodeUnitEnd += Character.charCount(codePoint);
                codePointArrayIndex++;
            }
            appendCodePoint(builder, codePoint, sourceView, sourceCodeUnitStart, sourceCodeUnitEnd);
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return builder.build();
    }

    // 호환 자모 연속 전체가 음절로 나뉠 때만 결합함
    private TextNormalizedView composeCompatibilityRunIntoSyllables(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int sourceCodeUnitStart = 0;
        int codePointArrayIndex = 0;
        while (codePointArrayIndex < codePoints.length) {
            int runEndArrayIndex = codePointArrayIndex;
            while (runEndArrayIndex < codePoints.length && isCompatibilityJamo(codePoints[runEndArrayIndex])) {
                runEndArrayIndex++;
            }
            List<Integer> syllableJamoCounts = segmentCompatibilityJamoRunIntoSyllables(codePoints, codePointArrayIndex, runEndArrayIndex);
            // 호환 자모는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
            for (int jamoCount : syllableJamoCounts) {
                int syllable = composeSyllable(codePoints[codePointArrayIndex], codePoints[codePointArrayIndex + 1],
                        jamoCount == 3 ? codePoints[codePointArrayIndex + 2] : NOT_FINAL_JAMO);
                appendCodePoint(builder, syllable, sourceView,
                        sourceCodeUnitStart, sourceCodeUnitStart + jamoCount);
                sourceCodeUnitStart += jamoCount;
                codePointArrayIndex += jamoCount;
            }
            if (syllableJamoCounts.isEmpty()) {
                int keptEndArrayIndex = Math.max(runEndArrayIndex, codePointArrayIndex + 1);
                for (; codePointArrayIndex < keptEndArrayIndex; codePointArrayIndex++) {
                    int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoints[codePointArrayIndex]);
                    appendCodePoint(builder, codePoints[codePointArrayIndex], sourceView,
                            sourceCodeUnitStart, sourceCodeUnitEnd);
                    sourceCodeUnitStart = sourceCodeUnitEnd;
                }
            }
        }
        return builder.build();
    }

    // 뒤에 모음이 오는 자음은 다음 음절의 첫소리로 읽으며, 연속 전체가 음절로 나뉘지 않으면 빈 목록을 반환함
    private List<Integer> segmentCompatibilityJamoRunIntoSyllables(int[] codePoints, int runStart, int runEnd) {
        List<Integer> syllableJamoCounts = new ArrayList<>();
        int cursor = runStart;
        while (cursor < runEnd) {
            if (cursor + 1 >= runEnd
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
        if (finalJamo != NOT_FINAL_JAMO) {
            conjoiningJamo.appendCodePoint(COMPATIBILITY_CONJOINING_JONGSEONG_MAP.get(finalJamo));
        }
        return nfcNormalizer.normalize(conjoiningJamo).codePointAt(0);
    }

    // 각 코드 포인트가 뒤따르는 늘여 쓴 모음·자음을 더 흡수할 수 없을 때까지 연달아 흡수하며 (바아알 → 발, 존ㄴ나 → 존나),
    // 병합된 코드 유닛은 흡수한 코드 유닛들의 구간 합에 대응함
    private TextNormalizedView mergeElongatedJamo(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codePointArrayIndex = 0;
        int sourceCodeUnitStart = 0;
        while (codePointArrayIndex < codePoints.length) {
            int mergedCodePoint = codePoints[codePointArrayIndex];
            int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(mergedCodePoint);
            codePointArrayIndex++;
            while (codePointArrayIndex < codePoints.length) {
                int absorbedCodePoint = absorbElongatedJamo(mergedCodePoint, codePoints, codePointArrayIndex);
                if (absorbedCodePoint == NOT_MERGED_CODE_POINT) {
                    break;
                }
                mergedCodePoint = absorbedCodePoint;
                sourceCodeUnitEnd += Character.charCount(codePoints[codePointArrayIndex]);
                codePointArrayIndex++;
            }
            appendCodePoint(builder, mergedCodePoint, sourceView, sourceCodeUnitStart, sourceCodeUnitEnd);
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return builder.build();
    }

    private int absorbElongatedJamo(int precedingCodePoint, int[] codePoints, int followingArrayIndex) {
        int absorbedCodePoint = absorbElongatedVowel(precedingCodePoint, codePoints[followingArrayIndex]);
        if (absorbedCodePoint != NOT_MERGED_CODE_POINT) {
            return absorbedCodePoint;
        }
        return isElongatedConsonant(precedingCodePoint, codePoints, followingArrayIndex)
                ? precedingCodePoint : NOT_MERGED_CODE_POINT;
    }

    // 두 음절 사이의 자음 낱자가 앞 음절의 받침이나 뒤 음절의 첫소리와 같으면 늘여 쓴 자음으로 봄 (존ㄴ나, 시ㅂ발)
    private boolean isElongatedConsonant(int precedingCodePoint, int[] codePoints, int followingArrayIndex) {
        int followingCodePoint = codePoints[followingArrayIndex];
        int nextArrayIndex = followingArrayIndex + 1;
        if (!isHangulSyllable(precedingCodePoint) || !isCompatibilityJamo(followingCodePoint)
                || nextArrayIndex >= codePoints.length || !isHangulSyllable(codePoints[nextArrayIndex])) {
            return false;
        }
        String precedingJamo = nfdNormalizer.normalize(Character.toString(precedingCodePoint));
        String nextJamo = nfdNormalizer.normalize(Character.toString(codePoints[nextArrayIndex]));
        boolean equalsPrecedingFinal = precedingJamo.length() == 3
                && CONJOINING_COMPATIBILITY_JAMO_MAP.get((int) precedingJamo.charAt(2)) == followingCodePoint;
        boolean equalsNextInitial = CONJOINING_COMPATIBILITY_JAMO_MAP.get((int) nextJamo.charAt(0)) == followingCodePoint;
        return equalsPrecedingFinal || equalsNextInitial;
    }

    // 받침 없는 음절이 같은 모음의 낱자 또는 같은 모음의 ㅇ 초성 음절을 흡수하며, 뒤 음절의 받침은 앞 음절로 옮김 (바 + 알 → 발)
    private int absorbElongatedVowel(int precedingCodePoint, int followingCodePoint) {
        if (!isHangulSyllableWithoutFinal(precedingCodePoint)) {
            return NOT_MERGED_CODE_POINT;
        }
        String precedingJamo = nfdNormalizer.normalize(Character.toString(precedingCodePoint));
        int vowel = precedingJamo.charAt(1);
        if (isCompatibilityJamo(followingCodePoint)) {
            return CONJOINING_COMPATIBILITY_JAMO_MAP.get(vowel) == followingCodePoint ? precedingCodePoint : NOT_MERGED_CODE_POINT;
        }
        if (!isHangulSyllable(followingCodePoint)) {
            return NOT_MERGED_CODE_POINT;
        }
        String followingJamo = nfdNormalizer.normalize(Character.toString(followingCodePoint));
        if (followingJamo.charAt(0) != CHOSEONG_IEUNG || followingJamo.charAt(1) != vowel) {
            return NOT_MERGED_CODE_POINT;
        }
        return nfcNormalizer.normalize(precedingJamo + followingJamo.substring(2)).codePointAt(0);
    }

    // 음절과 호환 자모를 두벌식 키 입력 자모로 분해해 입력 종류·자모 위치·음절 끝 태그를 기록하고, 그 밖의 문자는 OTHER 태그로 그대로 둠
    // 분해된 키 입력 자모는 모두 원래 코드 포인트의 구간을 그대로 물려받음
    private TextNormalizedView mapToKeystrokeJamo(TextNormalizedView sourceView, TextNormalizedView lightlyNormalizedView) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int sourceCodeUnitStart = 0;
        for (int codePoint : sourceView.getText().codePoints().toArray()) {
            if (isHangulSyllable(codePoint)) {
                appendSyllableKeystrokes(builder, codePoint, sourceView, sourceCodeUnitStart, lightlyNormalizedView);
            } else if (isCompatibilityJamo(codePoint)) {
                appendKeystrokes(builder,
                        convertToKeystrokeJamo(codePoint),
                        determineSourceCharacterKind(sourceView, sourceCodeUnitStart, lightlyNormalizedView),
                        STANDALONE, false, sourceView, sourceCodeUnitStart);
            } else {
                appendKeystrokes(builder,
                        Character.toString(codePoint),
                        OTHER, STANDALONE, false, sourceView, sourceCodeUnitStart);
            }
            sourceCodeUnitStart += Character.charCount(codePoint);
        }
        return builder.build();
    }

    // 낱자나 라틴 문자로 입력해 강화 정규화에서 합성된 음절도 그 입력으로 기록하되, 자모 위치는 합성된 음절의 위치를 따름
    private void appendSyllableKeystrokes(TextNormalizedView.Builder builder, int syllable, TextNormalizedView sourceView,
                                          int sourceCodeUnitIndex, TextNormalizedView lightlyNormalizedView) {
        TextNormalizedView.SourceCharacterKind sourceCharacterKind =
                determineSourceCharacterKind(sourceView, sourceCodeUnitIndex, lightlyNormalizedView);
        String conjoiningJamo = nfdNormalizer.normalize(Character.toString(syllable));
        for (int jamoIndex = 0; jamoIndex < conjoiningJamo.length(); jamoIndex++) {
            int compatibilityJamo = CONJOINING_COMPATIBILITY_JAMO_MAP.get((int) conjoiningJamo.charAt(jamoIndex));
            boolean isLastJamo = jamoIndex == conjoiningJamo.length() - 1;
            appendKeystrokes(builder, convertToKeystrokeJamo(compatibilityJamo), sourceCharacterKind,
                    SYLLABLE_JAMO_POSITIONS.get(jamoIndex), isLastJamo, sourceView, sourceCodeUnitIndex);
        }
    }

    // 강화 정규화의 한글 코드 유닛이 시작하는 경량 정규화 문자로 입력 종류를 판단하며, 라틴 문자로 치환되는 혼동 문자도 라틴 문자로 봄
    private TextNormalizedView.SourceCharacterKind determineSourceCharacterKind(TextNormalizedView sourceView,
                                                                                int sourceCodeUnitIndex,
                                                                                TextNormalizedView lightlyNormalizedView) {
        int lightlyNormalizedCodePoint = lightlyNormalizedView.getText()
                .codePointAt(sourceView.getLightlyNormalizedStart(sourceCodeUnitIndex));
        if (LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.containsKey(
                CROSS_SCRIPT_CONFUSABLE_LATIN_MAP.getOrDefault(lightlyNormalizedCodePoint, lightlyNormalizedCodePoint))) {
            return LATIN_LETTER;
        }
        return isCompatibilityJamo(lightlyNormalizedCodePoint) ? STANDALONE_JAMO : HANGUL_SYLLABLE;
    }

    private String convertToKeystrokeJamo(int compatibilityJamo) {
        return COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP
                .getOrDefault(compatibilityJamo, Character.toString(compatibilityJamo));
    }

    // 음절의 끝은 마지막 키 입력 자모에만 기록함
    private void appendKeystrokes(TextNormalizedView.Builder builder, String keystrokes,
                                  TextNormalizedView.SourceCharacterKind sourceCharacterKind,
                                  TextNormalizedView.JamoPosition jamoPosition, boolean syllableEndsAtLastKeystroke,
                                  TextNormalizedView sourceView, int sourceCodeUnitIndex) {
        for (int keystrokeIndex = 0; keystrokeIndex < keystrokes.length(); keystrokeIndex++) {
            boolean syllableEnds = syllableEndsAtLastKeystroke && keystrokeIndex == keystrokes.length() - 1;
            builder.append(keystrokes.charAt(keystrokeIndex),
                    sourceView.getLightlyNormalizedStart(sourceCodeUnitIndex),
                    sourceView.getLightlyNormalizedEnd(sourceCodeUnitIndex),
                    sourceView.getOriginalStart(sourceCodeUnitIndex),
                    sourceView.getOriginalEnd(sourceCodeUnitIndex),
                    sourceCharacterKind, jamoPosition, jamoPosition, syllableEnds);
        }
    }

    // 합쳐진 코드 유닛도 원본 순서를 유지하므로 구간 합은 첫 유닛의 시작과 마지막 유닛의 끝으로 결정됨
    private void appendCodePoint(TextNormalizedView.Builder builder, int codePoint, TextNormalizedView sourceView,
                                 int sourceCodeUnitStart, int sourceCodeUnitEnd) {
        int lightlyNormalizedStart = sourceView.getLightlyNormalizedStart(sourceCodeUnitStart);
        int lightlyNormalizedEnd = sourceView.getLightlyNormalizedEnd(sourceCodeUnitEnd - 1);
        int originalStart = sourceView.getOriginalStart(sourceCodeUnitStart);
        int originalEnd = sourceView.getOriginalEnd(sourceCodeUnitEnd - 1);
        for (char unit : Character.toChars(codePoint)) {
            builder.append(unit, lightlyNormalizedStart, lightlyNormalizedEnd, originalStart, originalEnd);
        }
    }

    // 라틴 문자에서 바꾼 음절 또는 자모 하나와 그것을 만든 라틴 문자 수
    private record ConvertedLatinLetters(int codePoint, int letterCount) {
    }
}
