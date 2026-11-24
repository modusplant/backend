package kr.modusplant.infrastructure.swear.common.util;

import kr.modusplant.infrastructure.swear.SwearScript;
import kr.modusplant.infrastructure.swear.SwearWord;
import kr.modusplant.shared.enums.SwearType;

public interface SwearWordTestUtils {
    SwearWord testSwearWord = new SwearWord("시발", SwearType.GENERAL, 2, SwearScript.HANGUL);
    SwearWord testLeadingConsonantSwearWord = new SwearWord("ㅅㅂ", SwearType.GENERAL, 2, SwearScript.HANGUL);
    SwearWord testEnglishSwearWord = new SwearWord("dick", SwearType.GENERAL, 2, SwearScript.LATIN);
    SwearWord testMildSwearWord = new SwearWord("바보", SwearType.GENERAL, 1, SwearScript.HANGUL);
}
