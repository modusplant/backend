package kr.modusplant.domains.search.framework.outbound.icu4j;

import kr.modusplant.domains.search.usecase.port.transliterator.SearchTransliterator;
import kr.modusplant.shared.framework.icu4j.util.Icu4jUtils;
import org.springframework.stereotype.Component;

@Component
public class SearchIcu4JTransliterator implements SearchTransliterator {
    @Override
    public String separateKoreanIntoConsonantAndVowel(String stringIncludingKorean) {
        return Icu4jUtils.getAnyNFDTransliterator().transliterate(stringIncludingKorean);
    }

    @Override
    public String combineKoreanIntoConsonantAndVowel(String stringIncludingKorean) {
        return Icu4jUtils.getAnyNFCTransliterator().transliterate(stringIncludingKorean);
    }
}
