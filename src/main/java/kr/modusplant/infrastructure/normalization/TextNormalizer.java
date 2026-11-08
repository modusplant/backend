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
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.CROSS_SCRIPT_HOMOGLYPH_TO_LATIN_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.HANGUL_CONTEXT_LOOK_ALIKE_TO_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.LATIN_CONTEXT_LOOK_ALIKE_TO_LEET_LETTER_MAP;
import static kr.modusplant.infrastructure.normalization.NormalizationCharacterMap.LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP;
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

    // 강화 정규화: 단어 사이에 끼워 넣거나 치환하거나 반복한 문자를 되돌리며, 병합된 코드 유닛은 그 유닛을 만든 코드 유닛들의 구간 합에 대응함
    public NormalizedText aggressivelyNormalize(NormalizedText lightlyNormalizedText) {
        NormalizedText homoglyphMappedText = mapCrossScriptHomoglyphsToLatin(lightlyNormalizedText);
        NormalizedText lookAlikeResolvedText = resolveContextLookAlikes(homoglyphMappedText);
        NormalizedText separatorRemovedText = removeSeparators(lookAlikeResolvedText);
        NormalizedText fillerJamoRemovedText = removeFillerJamoBetweenSyllables(separatorRemovedText);
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
                .map(codePoint -> LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP.getOrDefault(codePoint, codePoint))
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
        int sourceStart = 0;
        for (int codePoint : sourceText.getText().codePoints().toArray()) {
            int sourceEnd = sourceStart + Character.charCount(codePoint);
            appendCodePoint(builder, CROSS_SCRIPT_HOMOGLYPH_TO_LATIN_MAP.getOrDefault(codePoint, codePoint),
                    sourceText, sourceStart, sourceEnd);
            sourceStart = sourceEnd;
        }
        return builder.build();
    }

    private static NormalizedText resolveContextLookAlikes(NormalizedText sourceText) {
        int[] codePoints = sourceText.getText().codePoints().toArray();
        LetterContext[] previousContexts = new LetterContext[codePoints.length];
        LetterContext[] nextContexts = new LetterContext[codePoints.length];
        // 긴 유사 문자 연속에서도 선형 시간을 유지하도록 양방향 최근접 문맥을 미리 계산
        LetterContext nearestContext = LetterContext.OTHER;
        for (int position = 0; position < codePoints.length; position++) {
            previousContexts[position] = nearestContext;
            nearestContext = updateNearestContext(nearestContext, codePoints[position]);
        }
        nearestContext = LetterContext.OTHER;
        for (int position = codePoints.length - 1; position >= 0; position--) {
            nextContexts[position] = nearestContext;
            nearestContext = updateNearestContext(nearestContext, codePoints[position]);
        }

        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int sourceStart = 0;
        for (int position = 0; position < codePoints.length; position++) {
            int sourceEnd = sourceStart + Character.charCount(codePoints[position]);
            int resolvedCodePoint =
                    resolveContextLookAlike(codePoints[position], previousContexts[position], nextContexts[position]);
            if (resolvedCodePoint != REMOVED_CODE_POINT) {
                appendCodePoint(builder, resolvedCodePoint, sourceText, sourceStart, sourceEnd);
            }
            sourceStart = sourceEnd;
        }
        return builder.build();
    }

    // 유사 문자끼리는 서로의 문맥이 되지 않음: 人l발에서 人과 l은 서로를 건너뛰고 발을 문맥으로 삼음
    private static LetterContext updateNearestContext(LetterContext nearestContext, int codePoint) {
        return isContextLookAlike(codePoint) || isInSeparatorCategory(codePoint) ?
                nearestContext : classifyLetterContext(codePoint);
    }

    private static int resolveContextLookAlike(int codePoint, LetterContext previousContext, LetterContext nextContext) {
        if (!isContextLookAlike(codePoint)) {
            return codePoint;
        }
        if (previousContext == LetterContext.HANGUL_SYLLABLE && nextContext == LetterContext.HANGUL_SYLLABLE) {
            return REMOVED_CODE_POINT;
        }
        boolean isNextToHangul = previousContext.isHangul() || nextContext.isHangul();
        boolean isNextToLatin = previousContext == LetterContext.LATIN || nextContext == LetterContext.LATIN;
        if (isNextToHangul && !isNextToLatin) {
            return HANGUL_CONTEXT_LOOK_ALIKE_TO_JAMO_MAP.getOrDefault(codePoint, codePoint);
        }
        if (isNextToLatin && !isNextToHangul) {
            return LATIN_CONTEXT_LOOK_ALIKE_TO_LEET_LETTER_MAP.getOrDefault(codePoint, codePoint);
        }
        return codePoint;
    }

    private static NormalizedText removeSeparators(NormalizedText sourceText) {
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int sourceStart = 0;
        for (int codePoint : sourceText.getText().codePoints().toArray()) {
            int sourceEnd = sourceStart + Character.charCount(codePoint);
            if (!isInSeparatorCategory(codePoint)) {
                appendCodePoint(builder, codePoint, sourceText, sourceStart, sourceEnd);
            }
            sourceStart = sourceEnd;
        }
        return builder.build();
    }

    private static NormalizedText removeFillerJamoBetweenSyllables(NormalizedText sourceText) {
        int[] codePoints = sourceText.getText().codePoints().toArray();
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int sourceStart = 0;
        int position = 0;
        while (position < codePoints.length) {
            int runEnd = position;
            while (runEnd < codePoints.length && isFillerJamo(codePoints[runEnd])) {
                runEnd++;
            }
            boolean isRunBetweenSyllables = runEnd > position && position > 0 && runEnd < codePoints.length
                    && isHangulSyllable(codePoints[position - 1]) && isHangulSyllable(codePoints[runEnd]);
            if (isRunBetweenSyllables) {
                sourceStart += runEnd - position; // 채움 자모는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
                position = runEnd;
                continue;
            }
            int keptEnd = Math.max(runEnd, position + 1);
            for (; position < keptEnd; position++) {
                int sourceEnd = sourceStart + Character.charCount(codePoints[position]);
                appendCodePoint(builder, codePoints[position], sourceText, sourceStart, sourceEnd);
                sourceStart = sourceEnd;
            }
        }
        return builder.build();
    }

    // 서로게이트 절반이 합쳐지지 않도록 코드 포인트 단위로 비교
    private static NormalizedText collapseRepeats(NormalizedText sourceText) {
        int[] codePoints = sourceText.getText().codePoints().toArray();
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int sourceStart = 0;
        int position = 0;
        while (position < codePoints.length) {
            int codePoint = codePoints[position];
            int sourceEnd = sourceStart;
            while (position < codePoints.length && codePoints[position] == codePoint) {
                sourceEnd += Character.charCount(codePoint);
                position++;
            }
            appendCodePoint(builder, codePoint, sourceText, sourceStart, sourceEnd);
            sourceStart = sourceEnd;
        }
        return builder.build();
    }

    private NormalizedText mergeElongatedVowels(NormalizedText sourceText) {
        int[] codePoints = sourceText.getText().codePoints().toArray();
        NormalizedText.Builder builder = new NormalizedText.Builder(sourceText.length());
        int position = 0;
        int sourceStart = 0;
        while (position < codePoints.length) {
            int mergedCodePoint = codePoints[position];
            int sourceEnd = sourceStart + Character.charCount(mergedCodePoint);
            position++;
            while (position < codePoints.length) {
                int absorbedCodePoint = absorbElongatedVowel(mergedCodePoint, codePoints[position]);
                if (absorbedCodePoint == NOT_MERGED_CODE_POINT) {
                    break;
                }
                mergedCodePoint = absorbedCodePoint;
                sourceEnd += Character.charCount(codePoints[position]);
                position++;
            }
            appendCodePoint(builder, mergedCodePoint, sourceText, sourceStart, sourceEnd);
            sourceStart = sourceEnd;
        }
        return builder.build();
    }

    // 받침 없는 음절이 같은 모음의 낱자 또는 같은 모음의 ㅇ 초성 음절을 흡수하며, 뒤 음절의 받침은 앞 음절로 옮김 (바 + 알 → 발)
    private int absorbElongatedVowel(int syllable, int followingCodePoint) {
        if (!isHangulSyllableWithoutFinal(syllable)) {
            return NOT_MERGED_CODE_POINT;
        }
        String syllableJamo = nfdNormalizer.normalize(Character.toString(syllable));
        int vowel = syllableJamo.charAt(1);
        if (isCompatibilityJamo(followingCodePoint)) {
            return CONJOINING_COMPATIBILITY_JAMO_MAP.get(vowel) == followingCodePoint ? syllable : NOT_MERGED_CODE_POINT;
        }
        if (!isHangulSyllable(followingCodePoint)) {
            return NOT_MERGED_CODE_POINT;
        }
        String followingJamo = nfdNormalizer.normalize(Character.toString(followingCodePoint));
        if (followingJamo.charAt(0) != CHOSEONG_IEUNG || followingJamo.charAt(1) != vowel) {
            return NOT_MERGED_CODE_POINT;
        }
        return nfcNormalizer.normalize(syllableJamo + followingJamo.substring(2)).codePointAt(0);
    }

    // 합쳐진 코드 유닛도 원본 순서를 유지하므로 구간 합은 첫 유닛의 시작과 마지막 유닛의 끝으로 결정됨
    private static void appendCodePoint(NormalizedText.Builder builder, int codePoint, NormalizedText sourceText,
                                        int sourceStart, int sourceEnd) {
        int lightlyNormalizedStart = sourceText.getLightlyNormalizedStart(sourceStart);
        int lightlyNormalizedEnd = sourceText.getLightlyNormalizedEnd(sourceEnd - 1);
        int originalStart = sourceText.getOriginalStart(sourceStart);
        int originalEnd = sourceText.getOriginalEnd(sourceEnd - 1);
        for (char unit : Character.toChars(codePoint)) {
            builder.append(unit, lightlyNormalizedStart, lightlyNormalizedEnd, originalStart, originalEnd);
        }
    }

    private static boolean isContextLookAlike(int codePoint) {
        return HANGUL_CONTEXT_LOOK_ALIKE_TO_JAMO_MAP.containsKey(codePoint)
                || LATIN_CONTEXT_LOOK_ALIKE_TO_LEET_LETTER_MAP.containsKey(codePoint);
    }

    private static LetterContext classifyLetterContext(int codePoint) {
        if (isHangulSyllable(codePoint)) {
            return LetterContext.HANGUL_SYLLABLE;
        }
        if (isCompatibilityJamo(codePoint)) {
            return LetterContext.HANGUL_JAMO;
        }
        return UScript.getScript(codePoint) == UScript.LATIN ? LetterContext.LATIN : LetterContext.OTHER;
    }

    // 구분 문자: 공백(Z*, Cc), 문장 부호(P*), 기호(S*), 숫자(N*)
    private static boolean isInSeparatorCategory(int codePoint) {
        return switch (UCharacter.getType(codePoint)) {
            case UCharacterCategory.SPACE_SEPARATOR, UCharacterCategory.LINE_SEPARATOR,
                 UCharacterCategory.PARAGRAPH_SEPARATOR, UCharacterCategory.CONTROL,
                 UCharacterCategory.DASH_PUNCTUATION, UCharacterCategory.START_PUNCTUATION,
                 UCharacterCategory.END_PUNCTUATION, UCharacterCategory.CONNECTOR_PUNCTUATION,
                 UCharacterCategory.OTHER_PUNCTUATION, UCharacterCategory.INITIAL_PUNCTUATION,
                 UCharacterCategory.FINAL_PUNCTUATION, UCharacterCategory.MATH_SYMBOL,
                 UCharacterCategory.CURRENCY_SYMBOL, UCharacterCategory.MODIFIER_SYMBOL,
                 UCharacterCategory.OTHER_SYMBOL, UCharacterCategory.DECIMAL_DIGIT_NUMBER,
                 UCharacterCategory.LETTER_NUMBER, UCharacterCategory.OTHER_NUMBER -> true;
            default -> false;
        };
    }

    private static boolean isFillerJamo(int codePoint) {
        return PATTERN_FILLER_JAMO.matcher(Character.toString(codePoint)).matches();
    }

    private static boolean isHangulSyllable(int codePoint) {
        return isHangulSyllableWithoutFinal(codePoint)
                || UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LVT_SYLLABLE;
    }

    private static boolean isHangulSyllableWithoutFinal(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LV_SYLLABLE;
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
    }
}
