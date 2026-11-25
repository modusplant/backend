package kr.modusplant.infrastructure.prohibitedword.common.util;

import kr.modusplant.infrastructure.prohibitedword.ProhibitedWordMatch;

import static kr.modusplant.infrastructure.normalization.TextNormalizationType.LIGHT;

public interface ProhibitedWordMatchTestUtils extends ProhibitedWordTestUtils {
    ProhibitedWordMatch testProhibitedWordMatch = new ProhibitedWordMatch(testProhibitedWord, LIGHT, 0, 2);
}
