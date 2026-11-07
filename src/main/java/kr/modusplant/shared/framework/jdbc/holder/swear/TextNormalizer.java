package kr.modusplant.shared.framework.jdbc.holder.swear;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UCharacterCategory;
import com.ibm.icu.lang.UProperty;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.Normalizer2;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.IntPredicate;

import static kr.modusplant.shared.framework.jdbc.holder.swear.SwearCharacterMap.CONJOINING_TO_COMPATIBILITY_JAMO;
import static kr.modusplant.shared.framework.jdbc.holder.swear.SwearCharacterMap.LETTER_LIKE_SYMBOL_TO_LATIN;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextNormalizer {

    // 경량 뷰: 문자 집합은 유지한 채 인코딩 수준의 우회만 제거하며, 각 단위는 자신이 나온 문자소 군집 전체에 대응함
    public static NormalizedText lightlyNormalize(String original) {
        NormalizedText.Builder builder = new NormalizedText.Builder(original.length());
        BreakIterator clusters = BreakIterator.getCharacterInstance();
        clusters.setText(original);
        int start = clusters.first();
        for (int end = clusters.next(); end != BreakIterator.DONE; start = end, end = clusters.next()) {
            String canonical = canonicalize(original.substring(start, end));
            int lightlyNormalizedStart = builder.length();
            int lightlyNormalizedEnd = lightlyNormalizedStart + canonical.length();
            for (int index = 0; index < canonical.length(); index++) {
                builder.append(canonical.charAt(index), lightlyNormalizedStart, lightlyNormalizedEnd, start, end);
            }
        }
        return builder.build();
    }

    private static String canonicalize(String cluster) {
        String visible = removeCodePoints(cluster, TextNormalizer::isInvisible);
        if (visible.isEmpty()) {
            return visible;
        }
        String decomposed = Normalizer2.getNFKDInstance().normalize(visible);
        String unmarked = removeCodePoints(decomposed, TextNormalizer::isCombiningMark);
        String composed = Normalizer2.getNFKCInstance().normalize(unmarked);

        StringBuilder canonical = new StringBuilder(composed.length());
        composed.codePoints()
                .map(codePoint -> CONJOINING_TO_COMPATIBILITY_JAMO.getOrDefault(codePoint, codePoint))
                .map(codePoint -> LETTER_LIKE_SYMBOL_TO_LATIN.getOrDefault(codePoint, codePoint))
                // String.toLowerCase는 길이를 바꿀 수 있어 위치 대응이 깨지므로 코드 포인트 단위로 변환
                .map(UCharacter::toLowerCase)
                .forEach(canonical::appendCodePoint);
        return canonical.toString();
    }

    private static String removeCodePoints(String text, IntPredicate removed) {
        StringBuilder kept = new StringBuilder(text.length());
        text.codePoints().filter(removed.negate()).forEach(kept::appendCodePoint);
        return kept.toString();
    }

    // 보이지 않는 문자: 유니코드 Default_Ignorable_Code_Point 속성
    private static boolean isInvisible(int codePoint) {
        return UCharacter.hasBinaryProperty(codePoint, UProperty.DEFAULT_IGNORABLE_CODE_POINT);
    }

    // 결합 문자: 비공백 결합 문자(Mn), 공백 결합 문자(Mc), 둘러싸는 결합 문자(Me)
    private static boolean isCombiningMark(int codePoint) {
        int category = UCharacter.getType(codePoint);
        return category == UCharacterCategory.NON_SPACING_MARK
                || category == UCharacterCategory.COMBINING_SPACING_MARK
                || category == UCharacterCategory.ENCLOSING_MARK;
    }
}
