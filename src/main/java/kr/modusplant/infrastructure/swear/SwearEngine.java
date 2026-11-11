package kr.modusplant.infrastructure.swear;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.BytesTrie;
import com.ibm.icu.util.CharsTrie;
import com.ibm.icu.util.CharsTrieBuilder;
import com.ibm.icu.util.StringTrieBuilder;
import kr.modusplant.infrastructure.config.exception.ConfigurationException;
import kr.modusplant.infrastructure.config.exception.enums.ConfigurationErrorCode;
import kr.modusplant.infrastructure.normalization.TextNormalizationType;
import kr.modusplant.infrastructure.normalization.TextNormalizedView;
import kr.modusplant.infrastructure.normalization.TextNormalizer;

import java.util.*;

import static kr.modusplant.infrastructure.normalization.TextNormalizationCharacterMap.CROSS_SCRIPT_CONFUSABLE_LATIN_MAP;
import static kr.modusplant.infrastructure.normalization.TextNormalizationCharacterMap.LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP;
import static kr.modusplant.infrastructure.normalization.TextNormalizationType.*;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.JamoPosition.INITIAL;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.JamoPosition.STANDALONE;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.SourceCharacterKind.LATIN_LETTER;
import static kr.modusplant.infrastructure.normalization.TextNormalizedView.SourceCharacterKind.STANDALONE_JAMO;

// 비속어 사전과 허용 목록을 정규화 뷰별 ICU4J 트라이로 컴파일하고, 텍스트의 유효하고 중복 제거된 매칭을 반환
public final class SwearEngine {
    private static final int MINIMUM_LINE_INITIAL_WORD_CODE_POINT_COUNT = 2;
    private static final List<TextNormalizationType> ALLOWLIST_NORMALIZATION_TYPES = List.of(LIGHT, AGGRESSIVE, KEYSTROKE);

    private final TextNormalizer textNormalizer;
    private final Map<TextNormalizationType, KeywordTrie> swearKeywordTries;
    private final Map<TextNormalizationType, Map<String, List<CompiledSwearWord>>> keywordCompiledSwearWords;
    private final Map<TextNormalizationType, KeywordTrie> allowedKeywordTries;

    private SwearEngine(TextNormalizer textNormalizer, Map<TextNormalizationType, KeywordTrie> swearKeywordTries,
                        Map<TextNormalizationType, Map<String, List<CompiledSwearWord>>> keywordCompiledSwearWords,
                        Map<TextNormalizationType, KeywordTrie> allowedKeywordTries) {
        this.textNormalizer = textNormalizer;
        this.swearKeywordTries = swearKeywordTries;
        this.keywordCompiledSwearWords = keywordCompiledSwearWords;
        this.allowedKeywordTries = allowedKeywordTries;
    }

    public static SwearEngine create(TextNormalizer textNormalizer, List<SwearWord> swearWords,
                                     List<String> allowedWords) {
        Map<TextNormalizationType, Map<String, List<CompiledSwearWord>>> keywordCompiledSwearWords =
                new EnumMap<>(TextNormalizationType.class);
        for (TextNormalizationType textNormalizationType : TextNormalizationType.values()) {
            keywordCompiledSwearWords.put(textNormalizationType, new HashMap<>());
        }
        List<String> scriptMismatches = new ArrayList<>();
        for (SwearWord swearWord : swearWords) {
            Map<TextNormalizationType, String> keywords = compileWord(textNormalizer, swearWord.word());
            if (keywords.isEmpty()) {
                continue;
            }
            SwearScript derivedScript = SwearScript.of(keywords.get(LIGHT));
            if (swearWord.script() != derivedScript) {
                scriptMismatches.add(swearWord.word() + "(" + swearWord.script() + " → " + derivedScript + ")");
                continue;
            }
            CompiledSwearWord compiledSwearWord = CompiledSwearWord.of(swearWord, keywords.get(LIGHT));
            keywords.forEach((textNormalizationType, keyword) -> keywordCompiledSwearWords.get(textNormalizationType)
                    .computeIfAbsent(keyword, ignored -> new ArrayList<>())
                    .add(compiledSwearWord));
        }
        // 저장된 문자 체계가 단어의 문자와 다른 단어를 모두 모아 한 번에 실패시킴
        if (!scriptMismatches.isEmpty()) {
            throw new ConfigurationException(ConfigurationErrorCode.INCORRECT_SWEAR_SCRIPT, scriptMismatches.toArray(String[]::new));
        }

        Map<TextNormalizationType, List<String>> allowedKeywords = new EnumMap<>(TextNormalizationType.class);
        for (TextNormalizationType textNormalizationType : ALLOWLIST_NORMALIZATION_TYPES) {
            allowedKeywords.put(textNormalizationType, new ArrayList<>());
        }
        for (String allowedWord : allowedWords) {
            compileWord(textNormalizer, allowedWord).forEach((textNormalizationType, keyword) -> {
                if (allowedKeywords.containsKey(textNormalizationType)) {
                    allowedKeywords.get(textNormalizationType).add(keyword);
                }
            });
        }

        Map<TextNormalizationType, KeywordTrie> swearKeywordTries = new EnumMap<>(TextNormalizationType.class);
        Map<TextNormalizationType, Map<String, List<CompiledSwearWord>>> immutableKeywordCompiledSwearWords =
                new EnumMap<>(TextNormalizationType.class);
        keywordCompiledSwearWords.forEach(
                (textNormalizationType, keywordCompiledSwearWordsAsValue) -> {
                    swearKeywordTries.put(textNormalizationType, KeywordTrie.of(keywordCompiledSwearWordsAsValue.keySet()));
                    Map<String, List<CompiledSwearWord>> immutableKeywordCompiledSwearWordsAsValue = new HashMap<>();
                    keywordCompiledSwearWordsAsValue.forEach(
                            (keyword, compiledSwearWords) ->
                                    immutableKeywordCompiledSwearWordsAsValue.put(
                                            keyword, List.copyOf(compiledSwearWords)));
                    immutableKeywordCompiledSwearWords.put(
                            textNormalizationType, Map.copyOf(immutableKeywordCompiledSwearWordsAsValue));
                });
        Map<TextNormalizationType, KeywordTrie> allowedKeywordTries = new EnumMap<>(TextNormalizationType.class);
        allowedKeywords.forEach((textNormalizationType, allowedKeywordsAsValue) ->
                allowedKeywordTries.put(textNormalizationType, KeywordTrie.of(allowedKeywordsAsValue)));

        return new SwearEngine(textNormalizer, Collections.unmodifiableMap(swearKeywordTries),
                Collections.unmodifiableMap(immutableKeywordCompiledSwearWords),
                Collections.unmodifiableMap(allowedKeywordTries));
    }

    public List<SwearMatch> match(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        TextNormalizedView lightlyNormalizedView = textNormalizer.lightlyNormalize(text);
        TextNormalizedView aggressivelyNormalizedView = textNormalizer.aggressivelyNormalize(lightlyNormalizedView);
        Map<TextNormalizationType, TextNormalizedView> views = new EnumMap<>(TextNormalizationType.class);
        views.put(LIGHT, lightlyNormalizedView);
        views.put(AGGRESSIVE, aggressivelyNormalizedView);
        views.put(KEYSTROKE,
                textNormalizer.decomposeIntoKeystrokes(aggressivelyNormalizedView, lightlyNormalizedView));
        views.put(LINE_INITIAL, textNormalizer.extractLineInitials(lightlyNormalizedView));
        KeyboardRuns keyboardRuns = KeyboardRuns.of(lightlyNormalizedView.getText());

        List<SwearMatch> keptMatches = new ArrayList<>();
        for (TextNormalizationType textNormalizationType : TextNormalizationType.values()) {
            TextNormalizedView view = views.get(textNormalizationType);
            List<KeywordMatch> allowedKeywordMatches = allowedKeywordTries.containsKey(textNormalizationType)
                    ? allowedKeywordTries.get(textNormalizationType).findAll(view.getText())
                    : List.of();
            for (KeywordMatch keywordMatch : swearKeywordTries.get(textNormalizationType).findAll(view.getText())) {
                int start = keywordMatch.start();
                int end = keywordMatch.end();
                if (isInsideAllowedKeywordMatch(allowedKeywordMatches, start, end)) {
                    continue;
                }
                for (CompiledSwearWord compiledSwearWord :
                        keywordCompiledSwearWords.get(textNormalizationType).get(keywordMatch.keyword())) {
                    if (!isValidMatch(textNormalizationType, compiledSwearWord, view, lightlyNormalizedView,
                            keyboardRuns, start, end)) {
                        continue;
                    }
                    SwearMatch swearMatch = new SwearMatch(compiledSwearWord.swearWord(), textNormalizationType,
                            calculateOriginalStart(view, start, end), calculateOriginalEnd(view, start, end));
                    if (!overlapsSameWordWithKeptMatch(keptMatches, swearMatch)) {
                        keptMatches.add(swearMatch);
                    }
                }
            }
        }
        return List.copyOf(keptMatches);
    }

    // 경량 정규화 형태와, 공격적 정규화가 바꾸지 않는 단어에 한해 공격적 정규화 및 키 입력 형태를 뷰별로 반환
    private static Map<TextNormalizationType, String> compileWord(TextNormalizer textNormalizer, String word) {
        Map<TextNormalizationType, String> keywords = new EnumMap<>(TextNormalizationType.class);
        TextNormalizedView lightlyNormalizedView = textNormalizer.lightlyNormalize(word);
        String lightlyNormalizedWord = lightlyNormalizedView.getText();
        if (lightlyNormalizedWord.isEmpty()) {
            return keywords;
        }
        keywords.put(LIGHT, lightlyNormalizedWord);
        keywords.put(LINE_INITIAL, lightlyNormalizedWord);
        TextNormalizedView aggressivelyNormalizedView = textNormalizer.aggressivelyNormalize(lightlyNormalizedView);
        if (aggressivelyNormalizedView.getText().equals(lightlyNormalizedWord)) {
            keywords.put(AGGRESSIVE, lightlyNormalizedWord);
            String keystrokeWord = textNormalizer
                    .decomposeIntoKeystrokes(aggressivelyNormalizedView, lightlyNormalizedView).getText();
            if (!keystrokeWord.isEmpty()) {
                keywords.put(KEYSTROKE, keystrokeWord);
            }
        }
        return keywords;
    }

    private static boolean isInsideAllowedKeywordMatch(List<KeywordMatch> allowedKeywordMatches, int start, int end) {
        for (KeywordMatch allowedKeywordMatch : allowedKeywordMatches) {
            if (allowedKeywordMatch.start() <= start && end <= allowedKeywordMatch.end()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidMatch(TextNormalizationType textNormalizationType, CompiledSwearWord compiledSwearWord,
                                        TextNormalizedView view, TextNormalizedView lightlyNormalizedView,
                                        KeyboardRuns keyboardRuns, int start, int end) {
        return switch (textNormalizationType) {
            case LIGHT -> true;
            case AGGRESSIVE -> isTokenAlignedAcrossWhitespace(view, lightlyNormalizedView, start, end);
            case KEYSTROKE -> isTokenAlignedAcrossWhitespace(view, lightlyNormalizedView, start, end)
                    && isValidKeystrokeMatch(compiledSwearWord.swearWord().script(), view, keyboardRuns, start, end);
            case LINE_INITIAL -> compiledSwearWord.codePointCount() >= MINIMUM_LINE_INITIAL_WORD_CODE_POINT_COUNT;
        };
    }

    private static boolean isValidKeystrokeMatch(SwearScript swearScript, TextNormalizedView keystrokeView,
                                                 KeyboardRuns keyboardRuns, int start, int end) {
        return switch (swearScript) {
            case HANGUL -> hasValidJamoRoles(keystrokeView, start, end)
                    && coversWholeKeyboardRuns(keystrokeView, keyboardRuns, start, end);
            case LATIN -> isTypedAsStandaloneJamo(keystrokeView, start, end);
            case OTHER -> true;
        };
    }

    // 공백을 넘는 매칭은 경량 정규화 뷰의 토큰 전체를 덮어야 유효
    private static boolean isTokenAlignedAcrossWhitespace(TextNormalizedView view,
                                                          TextNormalizedView lightlyNormalizedView,
                                                          int start, int end) {
        int lightlyNormalizedStart = calculateLightlyNormalizedStart(view, start, end);
        int lightlyNormalizedEnd = calculateLightlyNormalizedEnd(view, start, end);
        String lightlyNormalizedText = lightlyNormalizedView.getText();
        if (!containsWhitespace(lightlyNormalizedText, lightlyNormalizedStart, lightlyNormalizedEnd)) {
            return true;
        }
        boolean startsAtTokenStart = lightlyNormalizedStart == 0
                || UCharacter.isUWhiteSpace(lightlyNormalizedText.codePointBefore(lightlyNormalizedStart));
        boolean endsAtTokenEnd = lightlyNormalizedEnd == lightlyNormalizedText.length()
                || UCharacter.isUWhiteSpace(lightlyNormalizedText.codePointAt(lightlyNormalizedEnd));
        return startsAtTokenStart && endsAtTokenEnd;
    }

    // 초성 또는 낱자 자모에서 시작해 음절 끝 또는 낱자 자모에서 끝나야 유효
    private static boolean hasValidJamoRoles(TextNormalizedView keystrokeView, int start, int end) {
        TextNormalizedView.JamoPosition firstJamoPosition = keystrokeView.getFirstSourceJamoPosition(start);
        int lastIndex = end - 1;
        boolean startsAtInitialOrStandaloneJamo = firstJamoPosition == INITIAL || firstJamoPosition == STANDALONE;
        boolean endsAtSyllableEndOrStandaloneJamo = keystrokeView.getSyllableEnds(lastIndex)
                || keystrokeView.getLastSourceJamoPosition(lastIndex) == STANDALONE;
        return startsAtInitialOrStandaloneJamo && endsAtSyllableEndOrStandaloneJamo;
    }

    // 라틴 문자에서 변환된 유닛이 있으면, 그 유닛이 닿는 경량 정규화 뷰의 라틴 문자 연속 구간을 모두 덮어야 유효
    private static boolean coversWholeKeyboardRuns(TextNormalizedView keystrokeView, KeyboardRuns keyboardRuns,
                                                   int start, int end) {
        int lightlyNormalizedStart = calculateLightlyNormalizedStart(keystrokeView, start, end);
        int lightlyNormalizedEnd = calculateLightlyNormalizedEnd(keystrokeView, start, end);
        for (int index = start; index < end; index++) {
            if (keystrokeView.getSourceCharacterKind(index) == LATIN_LETTER
                    && !keyboardRuns.isCoveredBy(keystrokeView.getLightlyNormalizedStart(index),
                    lightlyNormalizedStart, lightlyNormalizedEnd)) {
                return false;
            }
        }
        return true;
    }

    // 라틴 단어의 키 입력 매칭은 모든 유닛이 낱자 자모로 입력되어야 유효
    private static boolean isTypedAsStandaloneJamo(TextNormalizedView keystrokeView, int start, int end) {
        for (int index = start; index < end; index++) {
            if (keystrokeView.getSourceCharacterKind(index) != STANDALONE_JAMO) {
                return false;
            }
        }
        return true;
    }

    private static boolean isKeyboardLetter(int codePoint) {
        return LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP.containsKey(codePoint)
                || CROSS_SCRIPT_CONFUSABLE_LATIN_MAP.containsKey(codePoint);
    }

    private static boolean containsWhitespace(String text, int start, int end) {
        int index = start;
        while (index < end) {
            int codePoint = text.codePointAt(index);
            if (UCharacter.isUWhiteSpace(codePoint)) {
                return true;
            }
            index += Character.charCount(codePoint);
        }
        return false;
    }

    private static boolean overlapsSameWordWithKeptMatch(List<SwearMatch> keptMatches, SwearMatch swearMatch) {
        for (SwearMatch keptMatch : keptMatches) {
            if (keptMatch.swearWord().equals(swearMatch.swearWord())
                    && keptMatch.originalStart() < swearMatch.originalEnd()
                    && swearMatch.originalStart() < keptMatch.originalEnd()) {
                return true;
            }
        }
        return false;
    }

    private static int calculateLightlyNormalizedStart(TextNormalizedView view, int start, int end) {
        int lightlyNormalizedStart = Integer.MAX_VALUE;
        for (int index = start; index < end; index++) {
            lightlyNormalizedStart = Math.min(lightlyNormalizedStart, view.getLightlyNormalizedStart(index));
        }
        return lightlyNormalizedStart;
    }

    private static int calculateLightlyNormalizedEnd(TextNormalizedView view, int start, int end) {
        int lightlyNormalizedEnd = Integer.MIN_VALUE;
        for (int index = start; index < end; index++) {
            lightlyNormalizedEnd = Math.max(lightlyNormalizedEnd, view.getLightlyNormalizedEnd(index));
        }
        return lightlyNormalizedEnd;
    }

    private static int calculateOriginalStart(TextNormalizedView view, int start, int end) {
        int originalStart = Integer.MAX_VALUE;
        for (int index = start; index < end; index++) {
            originalStart = Math.min(originalStart, view.getOriginalStart(index));
        }
        return originalStart;
    }

    private static int calculateOriginalEnd(TextNormalizedView view, int start, int end) {
        int originalEnd = Integer.MIN_VALUE;
        for (int index = start; index < end; index++) {
            originalEnd = Math.max(originalEnd, view.getOriginalEnd(index));
        }
        return originalEnd;
    }

    // 키워드를 ICU4J CharsTrie로 직렬화하고, 텍스트의 모든 시작 위치에서 겹치는 매칭까지 모두 찾음
    // CharsTrie는 탐색 상태를 가지므로 매칭마다 직렬화된 문자열로 새로 만듦
    private record KeywordTrie(CharSequence trieChars, List<String> keywords) {
        private static KeywordTrie of(Collection<String> keywords) {
            List<String> distinctKeywords = List.copyOf(new HashSet<>(keywords));
            if (distinctKeywords.isEmpty()) {
                return new KeywordTrie("", distinctKeywords);
            }
            CharsTrieBuilder charsTrieBuilder = new CharsTrieBuilder();
            for (int index = 0; index < distinctKeywords.size(); index++) {
                charsTrieBuilder.add(distinctKeywords.get(index), index);
            }
            String trieChars = charsTrieBuilder.buildCharSequence(StringTrieBuilder.Option.FAST).toString();
            return new KeywordTrie(trieChars, distinctKeywords);
        }

        private List<KeywordMatch> findAll(String text) {
            if (keywords.isEmpty()) {
                return List.of();
            }
            List<KeywordMatch> keywordMatches = new ArrayList<>();
            CharsTrie charsTrie = new CharsTrie(trieChars, 0);
            for (int start = 0; start < text.length(); start++) {
                charsTrie.reset();
                for (int index = start; index < text.length(); index++) {
                    BytesTrie.Result result = charsTrie.next(text.charAt(index));
                    if (result.hasValue()) {
                        keywordMatches.add(new KeywordMatch(keywords.get(charsTrie.getValue()), start, index + 1));
                    }
                    if (!result.hasNext()) {
                        break;
                    }
                }
            }
            return List.copyOf(keywordMatches);
        }
    }

    // 경량 정규화 텍스트의 각 UTF-16 위치가 속한 두벌식 키보드 문자 연속 구간 [runStart, runEnd)를 한 번의 순회로 계산함
    // 두벌식 키보드 문자가 아닌 위치는 빈 구간 [index, index)를 가짐
    private record KeyboardRuns(int[] runStarts, int[] runEnds) {
        private static KeyboardRuns of(String lightlyNormalizedText) {
            int[] runStarts = new int[lightlyNormalizedText.length()];
            int[] runEnds = new int[lightlyNormalizedText.length()];
            int runStart = 0;
            int index = 0;
            while (index < lightlyNormalizedText.length()) {
                int codePoint = lightlyNormalizedText.codePointAt(index);
                int nextIndex = index + Character.charCount(codePoint);
                if (!isKeyboardLetter(codePoint)) {
                    fillRun(runStarts, runEnds, runStart, index);
                    Arrays.fill(runStarts, index, nextIndex, index);
                    Arrays.fill(runEnds, index, nextIndex, index);
                    runStart = nextIndex;
                }
                index = nextIndex;
            }
            fillRun(runStarts, runEnds, runStart, lightlyNormalizedText.length());
            return new KeyboardRuns(runStarts, runEnds);
        }

        // 구간 [runStart, runEnd)의 모든 위치에 그 구간을 기록함
        private static void fillRun(int[] runStarts, int[] runEnds, int runStart, int runEnd) {
            Arrays.fill(runStarts, runStart, runEnd, runStart);
            Arrays.fill(runEnds, runStart, runEnd, runEnd);
        }

        private boolean isCoveredBy(int lightlyNormalizedIndex, int lightlyNormalizedStart, int lightlyNormalizedEnd) {
            return lightlyNormalizedStart <= runStarts[lightlyNormalizedIndex]
                    && runEnds[lightlyNormalizedIndex] <= lightlyNormalizedEnd;
        }
    }

    // 뷰 텍스트의 반열린 구간 [start, end)에서 찾은 키워드
    private record KeywordMatch(String keyword, int start, int end) {
    }

    private record CompiledSwearWord(SwearWord swearWord, int codePointCount) {
        private static CompiledSwearWord of(SwearWord swearWord, String lightlyNormalizedWord) {
            return new CompiledSwearWord(swearWord,
                    lightlyNormalizedWord.codePointCount(0, lightlyNormalizedWord.length()));
        }
    }
}
