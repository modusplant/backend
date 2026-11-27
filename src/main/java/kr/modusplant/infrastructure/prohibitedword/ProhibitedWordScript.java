package kr.modusplant.infrastructure.prohibitedword;

import com.ibm.icu.lang.UScript;

public enum ProhibitedWordScript {
    HANGUL, LATIN, OTHER;

    public static ProhibitedWordScript of(String lightlyNormalizedWord) {
        boolean containsLatin = false;
        int index = 0;
        while (index < lightlyNormalizedWord.length()) {
            int codePoint = lightlyNormalizedWord.codePointAt(index);
            int script = UScript.getScript(codePoint);
            if (script == UScript.HANGUL) {
                return HANGUL;
            }
            containsLatin |= script == UScript.LATIN;
            index += Character.charCount(codePoint);
        }
        return containsLatin ? LATIN : OTHER;
    }
}
