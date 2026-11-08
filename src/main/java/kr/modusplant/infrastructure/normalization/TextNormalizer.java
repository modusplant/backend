package kr.modusplant.infrastructure.normalization;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UCharacterCategory;
import com.ibm.icu.lang.UProperty;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.Normalizer2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.IntPredicate;

import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFC_NORMALIZER_BEAN_NAME;
import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFD_NORMALIZER_BEAN_NAME;
import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFKC_NORMALIZER_BEAN_NAME;
import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFKD_NORMALIZER_BEAN_NAME;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CONJOINING_COMPATIBILITY_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP;
import static kr.modusplant.shared.constant.Regex.PATTERN_FILLER_JAMO;

@Component
public class TextNormalizer {
    private static final int REMOVED_CODE_POINT = -1;
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
        NormalizedText lookAlikeResolvedText = resolveContextLookAlikes(homoglyphMappedText);
        NormalizedText nonLetterNoiseRemovedText = removeNonLetterNoise(lookAlikeResolvedText);
        NormalizedText fillerJamoRemovedText = removeFillerJamoBetweenSyllables(nonLetterNoiseRemovedText);
        NormalizedText repeatCollapsedText = collapseRepeats(fillerJamoRemovedText);
        return mergeElongatedVowels(repeatCollapsedText);
    }

    private String canonicalize(String originalCluster) {
        String visibleText = removeCodePointIf(originalCluster, isInDICP());
        if (visibleText.isEmpty()) {
            return visibleText;
        }
        String decomposedText = nfkdNormalizer.normalize(visibleText);
        String unmarkedText = removeCodePointIf(decomposedText, TextNormalizer::isInCombiningMark);
        String composedText = nfkcNormalizer.normalize(unmarkedText);

        StringBuilder canonicalText = new StringBuilder(composedText.length());
        composedText.codePoints()
                .map(codePoint -> CONJOINING_COMPATIBILITY_JAMO_MAP.getOrDefault(codePoint, codePoint))
                .map(codePoint -> LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.getOrDefault(codePoint, codePoint))
                .map(UCharacter::toLowerCase) // String.toLowerCase는 길이를 바꿀 수 있어 위치 대응이 깨지므로 코드 포인트 단위로 변환
                .forEach(canonicalText::appendCodePoint);
        return canonicalText.toString();
    }

    private static String removeCodePointIf(String text, IntPredicate removalPredicate) {
        StringBuilder keptText = new StringBuilder(text.length());
        text.codePoints().filter(removalPredicate.negate()).forEach(keptText::appendCodePoint);
        return keptText.toString();
    }

    private static IntPredicate isInDICP() {
        return codePoint -> UCharacter.hasBinaryProperty(codePoint, UProperty.DEFAULT_IGNORABLE_CODE_POINT);
    }

    // 결합 문자: 비공백 결합 문자(Mn), 공백 결합 문자(Mc), 둘러싸는 결합 문자(Me)
    private static boolean isInCombiningMark(int codePoint) {
        int category = UCharacter.getType(codePoint);
        return category == UCharacterCategory.NON_SPACING_MARK
                || category == UCharacterCategory.COMBINING_SPACING_MARK
                || category == UCharacterCategory.ENCLOSING_MARK;
    }

    private static NormalizedText mapCrossScriptHomoglyphsToLatin(NormalizedText sourceText) {
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

    private static NormalizedText resolveContextLookAlikes(NormalizedText sourceText) {
        int[] codePoints = sourceText.getText().codePoints().toArray();
        LetterContext[] previousContexts = new LetterContext[codePoints.length];
        LetterContext[] nextContexts = new LetterContext[codePoints.length];
        // 긴 유사 문자 연속에서도 선형 시간을 유지하도록 양방향 최근접 문맥을 미리 계산
        LetterContext nearestContext = LetterContext.OTHER;
        for (int codePointArrayIndex = 0; codePointArrayIndex < codePoints.length; codePointArrayIndex++) {
            previousContexts[codePointArrayIndex] = nearestContext;
            nearestContext = updateNearestContext(nearestContext, codePoints[codePointArrayIndex]);
        }
        nearestContext = LetterContext.OTHER;
        for (int codePointArrayIndex = codePoints.length - 1; codePointArrayIndex >= 0; codePointArrayIndex--) {
            nextContexts[codePointArrayIndex] = nearestContext;
            nearestContext = updateNearestContext(nearestContext, codePoints[codePointArrayIndex]);
        }

        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int sourceCodeUnitStart = 0;
        for (int codePointArrayIndex = 0; codePointArrayIndex < codePoints.length; codePointArrayIndex++) {
            int sourceCodeUnitEnd = sourceCodeUnitStart + Character.charCount(codePoints[codePointArrayIndex]);
            int resolvedCodePoint = resolveContextLookAlike(
                    codePoints[codePointArrayIndex],
                    previousContexts[codePointArrayIndex],
                    nextContexts[codePointArrayIndex]);
            if (resolvedCodePoint != REMOVED_CODE_POINT) {
                appendCodePoint(builder, resolvedCodePoint, sourceText, sourceCodeUnitStart, sourceCodeUnitEnd);
            }
            sourceCodeUnitStart = sourceCodeUnitEnd;
        }
        return builder.build();
    }

    // 유사 문자와 비문자 잡음은 최근접 문맥을 갱신하지 않음: 人l발에서 人과 l의 다음 문맥은 모두 HANGUL_SYLLABLE(발)
    private static LetterContext updateNearestContext(LetterContext nearestContext, int codePoint) {
        return isContextLookAlike(codePoint) || isNonLetterNoiseCategory(codePoint) ?
                nearestContext : convertLetterContext(codePoint);
    }

    private static int resolveContextLookAlike(int codePoint, LetterContext previousContext, LetterContext nextContext) {
        if (!isContextLookAlike(codePoint)) {
            return codePoint;
        }
        if (previousContext == LetterContext.HANGUL_SYLLABLE && nextContext == LetterContext.HANGUL_SYLLABLE) {
            return REMOVED_CODE_POINT;
        }
        boolean isNextToHangul = previousContext.isHangul() || nextContext.isHangul();
        boolean isNextToLatin = previousContext.isLatin() || nextContext.isLatin();
        if (isNextToHangul && !isNextToLatin) {
            return HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP.getOrDefault(codePoint, codePoint);
        }
        if (isNextToLatin && !isNextToHangul) {
            return LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP.getOrDefault(codePoint, codePoint);
        }
        return codePoint;
    }

    private static boolean isContextLookAlike(int codePoint) {
        return HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP.containsKey(codePoint)
                || LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP.containsKey(codePoint);
    }

    private static LetterContext convertLetterContext(int codePoint) {
        if (isHangulSyllable(codePoint)) {
            return LetterContext.HANGUL_SYLLABLE;
        }
        if (isCompatibilityJamo(codePoint)) {
            return LetterContext.HANGUL_JAMO;
        }
        return UScript.getScript(codePoint) == UScript.LATIN ? LetterContext.LATIN : LetterContext.OTHER;
    }

    private static NormalizedText removeNonLetterNoise(NormalizedText sourceText) {
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

    private static NormalizedText removeFillerJamoBetweenSyllables(NormalizedText sourceText) {
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
    private static NormalizedText collapseRepeats(NormalizedText sourceText) {
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
    private static void appendCodePoint(NormalizedText.Builder builder, int codePoint, NormalizedText sourceText,
                                        int sourceCodeUnitStart, int sourceCodeUnitEnd) {
        int lightlyNormalizedStart = sourceText.getLightlyNormalizedStart(sourceCodeUnitStart);
        int lightlyNormalizedEnd = sourceText.getLightlyNormalizedEnd(sourceCodeUnitEnd - 1);
        int originalStart = sourceText.getOriginalStart(sourceCodeUnitStart);
        int originalEnd = sourceText.getOriginalEnd(sourceCodeUnitEnd - 1);
        for (char unit : Character.toChars(codePoint)) {
            builder.append(unit, lightlyNormalizedStart, lightlyNormalizedEnd, originalStart, originalEnd);
        }
    }

    // 비문자 잡음: 구분자(Z*), 제어 문자(Cc), 문장 부호(P*), 기호(S*), 숫자(N*)
    private static boolean isNonLetterNoiseCategory(int codePoint) {
        return switch (UCharacter.getType(codePoint)) {
            case UCharacterCategory.SPACE_SEPARATOR,
                 UCharacterCategory.LINE_SEPARATOR,
                 UCharacterCategory.PARAGRAPH_SEPARATOR,

                 UCharacterCategory.CONTROL,

                 UCharacterCategory.DASH_PUNCTUATION,
                 UCharacterCategory.START_PUNCTUATION,
                 UCharacterCategory.END_PUNCTUATION,
                 UCharacterCategory.CONNECTOR_PUNCTUATION,
                 UCharacterCategory.OTHER_PUNCTUATION,
                 UCharacterCategory.INITIAL_PUNCTUATION,
                 UCharacterCategory.FINAL_PUNCTUATION,

                 UCharacterCategory.MATH_SYMBOL,
                 UCharacterCategory.CURRENCY_SYMBOL,
                 UCharacterCategory.MODIFIER_SYMBOL,
                 UCharacterCategory.OTHER_SYMBOL,

                 UCharacterCategory.DECIMAL_DIGIT_NUMBER,
                 UCharacterCategory.LETTER_NUMBER,
                 UCharacterCategory.OTHER_NUMBER -> true;
            default -> false;
        };
    }

    private static boolean isFillerJamo(int codePoint) {
        return PATTERN_FILLER_JAMO.matcher(Character.toString(codePoint)).matches();
    }

    private static boolean isHangulSyllable(int codePoint) {
        return isHangulSyllableWithoutFinal(codePoint)
                || isHangulSyllableWithFinal(codePoint);
    }

    private static boolean isHangulSyllableWithoutFinal(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LV_SYLLABLE;
    }

    private static boolean isHangulSyllableWithFinal(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LVT_SYLLABLE;
    }

    private static boolean isCompatibilityJamo(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.BLOCK)
                == UCharacter.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO_ID;
    }

    private enum LetterContext {
        HANGUL_SYLLABLE, HANGUL_JAMO, LATIN, OTHER;

        private boolean isHangul() {
            return this == HANGUL_SYLLABLE || this == HANGUL_JAMO;
        }

        private boolean isLatin() {
            return this == LATIN;
        }
    }
}
