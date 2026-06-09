package kr.modusplant.shared.framework.icu4j.util;

import com.ibm.icu.text.Transliterator;

public abstract class Icu4jUtils {
    public static Transliterator getAnyNFDTransliterator() {
        return Transliterator.getInstance("Any-NFD");
    }

    public static Transliterator getAnyNFCTransliterator() {
        return Transliterator.getInstance("Any-NFC");
    }
}
