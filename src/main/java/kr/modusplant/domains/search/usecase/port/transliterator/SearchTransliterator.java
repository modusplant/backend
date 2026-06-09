package kr.modusplant.domains.search.usecase.port.transliterator;

public interface SearchTransliterator {
    String separateKoreanIntoConsonantAndVowel(String stringIncludingKorean);

    String combineKoreanIntoConsonantAndVowel(String stringIncludingKorean);
}
