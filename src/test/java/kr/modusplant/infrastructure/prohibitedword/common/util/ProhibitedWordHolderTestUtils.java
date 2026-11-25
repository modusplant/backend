package kr.modusplant.infrastructure.prohibitedword.common.util;

import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import kr.modusplant.infrastructure.normalization.TextNormalizer;
import kr.modusplant.infrastructure.prohibitedword.ProhibitedWordEngine;
import kr.modusplant.infrastructure.prohibitedword.ProhibitedWordHolder;

import java.util.List;

public interface ProhibitedWordHolderTestUtils extends ProhibitedWordTestUtils {
    static ProhibitedWordHolder createProhibitedWordHolder() {
        Icu4jConfig icu4jConfig = new Icu4jConfig();
        TextNormalizer textNormalizer = new TextNormalizer(icu4jConfig.nfdNormalizer(), icu4jConfig.nfcNormalizer(),
                icu4jConfig.nfkdNormalizer(), icu4jConfig.nfkcNormalizer());
        ProhibitedWordEngine prohibitedWordEngine = ProhibitedWordEngine.create(textNormalizer,
                List.of(testProhibitedWord, testLeadingConsonantProhibitedWord, testEnglishProhibitedWord, testMildProhibitedWord),
                List.of());
        return new ProhibitedWordHolder(prohibitedWordEngine);
    }
}
