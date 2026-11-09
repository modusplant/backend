package kr.modusplant.infrastructure.normalization;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.Normalizer2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFC_NORMALIZER_BEAN_NAME;
import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFD_NORMALIZER_BEAN_NAME;
import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFKC_NORMALIZER_BEAN_NAME;
import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFKD_NORMALIZER_BEAN_NAME;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.COMPATIBILITY_CONJOINING_CHOSEONG_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.COMPATIBILITY_CONJOINING_JONGSEONG_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.COMPATIBILITY_CONJOINING_JUNGSEONG_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CONJOINING_COMPATIBILITY_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isCompatibilityJamo;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isFillerJamo;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isHangulSyllable;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isHangulSyllableWithoutFinal;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isInDICP;
import static kr.modusplant.infrastructure.normalization.NormalizationHelper.isNonLetterNoiseCategory;

@Component
public class TextNormalizer {
    private static final int NOT_MERGED_CODE_POINT = -1;
    private static final char CHOSEONG_IEUNG = 'ᄋ';

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
    public NormalizedText lightlyNormalize(String originalText) {
        NormalizedText.Builder builder = new NormalizedText.Builder(originalText.length());
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
    public NormalizedText aggressivelyNormalize(NormalizedText lightlyNormalizedText) {
        NormalizedText homoglyphMappedText = mapCrossScriptHomoglyphsToLatin(lightlyNormalizedText);
        NormalizedText nonLetterNoiseRemovedText = removeNonLetterNoise(homoglyphMappedText);
        NormalizedText fillerJamoRemovedText = removeFillerJamoBetweenSyllables(nonLetterNoiseRemovedText);
        NormalizedText repeatCollapsedText = collapseRepeats(fillerJamoRemovedText);
        NormalizedText jamoComposedText = composeCompatibilityRunIntoSyllables(repeatCollapsedText);
        return mergeElongatedVowels(jamoComposedText);
    }

    private String canonicalize(String originalCluster) {
        String visibleText = removeCodePointIf(originalCluster, isInDICP());
        if (visibleText.isEmpty()) {
            return visibleText;
        }
        String decomposedText = nfkdNormalizer.normalize(visibleText);
        String unmarkedText = removeCodePointIf(decomposedText, NormalizationHelper::isInCombiningMark);
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

    private NormalizedText mapCrossScriptHomoglyphsToLatin(NormalizedText sourceText) {
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int sourceCodeUnitStart = 0;
        for (int codePoint : sourceText.getText().codePoints().toArray()) {
            int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoint);
            appendCodePoint(builder, CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP.getOrDefault(codePoint, codePoint),
                    sourceText, sourceCodeUnitStart, sourceCodeUnitEnd);
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return builder.build();
    }

    private NormalizedText removeNonLetterNoise(NormalizedText sourceText) {
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int sourceCodeUnitStart = 0;
        for (int codePoint : sourceText.getText().codePoints().toArray()) {
            int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoint);
            if (!isNonLetterNoiseCategory(codePoint)) {
                appendCodePoint(builder, codePoint, sourceText, sourceCodeUnitStart, sourceCodeUnitEnd);
            }
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return builder.build();
    }

    private NormalizedText removeFillerJamoBetweenSyllables(NormalizedText sourceText) {
        int[] codePoints = sourceText.getText().codePoints().toArray();
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int sourceCodeUnitStart = 0;
        int codePointArrayIndex = 0;
        while (codePointArrayIndex < codePoints.length) {
            int runEndArrayIndex = codePointArrayIndex;
            while (runEndArrayIndex < codePoints.length && isFillerJamo(codePoints[runEndArrayIndex])) {
                runEndArrayIndex++;
            }
            boolean isRunBetweenSyllables = runEndArrayIndex > codePointArrayIndex
                    && codePointArrayIndex > 0 && runEndArrayIndex < codePoints.length
                    && isHangulSyllable(codePoints[codePointArrayIndex - 1])
                    && isHangulSyllable(codePoints[runEndArrayIndex]);
            if (isRunBetweenSyllables) {
                // 채움 자모는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
                sourceCodeUnitStart += runEndArrayIndex - codePointArrayIndex;
                codePointArrayIndex = runEndArrayIndex;
                continue;
            }
            int keptEndArrayIndex = Math.max(runEndArrayIndex, codePointArrayIndex + 1);
            for (; codePointArrayIndex < keptEndArrayIndex; codePointArrayIndex++) {
                int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoints[codePointArrayIndex]);
                appendCodePoint(builder, codePoints[codePointArrayIndex], sourceText,
                        sourceCodeUnitStart, sourceCodeUnitEnd);
                sourceCodeUnitStart = sourceCodeUnitEnd;
            }
        }
        return builder.build();
    }

    // 서로게이트 절반이 합쳐지지 않도록 코드 포인트 단위로 비교
    private NormalizedText collapseRepeats(NormalizedText sourceText) {
        int[] codePoints = sourceText.getText().codePoints().toArray();
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int sourceCodeUnitStart = 0;
        int codePointArrayIndex = 0;
        while (codePointArrayIndex < codePoints.length) {
            int codePoint = codePoints[codePointArrayIndex];
            int sourceCodeUnitEnd = sourceCodeUnitStart;
            while (codePointArrayIndex < codePoints.length && codePoints[codePointArrayIndex] == codePoint) {
                sourceCodeUnitEnd += Character.charCount(codePoint);
                codePointArrayIndex++;
            }
            appendCodePoint(builder, codePoint, sourceText, sourceCodeUnitStart, sourceCodeUnitEnd);
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return builder.build();
    }

    // 호환 자모 연속 전체가 음절로 나뉠 때만 결합함
    private NormalizedText composeCompatibilityRunIntoSyllables(NormalizedText sourceText) {
        int[] codePoints = sourceText.getText().codePoints().toArray();
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
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
                appendCodePoint(builder, composeSyllable(codePoints, codePointArrayIndex, jamoCount), sourceText,
                        sourceCodeUnitStart, sourceCodeUnitStart + jamoCount);
                sourceCodeUnitStart += jamoCount;
                codePointArrayIndex += jamoCount;
            }
            if (syllableJamoCounts.isEmpty()) {
                int keptEndArrayIndex = Math.max(runEndArrayIndex, codePointArrayIndex + 1);
                for (; codePointArrayIndex < keptEndArrayIndex; codePointArrayIndex++) {
                    int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoints[codePointArrayIndex]);
                    appendCodePoint(builder, codePoints[codePointArrayIndex], sourceText,
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

    private int composeSyllable(int[] codePoints, int syllableStart, int jamoCount) {
        StringBuilder conjoiningJamo = new StringBuilder(jamoCount)
                .appendCodePoint(COMPATIBILITY_CONJOINING_CHOSEONG_MAP.get(codePoints[syllableStart]))
                .appendCodePoint(COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.get(codePoints[syllableStart + 1]));
        if (jamoCount == 3) {
            conjoiningJamo.appendCodePoint(COMPATIBILITY_CONJOINING_JONGSEONG_MAP.get(codePoints[syllableStart + 2]));
        }
        return nfcNormalizer.normalize(conjoiningJamo).codePointAt(0);
    }

    private NormalizedText mergeElongatedVowels(NormalizedText sourceText) {
        int[] codePoints = sourceText.getText().codePoints().toArray();
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int codePointArrayIndex = 0;
        int sourceCodeUnitStart = 0;
        while (codePointArrayIndex < codePoints.length) {
            int mergedCodePoint = codePoints[codePointArrayIndex];
            int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(mergedCodePoint);
            codePointArrayIndex++;
            while (codePointArrayIndex < codePoints.length) {
                int absorbedCodePoint = absorbElongatedVowel(mergedCodePoint, codePoints[codePointArrayIndex]);
                if (absorbedCodePoint == NOT_MERGED_CODE_POINT) {
                    break;
                }
                mergedCodePoint = absorbedCodePoint;
                sourceCodeUnitEnd += Character.charCount(codePoints[codePointArrayIndex]);
                codePointArrayIndex++;
            }
            appendCodePoint(builder, mergedCodePoint, sourceText, sourceCodeUnitStart, sourceCodeUnitEnd);
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return builder.build();
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

    // 합쳐진 코드 유닛도 원본 순서를 유지하므로 구간 합은 첫 유닛의 시작과 마지막 유닛의 끝으로 결정됨
    private void appendCodePoint(NormalizedText.Builder builder, int codePoint, NormalizedText sourceText,
                                 int sourceCodeUnitStart, int sourceCodeUnitEnd) {
        int lightlyNormalizedStart = sourceText.getLightlyNormalizedStart(sourceCodeUnitStart);
        int lightlyNormalizedEnd = sourceText.getLightlyNormalizedEnd(sourceCodeUnitEnd - 1);
        int originalStart = sourceText.getOriginalStart(sourceCodeUnitStart);
        int originalEnd = sourceText.getOriginalEnd(sourceCodeUnitEnd - 1);
        for (char unit : Character.toChars(codePoint)) {
            builder.append(unit, lightlyNormalizedStart, lightlyNormalizedEnd, originalStart, originalEnd);
        }
    }
}
