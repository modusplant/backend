package kr.modusplant.infrastructure.prohibitedword.common.util;

import kr.modusplant.infrastructure.prohibitedword.ProhibitedWord;
import kr.modusplant.infrastructure.prohibitedword.ProhibitedWordScript;
import kr.modusplant.shared.enums.ProhibitedWordType;

public interface ProhibitedWordTestUtils {
    ProhibitedWord testProhibitedWord = new ProhibitedWord("시발", ProhibitedWordType.GENERAL, 2, ProhibitedWordScript.HANGUL);
    ProhibitedWord testLeadingConsonantProhibitedWord = new ProhibitedWord("ㅅㅂ", ProhibitedWordType.GENERAL, 2, ProhibitedWordScript.HANGUL);
    ProhibitedWord testEnglishProhibitedWord = new ProhibitedWord("dick", ProhibitedWordType.GENERAL, 2, ProhibitedWordScript.LATIN);
    ProhibitedWord testMildProhibitedWord = new ProhibitedWord("바보", ProhibitedWordType.GENERAL, 1, ProhibitedWordScript.HANGUL);
}
