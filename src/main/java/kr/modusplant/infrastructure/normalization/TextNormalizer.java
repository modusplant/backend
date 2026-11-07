package kr.modusplant.infrastructure.normalization;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UCharacterCategory;
import com.ibm.icu.lang.UProperty;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.Normalizer2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.function.IntPredicate;

import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFKC_NORMALIZER_BEAN_NAME;
import static kr.modusplant.infrastructure.config.icu4j.Icu4jConfig.NFKD_NORMALIZER_BEAN_NAME;
import static kr.modusplant.shared.framework.jdbc.holder.swear.SwearCharacterMap.CONJOINING_COMPATIBILITY_JAMO_MAP;
import static kr.modusplant.shared.framework.jdbc.holder.swear.SwearCharacterMap.LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP;

@Component
public class TextNormalizer {
    private final Normalizer2 nfkdNormalizer;
    private final Normalizer2 nfkcNormalizer;

    public TextNormalizer(@Qualifier(NFKD_NORMALIZER_BEAN_NAME) Normalizer2 nfkdNormalizer,
                          @Qualifier(NFKC_NORMALIZER_BEAN_NAME) Normalizer2 nfkcNormalizer) {
        this.nfkdNormalizer = nfkdNormalizer;
        this.nfkcNormalizer = nfkcNormalizer;
    }

    public String nfdNormalize(String stringIncludingKorean) {
        return Normalizer.normalize(stringIncludingKorean, Normalizer.Form.NFD);
    }

    public String nfcNormalize(String stringIncludingKorean) {
        return Normalizer.normalize(stringIncludingKorean, Normalizer.Form.NFC);
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

    private String canonicalize(String originalCluster) {
        String visibleText = removeDICPCodePoint(originalCluster, isInDICP());
        if (visibleText.isEmpty()) {
            return visibleText;
        }
        String decomposedText = nfkdNormalizer.normalize(visibleText);
        String unmarkedText = removeDICPCodePoint(decomposedText, TextNormalizer::isInCombiningMark);
        String composedText = nfkcNormalizer.normalize(unmarkedText);

        StringBuilder canonicalText = new StringBuilder(composedText.length());
        composedText.codePoints()
                .map(codePoint -> CONJOINING_COMPATIBILITY_JAMO_MAP.getOrDefault(codePoint, codePoint))
                .map(codePoint -> LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP.getOrDefault(codePoint, codePoint))
                .map(UCharacter::toLowerCase) // String.toLowerCase는 길이를 바꿀 수 있어 위치 대응이 깨지므로 코드 포인트 단위로 변환
                .forEach(canonicalText::appendCodePoint);
        return canonicalText.toString();
    }

    private static String removeDICPCodePoint(String originalCluster, IntPredicate removeDICPFunction) {
        StringBuilder keptText = new StringBuilder(originalCluster.length());
        originalCluster.codePoints().filter(removeDICPFunction.negate()).forEach(keptText::appendCodePoint);
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
}
