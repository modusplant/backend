package kr.modusplant.infrastructure.swear;

import com.ibm.icu.lang.UScript;

// 비속어 사전 단어가 쓰인 문자 체계
public enum SwearScript {
    HANGUL, LATIN, OTHER;

    // 한글 코드 포인트가 하나라도 있으면 HANGUL, 그렇지 않고 라틴 코드 포인트가 하나라도 있으면 LATIN
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
