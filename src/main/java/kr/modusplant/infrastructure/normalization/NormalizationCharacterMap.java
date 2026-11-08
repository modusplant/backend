package kr.modusplant.infrastructure.normalization;

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
public final class NormalizationCharacterMap {
    private static final List<String> CONJOINING_JAMO_NAME_PREFIXES =
            List.of("HANGUL CHOSEONG ", "HANGUL JUNGSEONG ", "HANGUL JONGSEONG ");

    // 유니코드 Hangul_Syllable_Type 속성값: 첫소리(L)·가운뎃소리(V)·끝소리(T)
    private static final List<Integer> CONJOINING_JAMO_SYLLABLE_TYPES = List.of(
            UCharacter.HangulSyllableType.LEADING_JAMO,
            UCharacter.HangulSyllableType.VOWEL_JAMO,
            UCharacter.HangulSyllableType.TRAILING_JAMO);

    private static final int LATIN_LETTER_COUNT = 26;
    private static final int REGIONAL_INDICATOR_A = 0x1F1E6; // 국기 이모지를 이루는 지역 표시 기호 🇦~🇿 26자의 시작 코드 포인트
    private static final int NEGATIVE_CIRCLED_LATIN_CAPITAL_A = 0x1F150; // 검은 원 안의 라틴 대문자 🅐~🅩 26자의 시작 코드 포인트
    private static final int NEGATIVE_SQUARED_LATIN_CAPITAL_A = 0x1F170; // 검은 사각형 안의 라틴 대문자 🅰~🆉 26자의 시작 코드 포인트

    private static final Normalizer2 nfkdInstance = Normalizer2.getNFKDInstance();

    // 키: Hangul_Syllable_Type이 첫소리(L)·가운뎃소리(V)·끝소리(T)인 조합형 자모의 코드 포인트
    // 값: 접두어(HANGUL CHOSEONG/JUNGSEONG/JONGSEONG)를 뗀 자모 이름에 매칭되는 한글 호환 자모의 코드 포인트
    // 이름이 같은 호환 자모가 없는 조합형 자모(호환 자모가 없는 옛한글 자모, 채움 문자)는 포함하지 않음
    public static final Map<Integer, Integer> CONJOINING_COMPATIBILITY_JAMO_MAP = createConjoiningCompatibilityJamoMap();
    // 호환 분해가 없는 문자 유사 기호 → 라틴 소문자
    public static final Map<Integer, Integer> LETTER_LIKE_SYMBOL_TO_LOWER_CASE_LATIN_MAP = createLetterLikeSymbolLowerCaseLatinMap();
    // 라틴 소문자와 형태가 혼동되는 키릴 · 그리스 소문자 → 라틴 소문자
    public static final Map<Integer, Integer> CROSS_SCRIPT_HOMOGLYPH_TO_LATIN_MAP = Map.ofEntries(
            Map.entry(0x0430, (int) 'a'), // 키릴 а
            Map.entry(0x0435, (int) 'e'), // 키릴 е
            Map.entry(0x043E, (int) 'o'), // 키릴 о
            Map.entry(0x0440, (int) 'p'), // 키릴 р
            Map.entry(0x0441, (int) 'c'), // 키릴 с
            Map.entry(0x0443, (int) 'y'), // 키릴 у
            Map.entry(0x0445, (int) 'x'), // 키릴 х
            Map.entry(0x0455, (int) 's'), // 키릴 ѕ
            Map.entry(0x0456, (int) 'i'), // 키릴 і
            Map.entry(0x0458, (int) 'j'), // 키릴 ј
            Map.entry(0x04BB, (int) 'h'), // 키릴 һ
            Map.entry(0x0501, (int) 'd'), // 키릴 ԁ
            Map.entry(0x051B, (int) 'q'), // 키릴 ԛ
            Map.entry(0x051D, (int) 'w'), // 키릴 ԝ
            Map.entry(0x03B1, (int) 'a'), // 그리스 α
            Map.entry(0x03B2, (int) 'b'), // 그리스 β
            Map.entry(0x03B5, (int) 'e'), // 그리스 ε
            Map.entry(0x03B9, (int) 'i'), // 그리스 ι
            Map.entry(0x03BA, (int) 'k'), // 그리스 κ
            Map.entry(0x03BD, (int) 'v'), // 그리스 ν
            Map.entry(0x03BF, (int) 'o'), // 그리스 ο
            Map.entry(0x03C1, (int) 'p'), // 그리스 ρ
            Map.entry(0x03C4, (int) 't'), // 그리스 τ
            Map.entry(0x03C5, (int) 'u'), // 그리스 υ
            Map.entry(0x03C7, (int) 'x')); // 그리스 χ
    // 한글 주변에서 한글 자모를 흉내 내는 문자 → 호환 자모
    public static final Map<Integer, Integer> HANGUL_CONTEXT_LOOK_ALIKE_TO_JAMO_MAP = Map.ofEntries(
            Map.entry((int) '1', (int) 'ㅣ'),
            Map.entry((int) 'l', (int) 'ㅣ'),
            Map.entry((int) '|', (int) 'ㅣ'),
            Map.entry((int) '0', (int) 'ㅇ'),
            Map.entry((int) 'o', (int) 'ㅇ'),
            Map.entry((int) '^', (int) 'ㅅ'),
            Map.entry((int) '人', (int) 'ㅅ'));
    // 라틴 문자 주변에서 라틴 문자를 흉내 내는 숫자·기호 → 라틴 소문자
    public static final Map<Integer, Integer> LATIN_CONTEXT_LOOK_ALIKE_TO_LEET_LETTER_MAP = Map.ofEntries(
            Map.entry((int) '0', (int) 'o'),
            Map.entry((int) '1', (int) 'i'),
            Map.entry((int) '3', (int) 'e'),
            Map.entry((int) '4', (int) 'a'),
            Map.entry((int) '5', (int) 's'),
            Map.entry((int) '7', (int) 't'),
            Map.entry((int) '8', (int) 'b'),
            Map.entry((int) '$', (int) 's'),
            Map.entry((int) '@', (int) 'a'),
            Map.entry((int) '+', (int) 't'));

    private static Map<Integer, Integer> createConjoiningCompatibilityJamoMap() {
        // 1. 한글 호환 자모의 각 문자를 NFKD로 분해한 조합형 자모의 이름에서 접두어를 떼어, 조합형 자모 이름 → 호환 자모 맵을 만듦
        Map<String, Integer> conjoiningJamoNameCompatibilityJamoMap = new HashMap<>();
        UnicodeSet compatibilityJamoBlock = new UnicodeSet()
                .applyIntPropertyValue(UProperty.BLOCK, UCharacter.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO_ID);
        for (UnicodeSet.EntryRange range : compatibilityJamoBlock.ranges()) {
            for (int compatibilityJamo = range.codepoint; compatibilityJamo <= range.codepointEnd; compatibilityJamo++) {
                // 채움 문자(HANGUL FILLER)는 보이지 않는 문자로 제거되므로 대응 대상에서 제외
                if (UCharacter.hasBinaryProperty(compatibilityJamo, UProperty.DEFAULT_IGNORABLE_CODE_POINT)) {
                    continue;
                }
                String conjoiningJamo = nfkdInstance.normalize(Character.toString(compatibilityJamo));
                String conjoiningJamoName = getConjoiningJamoName(conjoiningJamo.codePointAt(0));
                if (conjoiningJamoName != null) {
                    conjoiningJamoNameCompatibilityJamoMap.put(conjoiningJamoName, compatibilityJamo);
                }
            }
        }

        // 2. Hangul_Syllable_Type이 L·V·T인 조합형 자모를 모두 순회하며, 접두어를 뗀 이름이 1단계 맵에 있으면 그 호환 자모에 대응시킴
        // (예: 첫소리 ᄀ과 끝소리 ᆨ이 모두 호환 자모 ㄱ에 대응)
        UnicodeSet conjoiningJamoSet = new UnicodeSet();
        for (int syllableType : CONJOINING_JAMO_SYLLABLE_TYPES) {
            conjoiningJamoSet.addAll(
                    new UnicodeSet().applyIntPropertyValue(UProperty.HANGUL_SYLLABLE_TYPE, syllableType));
        }
        Map<Integer, Integer> conjoiningCompatibilityJamoMap = new HashMap<>();
        for (UnicodeSet.EntryRange range : conjoiningJamoSet.ranges()) {
            for (int conjoiningJamo = range.codepoint; conjoiningJamo <= range.codepointEnd; conjoiningJamo++) {
                String conjoiningJamoName = getConjoiningJamoName(conjoiningJamo);
                Integer compatibilityJamo = conjoiningJamoName == null ?
                        null : conjoiningJamoNameCompatibilityJamoMap.get(conjoiningJamoName);
                if (compatibilityJamo != null) {
                    conjoiningCompatibilityJamoMap.put(conjoiningJamo, compatibilityJamo);
                }
            }
        }
        return Map.copyOf(conjoiningCompatibilityJamoMap);
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

    private static Map<Integer, Integer> createLetterLikeSymbolLowerCaseLatinMap() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int offset = 0; offset < LATIN_LETTER_COUNT; offset++) {
            int latin_a_plus_offset = 'a' + offset;
            map.put(REGIONAL_INDICATOR_A + offset, latin_a_plus_offset);
            map.put(NEGATIVE_CIRCLED_LATIN_CAPITAL_A + offset, latin_a_plus_offset);
            map.put(NEGATIVE_SQUARED_LATIN_CAPITAL_A + offset, latin_a_plus_offset);
        }
        return Map.copyOf(map);
    }
}
