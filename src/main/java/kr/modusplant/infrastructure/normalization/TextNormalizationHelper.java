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

    public static boolean isCombiningMark(int codePoint) {
        return switch (UCharacter.getType(codePoint)) {
            case UCharacterCategory.NON_SPACING_MARK,
                 UCharacterCategory.COMBINING_SPACING_MARK,
                 UCharacterCategory.ENCLOSING_MARK -> true;
            default -> false;
        };
    }

    public static boolean isLetter(int codePoint) {
        return UCharacter.isLetter(codePoint);
    }

    public static boolean isLineTerminator(int codePoint) {
        return switch (UCharacter.getIntPropertyValue(codePoint, UProperty.LINE_BREAK)) {
            case UCharacter.LineBreak.MANDATORY_BREAK,
                 UCharacter.LineBreak.CARRIAGE_RETURN,
                 UCharacter.LineBreak.LINE_FEED,
                 UCharacter.LineBreak.NEXT_LINE -> true;
            default -> false;
        };
    }

    public static boolean isAsciiLowerCaseLetter(int codePoint) {
        return UCharacter.getType(codePoint) == UCharacterCategory.LOWERCASE_LETTER
                && UCharacter.getIntPropertyValue(codePoint, UProperty.BLOCK)
                == UCharacter.UnicodeBlock.BASIC_LATIN_ID;
    }

    public static boolean isHangulSyllable(int codePoint) {
        return isHangulSyllableWithoutTrailingConsonant(codePoint)
                || isHangulSyllableWithTrailingConsonant(codePoint);
    }

    public static boolean isHangulSyllableWithoutTrailingConsonant(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LV_SYLLABLE;
    }

    public static boolean isHangulSyllableWithTrailingConsonant(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.HANGUL_SYLLABLE_TYPE)
                == UCharacter.HangulSyllableType.LVT_SYLLABLE;
    }

    public static boolean isCompatibilityJamo(int codePoint) {
        return UCharacter.getIntPropertyValue(codePoint, UProperty.BLOCK)
                == UCharacter.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO_ID;
    }
}
