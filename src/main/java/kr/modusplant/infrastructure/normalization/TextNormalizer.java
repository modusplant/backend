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
    private static final int NOT_CONVERTED_JAMO = -1;
    private static final int NOT_FINAL_JAMO = -1;
    private static final int NOT_MERGED_CODE_POINT = -1;
    private static final int MINIMUM_CONTRIBUTING_LINE_COUNT = 2;
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

    // 경량 정규화: 문자 체계를 바꾸지 않고 인코딩 수준의 변형만 제거하며, 각 코드 유닛은 그 유닛을 생성한 원본 그래핌 클러스터의 전체 구간에 대응함
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

    // 강화 정규화: 단어를 숨기려고 의도적으로 가한 변형(문자 끼워 넣기, 닮은 문자로 치환, 자모를 풀어 쓰거나 반복하거나 늘여 쓰기)을 되돌려 단어의 글자만 남기며,
    // 합쳐진 코드 유닛은 그 유닛을 만든 코드 유닛들의 구간 합에 대응함
    public AggressivelyNormalizedView normalizeAggressively(LightlyNormalizedView lightlyNormalizedView) {
        TextNormalizedView confusableMappedView = mapCrossScriptConfusablesToLatin(lightlyNormalizedView);
        TextNormalizedView nonLetterNoiseRemovedView = removeNonLetterNoise(confusableMappedView);
        TextNormalizedView repeatCollapsedView = collapseRepeatedJamo(nonLetterNoiseRemovedView);
        TextNormalizedView jamoComposedView = composeCompatibilityRunIntoSyllables(repeatCollapsedView);
        return new AggressivelyNormalizedView(mergeElongatedJamo(jamoComposedView));
    }

    // 음절 정규화: 경량 정규화 뷰의 토큰마다 이어지는 호환 자모를 최대한 음절로 조합한 뒤, 남은 호환 자모와 ASCII 소문자를 모두 제거함 (시바ㄹ놈 → 시발놈, 시l발, 시 ㅋ 발 → 시발)
    public SyllableNormalizedView normalizeWithSyllable(AggressivelyNormalizedView aggressivelyNormalizedView,
                                                        LightlyNormalizedView lightlyNormalizedView) {
        TextNormalizedView jamoComposedView = composeCompatibilityJamoWithinTokens(aggressivelyNormalizedView, lightlyNormalizedView);
        return new SyllableNormalizedView(removeCompatibilityJamoAndAsciiLowerCaseLetters(jamoComposedView));
    }

    // 키 입력 정규화: 한/영 전환 없이 영문 자판으로 친 한글을 두벌식 입력 결과로 되돌리고, 되돌린 한글의 풀어 쓰거나 늘여 쓴 자모도 강화 정규화처럼 합치며 (tlqkf, tlllqkf → 시발),
    // 합쳐진 코드 유닛은 그 유닛을 만든 코드 유닛들의 구간 합에 대응함
    public KeystrokeNormalizedView normalizeWithKeystroke(AggressivelyNormalizedView aggressivelyNormalizedView) {
        TextNormalizedView asciiLowerCaseConvertedView = convertAsciiLowerCaseLettersToDubeolsikHangul(aggressivelyNormalizedView);
        TextNormalizedView repeatCollapsedView = collapseRepeatedJamo(asciiLowerCaseConvertedView);
        TextNormalizedView jamoComposedView = composeCompatibilityRunIntoSyllables(repeatCollapsedView);
        return new KeystrokeNormalizedView(mergeElongatedJamo(jamoComposedView));
    }

    // 행 머리 정규화: 각 행의 첫 글자를 세로로 이어 읽어야 드러나는 단어를 한 줄로 만들며 (- 시원 / - 발코니 → 시발), 각 코드 유닛은 자기 구간을 유지하고,
    // 이어 읽을 행이 2개 미만이면 빈 뷰를 반환함
    public LineInitialNormalizedView normalizeWithLineInitial(LightlyNormalizedView lightlyNormalizedView) {
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(lightlyNormalizedView.length());
        int contributingLineCount = 0;
        boolean isLineInitialFound = false;
        int codeUnitStart = 0;
        for (int codePoint : lightlyNormalizedView.getText().codePoints().toArray()) {
            int codeUnitEnd = codeUnitStart + Character.charCount(codePoint);
            if (isLineTerminator(codePoint)) {
                isLineInitialFound = false;
            } else if (!isLineInitialFound && !isNonLetterNoiseCategory(codePoint)) {
                appendCodePoint(builder, codePoint, lightlyNormalizedView, codeUnitStart, codeUnitEnd);
                isLineInitialFound = true;
                contributingLineCount++;
            }
            codeUnitStart = codeUnitEnd;
        }
        return new LineInitialNormalizedView(contributingLineCount >= MINIMUM_CONTRIBUTING_LINE_COUNT
                ? builder.build() : new TextNormalizedView.Builder(0).build());
    }

    private String canonicalize(String originalCluster) {
        String unmarkedText = nfkdNormalizer.normalize(originalCluster).codePoints()
                .filter(codePoint -> !isDICP(codePoint) && !isCombiningMark(codePoint))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return nfkcNormalizer.normalize(unmarkedText).codePoints()
                .map(codePoint -> CONJOINING_COMPATIBILITY_JAMO_MAP.getOrDefault(codePoint, codePoint))
                .map(codePoint -> LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP.getOrDefault(codePoint, codePoint))
                .map(UCharacter::toLowerCase) // String.toLowerCase는 길이를 바꿀 수 있어 위치 대응이 깨지므로 코드 포인트 단위로 변환
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

    // ASCII 소문자만 두벌식 자모로 바꾸며, 바꾼 자모끼리만 두벌식 입력기처럼 초성, 중성, 선택적 종성 순으로 음절을 조합함
    private TextNormalizedView convertAsciiLowerCaseLettersToDubeolsikHangul(TextNormalizedView sourceView) {
        int[] codePoints = sourceView.getText().codePoints().toArray();
        // ASCII 소문자가 아닌 위치는 바꾼 자모가 없으므로, 바꾼 자모끼리만 음절을 조합함
        int[] convertedJamos = Arrays.stream(codePoints)
                .map(codePoint -> ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP.getOrDefault(codePoint, NOT_CONVERTED_JAMO))
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
            // 바꾼 모음뿐 아니라 입력한 모음 낱자도 뒤따르는 모음으로 봄 (tlqㅏㄹ → 시ㅂㅏㄹ)
            ComposedJamo composedJamo = composeLeadingJamo(convertedJamos, codePointIndex,
                    arrayIndex -> arrayIndex < codePoints.length && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(
                            ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP.getOrDefault(codePoints[arrayIndex], codePoints[arrayIndex])));
            // ASCII 소문자는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
            appendCodePoint(builder, composedJamo.codePoint(), sourceView,
                    codeUnitStart, codeUnitStart + composedJamo.jamoCount());
            codeUnitStart += composedJamo.jamoCount();
            codePointIndex += composedJamo.jamoCount();
        }
        return builder.build();
    }

    // 자모 배열의 시작 위치에서 두벌식 입력기처럼 초성, 중성, 선택적 종성 순으로 음절 하나를 조합하며, 조합하지 못하면 첫 자모를 그대로 반환함
    // 뒤에 모음이 오는 자음은 종성이 되지 않고 다음 음절의 초성으로 남으며, 이웃한 두 자모가 겹모음·겹종성을 이루면 하나로 합침
    private ComposedJamo composeLeadingJamo(int[] jamos, int jamoStartIndex, IntPredicate isVowelAt) {
        int firstJamo = jamoAt(jamos, jamoStartIndex);
        boolean startsWithInitial = COMPATIBILITY_CONJOINING_CHOSEONG_MAP.containsKey(firstJamo)
                && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(jamoAt(jamos, jamoStartIndex + 1));
        if (!startsWithInitial && !COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(firstJamo)) {
            return new ComposedJamo(firstJamo, 1);
        }
        int jamoIndex = startsWithInitial ? jamoStartIndex + 1 : jamoStartIndex;
        int vowelJamo = jamoAt(jamos, jamoIndex);
        Integer compoundVowelJamo = composeCompoundJamo(vowelJamo, jamoAt(jamos, jamoIndex + 1));
        if (compoundVowelJamo != null) {
            vowelJamo = compoundVowelJamo;
            jamoIndex++;
        }
        jamoIndex++;
        if (!startsWithInitial) {
            return new ComposedJamo(vowelJamo, jamoIndex - jamoStartIndex);
        }
        int finalJamo = NOT_FINAL_JAMO;
        int firstFinalJamo = jamoAt(jamos, jamoIndex);
        // 종성이 될 수 있는 자음이고 뒤에 모음이 없을 때만 종성으로 붙임
        if (COMPATIBILITY_CONJOINING_JONGSEONG_MAP.containsKey(firstFinalJamo) && !isVowelAt.test(jamoIndex + 1)) {
            Integer compoundFinalJamo = composeCompoundJamo(firstFinalJamo, jamoAt(jamos, jamoIndex + 1));
            if (compoundFinalJamo != null && !isVowelAt.test(jamoIndex + 2)) {
                finalJamo = compoundFinalJamo;
                jamoIndex++;
            } else {
                finalJamo = firstFinalJamo;
            }
            jamoIndex++;
        }
        return new ComposedJamo(composeSyllable(firstJamo, vowelJamo, finalJamo), jamoIndex - jamoStartIndex);
    }

    private int jamoAt(int[] jamos, int jamoIndex) {
        return jamoIndex < jamos.length ? jamos[jamoIndex] : NOT_CONVERTED_JAMO;
    }

    private Integer composeCompoundJamo(int firstJamo, int secondJamo) {
        if (firstJamo == NOT_CONVERTED_JAMO || secondJamo == NOT_CONVERTED_JAMO) {
            return null;
        }
        return COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_MAP
                .get(Character.toString(firstJamo) + Character.toString(secondJamo));
    }

    // 경량 정규화 뷰의 같은 토큰에 속한 호환 자모 연속마다, 앞에서부터 음절을 최대한 조합하고 조합하지 못한 자모는 그대로 둠
    // 종성 없는 음절 뒤의 자음 낱자는 그 음절의 종성으로 붙이며 (시바ㄹ놈 → 시발놈), 합성된 음절은 조합한 자모들의 구간 합에 대응함
    private TextNormalizedView composeCompatibilityJamoWithinTokens(TextNormalizedView sourceView,
                                                                    LightlyNormalizedView lightlyNormalizedView) {
        int[] tokenIndexes = indexTokens(lightlyNormalizedView.getText());
        int[] codePoints = sourceView.getText().codePoints().toArray();
        TextNormalizedView.Builder builder = new TextNormalizedView.Builder(sourceView.length());
        int codeUnitStart = 0;
        int codePointIndex = 0;
        while (codePointIndex < codePoints.length) {
            if (isHangulSyllableWithoutFinal(codePoints[codePointIndex])) {
                int syllableIndex = codePointIndex;
                int syllableCodeUnitStart = codeUnitStart;
                int syllableTokenIndex = tokenIndexes[sourceView.getLightlyNormalizedStart(syllableCodeUnitStart)];
                // 음절과 호환 자모는 모두 BMP 문자이므로, 음절부터 이어지는 호환 자모까지는 배열 인덱스 차이가 코드 유닛 차이와 같음
                ComposedJamo composedJamo = attachFollowingFinalJamo(codePoints, syllableIndex,
                        arrayIndex -> arrayIndex < codePoints.length && isCompatibilityJamo(codePoints[arrayIndex])
                                && tokenIndexes[sourceView.getLightlyNormalizedStart(
                                        syllableCodeUnitStart + arrayIndex - syllableIndex)] == syllableTokenIndex);
                appendCodePoint(builder, composedJamo.codePoint(), sourceView,
                        codeUnitStart, codeUnitStart + composedJamo.jamoCount());
                codeUnitStart += composedJamo.jamoCount();
                codePointIndex += composedJamo.jamoCount();
                continue;
            }
            if (!isCompatibilityJamo(codePoints[codePointIndex])) {
                int codeUnitEnd = codeUnitStart + Character.charCount(codePoints[codePointIndex]);
                appendCodePoint(builder, codePoints[codePointIndex], sourceView, codeUnitStart, codeUnitEnd);
                codeUnitStart = codeUnitEnd;
                codePointIndex++;
                continue;
            }
            // 호환 자모는 모두 BMP 문자이므로 코드 포인트 수와 코드 유닛 수가 같음
            int tokenIndex = tokenIndexes[sourceView.getLightlyNormalizedStart(codeUnitStart)];
            int runEndIndex = codePointIndex;
            while (runEndIndex < codePoints.length && isCompatibilityJamo(codePoints[runEndIndex])
                    && tokenIndexes[sourceView.getLightlyNormalizedStart(
                            codeUnitStart + runEndIndex - codePointIndex)] == tokenIndex) {
                runEndIndex++;
            }
            int[] runJamos = Arrays.copyOfRange(codePoints, codePointIndex, runEndIndex);
            int runJamoIndex = 0;
            while (runJamoIndex < runJamos.length) {
                ComposedJamo composedJamo = composeLeadingJamo(runJamos, runJamoIndex,
                        jamoIndex -> jamoIndex < runJamos.length
                                && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(runJamos[jamoIndex]));
                appendCodePoint(builder, composedJamo.codePoint(), sourceView,
                        codeUnitStart, codeUnitStart + composedJamo.jamoCount());
                codeUnitStart += composedJamo.jamoCount();
                runJamoIndex += composedJamo.jamoCount();
            }
            codePointIndex = runEndIndex;
        }
        return builder.build();
    }

    // 종성 없는 음절 뒤에 같은 토큰의 종성이 될 수 있는 자음 낱자가 오고 그 뒤에 모음 낱자가 없으면, 그 자음 낱자를 종성으로 붙이며
    // 이웃한 두 자음 낱자가 겹종성을 이루면 하나로 합침 (바ㄹ → 발, 바ㄹㄱ → 밝, 바ㄹㅏ는 붙이지 않음)
    // 모든 위치의 자모 여부는 그 앞 위치가 같은 토큰의 자모일 때만 판정하므로, 판정 위치까지는 모두 BMP 문자임
    private ComposedJamo attachFollowingFinalJamo(int[] codePoints, int syllableIndex,
                                                  IntPredicate isJamoInSyllableTokenAt) {
        int syllable = codePoints[syllableIndex];
        int firstFinalIndex = syllableIndex + 1;
        if (!isJamoInSyllableTokenAt.test(firstFinalIndex)
                || !COMPATIBILITY_CONJOINING_JONGSEONG_MAP.containsKey(codePoints[firstFinalIndex])
                || isVowelJamoInSyllableTokenAt(codePoints, firstFinalIndex + 1, isJamoInSyllableTokenAt)) {
            return new ComposedJamo(syllable, 1);
        }
        int finalJamo = codePoints[firstFinalIndex];
        int jamoCount = 2;
        if (isJamoInSyllableTokenAt.test(firstFinalIndex + 1)) {
            Integer compoundFinalJamo = composeCompoundJamo(finalJamo, codePoints[firstFinalIndex + 1]);
            if (compoundFinalJamo != null
                    && !isVowelJamoInSyllableTokenAt(codePoints, firstFinalIndex + 2, isJamoInSyllableTokenAt)) {
                finalJamo = compoundFinalJamo;
                jamoCount = 3;
            }
        }
        StringBuilder conjoiningJamo = new StringBuilder(nfdNormalizer.normalize(Character.toString(syllable)))
                .appendCodePoint(COMPATIBILITY_CONJOINING_JONGSEONG_MAP.get(finalJamo));
        return new ComposedJamo(nfcNormalizer.normalize(conjoiningJamo).codePointAt(0), jamoCount);
    }

    private boolean isVowelJamoInSyllableTokenAt(int[] codePoints, int arrayIndex, IntPredicate isJamoInSyllableTokenAt) {
        return isJamoInSyllableTokenAt.test(arrayIndex)
                && COMPATIBILITY_CONJOINING_JUNGSEONG_MAP.containsKey(codePoints[arrayIndex]);
    }

    // 경량 정규화 텍스트의 각 코드 유닛에, 공백으로 나뉜 토큰의 번호를 기록함
    private int[] indexTokens(String lightlyNormalizedText) {
        int[] tokenIndexes = new int[lightlyNormalizedText.length()];
        int tokenIndex = 0;
        int index = 0;
        while (index < lightlyNormalizedText.length()) {
            int codePoint = lightlyNormalizedText.codePointAt(index);
            int nextIndex = index + Character.charCount(codePoint);
            if (UCharacter.isUWhiteSpace(codePoint)) {
                tokenIndex++;
            }
            Arrays.fill(tokenIndexes, index, nextIndex, tokenIndex);
            index = nextIndex;
        }
        return tokenIndexes;
    }

    // 음절로 조합되지 못하고 남은 호환 자모와 ASCII 소문자를 위치와 관계없이 모두 제거하며, 나머지 코드 유닛은 구간을 유지함
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

    // 연속한 같은 낱자 자모(호환 자모, 조합형 자모)만 하나로 병합하며, 음절과 라틴 문자 등 그 밖의 코드 포인트는 반복해도 각각 유지함
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

    // 호환 자모 연속 전체가 음절로 나뉠 때만 결합함
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
                        jamoCount == 3 ? codePoints[codePointIndex + 2] : NOT_FINAL_JAMO);
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

    // 뒤에 모음이 오는 자음은 음절의 초성으로 읽으며, 연속 전체가 음절로 나뉘지 않으면 빈 목록을 반환함
    // 겹모음·겹종성은 낱자 하나로 된 것만 조합하며 (ㅂㅏㄺ → 밝), 이웃한 두 낱자는 합치지 않고 (ㅂㅏㄹㄱ 유지),
    // 모음 앞의 겹종성은 두벌식처럼 앞 음절의 종성과 다음 음절의 초성으로 나누지 않으며, 겹종성은 초성이 될 수 없으므로 연속이 그대로 남음 (ㄱㅏㄳㅣ 유지)
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
        if (finalJamo != NOT_FINAL_JAMO) {
            conjoiningJamo.appendCodePoint(COMPATIBILITY_CONJOINING_JONGSEONG_MAP.get(finalJamo));
        }
        return nfcNormalizer.normalize(conjoiningJamo).codePointAt(0);
    }

    // 각 코드 포인트가 뒤따르는 늘여 쓴 모음·자음을 더 흡수할 수 없을 때까지 연달아 흡수하며 (바아알 → 발, 존ㄴ맛 → 존맛),
    // 병합된 코드 유닛은 흡수한 코드 유닛들의 구간 합에 대응함
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
                int absorbedCodePoint = absorbElongatedJamo(mergedCodePoint, codePoints, codePointIndex);
                if (absorbedCodePoint == NOT_MERGED_CODE_POINT) {
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

    private int absorbElongatedJamo(int precedingCodePoint, int[] codePoints, int followingIndex) {
        int absorbedCodePoint = absorbElongatedVowel(precedingCodePoint, codePoints[followingIndex]);
        if (absorbedCodePoint != NOT_MERGED_CODE_POINT) {
            return absorbedCodePoint;
        }
        return isElongatedConsonant(precedingCodePoint, codePoints, followingIndex)
                ? precedingCodePoint : NOT_MERGED_CODE_POINT;
    }

    // 두 음절 사이의 자음 낱자가 앞 음절의 종성이나 뒤 음절의 초성과 같으면 늘여 쓴 자음으로 봄 (존ㄴ맛, 시ㅂ발)
    private boolean isElongatedConsonant(int precedingCodePoint, int[] codePoints, int followingIndex) {
        int followingCodePoint = codePoints[followingIndex];
        int nextIndex = followingIndex + 1;
        if (!isHangulSyllable(precedingCodePoint) || !isCompatibilityJamo(followingCodePoint)
                || nextIndex >= codePoints.length || !isHangulSyllable(codePoints[nextIndex])) {
            return false;
        }
        String precedingJamo = nfdNormalizer.normalize(Character.toString(precedingCodePoint));
        String nextJamo = nfdNormalizer.normalize(Character.toString(codePoints[nextIndex]));
        boolean equalsPrecedingFinal = precedingJamo.length() == 3
                && CONJOINING_COMPATIBILITY_JAMO_MAP.get((int) precedingJamo.charAt(2)) == followingCodePoint;
        boolean equalsNextInitial = CONJOINING_COMPATIBILITY_JAMO_MAP.get((int) nextJamo.charAt(0)) == followingCodePoint;
        return equalsPrecedingFinal || equalsNextInitial;
    }

    // 종성 없는 음절이 같은 모음의 낱자 또는 같은 모음의 ㅇ 초성 음절을 흡수하며, 뒤 음절의 종성은 앞 음절로 옮김 (바 + 알 → 발)
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
        if (followingJamo.charAt(0) != CONJOINING_CHOSEONG_IEUNG || followingJamo.charAt(1) != vowel) {
            return NOT_MERGED_CODE_POINT;
        }
        return nfcNormalizer.normalize(precedingJamo + followingJamo.substring(2)).codePointAt(0);
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
    private record ComposedJamo(int codePoint, int jamoCount) {
    }
}
