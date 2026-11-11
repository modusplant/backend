package kr.modusplant.infrastructure.swear.common.util;

import kr.modusplant.infrastructure.swear.SwearScript;
import kr.modusplant.infrastructure.swear.SwearWord;
import kr.modusplant.shared.enums.SwearType;

public interface SwearWordTestUtils {
    SwearWord testSwearWord = new SwearWord("시발", SwearType.GENERAL, 2, SwearScript.HANGUL);
    SwearWord testInitialConsonantSwearWord = new SwearWord("ㅅㅂ", SwearType.GENERAL, 2, SwearScript.HANGUL);
    SwearWord testEnglishSwearWord = new SwearWord("fuck", SwearType.GENERAL, 2, SwearScript.LATIN);
}
