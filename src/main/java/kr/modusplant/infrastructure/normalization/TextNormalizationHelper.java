package kr.modusplant.infrastructure.normalization;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UCharacterCategory;
import com.ibm.icu.lang.UProperty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.IntPredicate;

import static kr.modusplant.shared.constant.Regex.PATTERN_NOISE_JAMO;
import static kr.modusplant.shared.constant.Regex.PATTERN_LINE_TERMINATOR;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TextNormalizationHelper {
    static IntPredicate isInDICP() {
        return codePoint -> UCharacter.hasBinaryProperty(codePoint, UProperty.DEFAULT_IGNORABLE_CODE_POINT);
    }

    // 결합 문자: 비공백 결합 문자(Mn), 공백 결합 문자(Mc), 둘러싸는 결합 문자(Me)
    static boolean isInCombiningMark(int codePoint) {
        int category = UCharacter.getType(codePoint);
        return category == UCharacterCategory.NON_SPACING_MARK
                || category == UCharacterCategory.COMBINING_SPACING_MARK
                || category == UCharacterCategory.ENCLOSING_MARK;
    }

    // 비문자 잡음: 구분자(Z*), 제어 문자(Cc), 문장 부호(P*), 기호(S*), 숫자(N*)
    static boolean isNonLetterNoiseCategory(int codePoint) {
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

    static boolean isNoiseJamo(int codePoint) {
        return PATTERN_NOISE_JAMO.matcher(Character.toString(codePoint)).matches();
    }

    static boolean isLineTerminator(int codePoint) {
        return PATTERN_LINE_TERMINATOR.matcher(Character.toString(codePoint)).matches();
    }

    static boolean isHangulSyllable(int codePoint) {
        return isHangulSyllableWithoutFinal(codePoint)
                || isHangulSyllableWithFinal(codePoint);
    }

    static boolean isHangulSyllableWithoutFinal(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LV_SYLLABLE;
    }

    static boolean isHangulSyllableWithFinal(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LVT_SYLLABLE;
    }

    static boolean isCompatibilityJamo(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.BLOCK)
                == UCharacter.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO_ID;
    }
}
