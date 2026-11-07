package kr.modusplant.shared.framework.jdbc.holder.swear;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UProperty;
import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.UnicodeSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SwearCharacterMap {
    private static final List<String> CONJOINING_JAMO_NAME_PREFIXES =
            List.of("HANGUL CHOSEONG ", "HANGUL JUNGSEONG ", "HANGUL JONGSEONG ");
    // 첫소리(L), 가운뎃소리(V), 끝소리(T) 자모: 유니코드 Hangul_Syllable_Type 속성
    private static final List<Integer> CONJOINING_JAMO_SYLLABLE_TYPES = List.of(
            UCharacter.HangulSyllableType.LEADING_JAMO,
            UCharacter.HangulSyllableType.VOWEL_JAMO,
            UCharacter.HangulSyllableType.TRAILING_JAMO);

    private static final int LATIN_LETTER_COUNT = 26;
    private static final int REGIONAL_INDICATOR_A = 0x1F1E6;
    private static final int NEGATIVE_CIRCLED_LATIN_CAPITAL_A = 0x1F150;
    private static final int NEGATIVE_SQUARED_LATIN_CAPITAL_A = 0x1F170;

    // 첫소리·가운뎃소리·끝소리 조합형 자모 → 같은 이름의 호환 자모 (호환 자모가 없는 옛한글 자모는 포함하지 않음)
    public static final Map<Integer, Integer> CONJOINING_TO_COMPATIBILITY_JAMO = createConjoiningToCompatibilityJamo();
    // 호환 분해가 없는 문자형 기호 → 라틴 소문자
    public static final Map<Integer, Integer> LETTER_LIKE_SYMBOL_TO_LATIN = createLetterLikeSymbolToLatin();

    private static Map<Integer, Integer> createConjoiningToCompatibilityJamo() {
        // 호환 자모의 NFKD 결과인 조합형 자모의 이름으로 묶어, 첫소리·가운뎃소리·끝소리를 모두 같은 호환 자모에 대응시킴
        Map<String, Integer> compatibilityJamoByName = new HashMap<>();
        UnicodeSet compatibilityJamoBlock = new UnicodeSet()
                .applyIntPropertyValue(UProperty.BLOCK, UCharacter.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO_ID);
        for (UnicodeSet.EntryRange range : compatibilityJamoBlock.ranges()) {
            for (int compatibilityJamo = range.codepoint; compatibilityJamo <= range.codepointEnd;
                 compatibilityJamo++) {
                // 채움 문자(HANGUL FILLER)는 보이지 않는 문자로 제거되므로 대응 대상에서 제외
                if (UCharacter.hasBinaryProperty(compatibilityJamo, UProperty.DEFAULT_IGNORABLE_CODE_POINT)) {
                    continue;
                }
                String conjoiningJamo = Normalizer2.getNFKDInstance().normalize(Character.toString(compatibilityJamo));
                String jamoName = getConjoiningJamoName(conjoiningJamo.codePointAt(0));
                if (jamoName != null) {
                    compatibilityJamoByName.put(jamoName, compatibilityJamo);
                }
            }
        }

        UnicodeSet conjoiningJamoSet = new UnicodeSet();
        for (int syllableType : CONJOINING_JAMO_SYLLABLE_TYPES) {
            conjoiningJamoSet.addAll(
                    new UnicodeSet().applyIntPropertyValue(UProperty.HANGUL_SYLLABLE_TYPE, syllableType));
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (UnicodeSet.EntryRange range : conjoiningJamoSet.ranges()) {
            for (int conjoiningJamo = range.codepoint; conjoiningJamo <= range.codepointEnd; conjoiningJamo++) {
                String jamoName = getConjoiningJamoName(conjoiningJamo);
                Integer compatibilityJamo = jamoName == null ? null : compatibilityJamoByName.get(jamoName);
                if (compatibilityJamo != null) {
                    map.put(conjoiningJamo, compatibilityJamo);
                }
            }
        }
        return Map.copyOf(map);
    }

    private static String getConjoiningJamoName(int codePoint) {
        String name = UCharacter.getName(codePoint);
        if (name == null) {
            return null;
        }
        for (String prefix : CONJOINING_JAMO_NAME_PREFIXES) {
            if (name.startsWith(prefix)) {
                return name.substring(prefix.length());
            }
        }
        return null;
    }

    private static Map<Integer, Integer> createLetterLikeSymbolToLatin() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int offset = 0; offset < LATIN_LETTER_COUNT; offset++) {
            int latin = 'a' + offset;
            map.put(REGIONAL_INDICATOR_A + offset, latin);
            map.put(NEGATIVE_CIRCLED_LATIN_CAPITAL_A + offset, latin);
            map.put(NEGATIVE_SQUARED_LATIN_CAPITAL_A + offset, latin);
        }
        return Map.copyOf(map);
    }
}
