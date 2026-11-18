package kr.modusplant.infrastructure.normalization;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UCharacterCategory;
import com.ibm.icu.lang.UProperty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextNormalizationHelper {
    public static boolean isDICP(int codePoint) {
        return UCharacter.hasBinaryProperty(codePoint, UProperty.DEFAULT_IGNORABLE_CODE_POINT);
    }

    // 결합 문자: 비공백 결합 문자(Mn), 공백 결합 문자(Mc), 둘러싸는 결합 문자(Me)
    public static boolean isCombiningMark(int codePoint) {
        return switch (UCharacter.getType(codePoint)) {
            case UCharacterCategory.NON_SPACING_MARK,
                 UCharacterCategory.COMBINING_SPACING_MARK,
                 UCharacterCategory.ENCLOSING_MARK -> true;
            default -> false;
        };
    }

    // 비문자 잡음: 구분자(Z*), 제어 문자(Cc), 문장 부호(P*), 기호(S*), 숫자(N*)
    public static boolean isNonLetterNoiseCategory(int codePoint) {
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

    // 줄 종결 문자: 필수 줄바꿈(BK), 캐리지 리턴(CR), 라인 피드(LF), 다음 줄(NL)
    public static boolean isLineTerminator(int codePoint) {
        return switch (UCharacter.getIntPropertyValue(codePoint, UProperty.LINE_BREAK)) {
            case UCharacter.LineBreak.MANDATORY_BREAK,
                 UCharacter.LineBreak.CARRIAGE_RETURN,
                 UCharacter.LineBreak.LINE_FEED,
                 UCharacter.LineBreak.NEXT_LINE -> true;
            default -> false;
        };
    }

    // ASCII 소문자: Basic Latin 블록의 소문자(Ll)
    public static boolean isAsciiLowerCaseLetter(int codePoint) {
        return UCharacter.getType(codePoint) == UCharacterCategory.LOWERCASE_LETTER
                && UCharacter.getIntPropertyValue(codePoint, UProperty.BLOCK)
                == UCharacter.UnicodeBlock.BASIC_LATIN_ID;
    }

    public static boolean isHangulSyllable(int codePoint) {
        return isHangulSyllableWithoutFinal(codePoint)
                || isHangulSyllableWithFinal(codePoint);
    }

    public static boolean isHangulSyllableWithoutFinal(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LV_SYLLABLE;
    }

    public static boolean isHangulSyllableWithFinal(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LVT_SYLLABLE;
    }

    public static boolean isCompatibilityJamo(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.BLOCK)
                == UCharacter.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO_ID;
    }

    // 조합형 자모: 음절 유형이 초성(L), 중성(V), 종성(T)인 코드 포인트
    public static boolean isConjoiningJamo(int codePoint) {
        return switch (UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)) {
            case UCharacter.HangulSyllableType.LEADING_JAMO,
                 UCharacter.HangulSyllableType.VOWEL_JAMO,
                 UCharacter.HangulSyllableType.TRAILING_JAMO -> true;
            default -> false;
        };
    }
}
