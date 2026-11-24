package kr.modusplant.infrastructure.swear.common.util;

import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import kr.modusplant.infrastructure.normalization.TextNormalizer;
import kr.modusplant.infrastructure.swear.SwearEngine;
import kr.modusplant.infrastructure.swear.SwearHolder;

import java.util.List;

public interface SwearHolderTestUtils extends SwearWordTestUtils {
    static SwearHolder createSwearHolder() {
        Icu4jConfig icu4jConfig = new Icu4jConfig();
        TextNormalizer textNormalizer = new TextNormalizer(icu4jConfig.nfdNormalizer(), icu4jConfig.nfcNormalizer(),
                icu4jConfig.nfkdNormalizer(), icu4jConfig.nfkcNormalizer());
        SwearEngine swearEngine = SwearEngine.create(textNormalizer,
                List.of(testSwearWord, testLeadingConsonantSwearWord, testEnglishSwearWord, testMildSwearWord),
                List.of());
        return new SwearHolder(swearEngine);
    }
}
