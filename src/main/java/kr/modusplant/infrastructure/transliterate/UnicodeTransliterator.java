package kr.modusplant.infrastructure.transliterate;

import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class UnicodeTransliterator {
    public String separateKoreanIntoConsonantAndVowel(String stringIncludingKorean) {
        return Normalizer.normalize(stringIncludingKorean, Normalizer.Form.NFD);
    }

    public String combineKoreanIntoConsonantAndVowel(String stringIncludingKorean) {
        return Normalizer.normalize(stringIncludingKorean, Normalizer.Form.NFC);
    }
}
