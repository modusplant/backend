package kr.modusplant.infrastructure.swear;

import com.ibm.icu.lang.UScript;

public enum SwearScript {
    HANGUL, LATIN, OTHER;

    public static SwearScript of(String lightlyNormalizedWord) {
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
