package kr.modusplant.infrastructure.swear.common.util;

import kr.modusplant.infrastructure.swear.SwearMatch;

import static kr.modusplant.infrastructure.normalization.TextNormalizationType.LIGHT;

public interface SwearMatchTestUtils extends SwearWordTestUtils {
    SwearMatch testSwearMatch = new SwearMatch(testSwearWord, LIGHT, 0, 2);
}
