package kr.modusplant.infrastructure.swear;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.BytesTrie;
import com.ibm.icu.util.CharsTrie;
import com.ibm.icu.util.CharsTrieBuilder;
import com.ibm.icu.util.StringTrieBuilder;
import kr.modusplant.infrastructure.config.exception.ConfigurationException;
import kr.modusplant.infrastructure.config.exception.enums.ConfigurationErrorCode;
import kr.modusplant.infrastructure.normalization.TextNormalizationType;
import kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView;
import kr.modusplant.infrastructure.normalization.TextNormalizedView;
import kr.modusplant.infrastructure.normalization.TextNormalizer;

import java.util.*;

import static kr.modusplant.infrastructure.normalization.TextNormalizationCharacterMap.CROSS_SCRIPT_CONFUSABLE_LATIN_MAP;
import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isAsciiLowerCaseLetter;
import static kr.modusplant.infrastructure.normalization.TextNormalizationType.*;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.CHOSEONG;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.JamoPosition.STANDALONE;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.SourceKind.ASCII_LOWER_CASE_LETTER;
import static kr.modusplant.infrastructure.normalization.TextNormalizedKeystrokeView.SourceKind.STANDALONE_JAMO;

// 비속어 사전과 비속어가 아닌 단어를 정규화 뷰별 ICU4J 트라이로 컴파일하고, 텍스트의 유효하고 중복 제거된 매칭을 반환
public final class SwearEngine {
    private static final int MINIMUM_LINE_INITIAL_WORD_CODE_POINT_COUNT = 2;
    private static final List<TextNormalizationType> NOT_SWEAR_NORMALIZATION_TYPES = List.of(LIGHT, AGGRESSIVE, KEYSTROKE);

    private final TextNormalizer textNormalizer;
    private final Map<TextNormalizationType, KeywordTrie> swearKeywordTries;
    private final Map<TextNormalizationType, Map<String, List<CompiledSwearWord>>> keywordCompiledSwearWords;
    private final Map<TextNormalizationType, KeywordTrie> notSwearKeywordTries;

    private SwearEngine(TextNormalizer textNormalizer, Map<TextNormalizationType, KeywordTrie> swearKeywordTries,
                        Map<TextNormalizationType, Map<String, List<CompiledSwearWord>>> keywordCompiledSwearWords,
                        Map<TextNormalizationType, KeywordTrie> notSwearKeywordTries) {
        this.textNormalizer = textNormalizer;
        this.swearKeywordTries = swearKeywordTries;
        this.keywordCompiledSwearWords = keywordCompiledSwearWords;
        this.notSwearKeywordTries = notSwearKeywordTries;
    }

    public static SwearEngine create(TextNormalizer textNormalizer, List<SwearWord> swearWords,
                                     List<String> notSwearWords) {
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

        Map<TextNormalizationType, List<String>> notSwearKeywords = new EnumMap<>(TextNormalizationType.class);
        for (TextNormalizationType textNormalizationType : NOT_SWEAR_NORMALIZATION_TYPES) {
            notSwearKeywords.put(textNormalizationType, new ArrayList<>());
        }
        for (String notSwearWord : notSwearWords) {
            compileWord(textNormalizer, notSwearWord).forEach((textNormalizationType, keyword) -> {
                if (notSwearKeywords.containsKey(textNormalizationType)) {
                    notSwearKeywords.get(textNormalizationType).add(keyword);
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
        Map<TextNormalizationType, KeywordTrie> notSwearKeywordTries = new EnumMap<>(TextNormalizationType.class);
        notSwearKeywords.forEach((textNormalizationType, notSwearKeywordsAsValue) ->
                notSwearKeywordTries.put(textNormalizationType, KeywordTrie.of(notSwearKeywordsAsValue)));

        return new SwearEngine(textNormalizer, Collections.unmodifiableMap(swearKeywordTries),
                Collections.unmodifiableMap(immutableKeywordCompiledSwearWords),
                Collections.unmodifiableMap(notSwearKeywordTries));
    }

    public List<SwearMatch> match(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        TextNormalizedView lightlyNormalizedView = textNormalizer.normalizeLightly(text);
        TextNormalizedView aggressivelyNormalizedView = textNormalizer.normalizeAggressively(lightlyNormalizedView);
        TextNormalizedKeystrokeView keystrokeView =
                textNormalizer.normalizeWithKeystroke(aggressivelyNormalizedView, lightlyNormalizedView);
        Map<TextNormalizationType, TextNormalizedView> views = new EnumMap<>(TextNormalizationType.class);
        views.put(LIGHT, lightlyNormalizedView);
        views.put(AGGRESSIVE, aggressivelyNormalizedView);
        views.put(KEYSTROKE, keystrokeView.getTextNormalizedView());
        views.put(LINE_INITIAL, textNormalizer.normalizeWithLineInitial(lightlyNormalizedView));
        KeyboardRuns keyboardRuns = KeyboardRuns.of(lightlyNormalizedView.getText());

        List<SwearMatch> keptMatches = new ArrayList<>();
        for (TextNormalizationType textNormalizationType : TextNormalizationType.values()) {
            TextNormalizedView view = views.get(textNormalizationType);
            List<KeywordMatch> notSwearKeywordMatches = notSwearKeywordTries.containsKey(textNormalizationType)
                    ? notSwearKeywordTries.get(textNormalizationType).findAll(view.getText())
                    : List.of();
            for (KeywordMatch keywordMatch : swearKeywordTries.get(textNormalizationType).findAll(view.getText())) {
                int start = keywordMatch.start();
                int end = keywordMatch.end();
                if (isInsideNotSwearKeywordMatch(notSwearKeywordMatches, start, end)) {
                    continue;
                }
                for (CompiledSwearWord compiledSwearWord :
                        keywordCompiledSwearWords.get(textNormalizationType).get(keywordMatch.keyword())) {
                    if (!isValidMatch(textNormalizationType, compiledSwearWord, view, lightlyNormalizedView,
                            keystrokeView, keyboardRuns, start, end)) {
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
        TextNormalizedView lightlyNormalizedView = textNormalizer.normalizeLightly(word);
        String lightlyNormalizedWord = lightlyNormalizedView.getText();
        if (lightlyNormalizedWord.isEmpty()) {
            return keywords;
        }
        keywords.put(LIGHT, lightlyNormalizedWord);
        keywords.put(LINE_INITIAL, lightlyNormalizedWord);
        TextNormalizedView aggressivelyNormalizedView = textNormalizer.normalizeAggressively(lightlyNormalizedView);
        if (aggressivelyNormalizedView.getText().equals(lightlyNormalizedWord)) {
            keywords.put(AGGRESSIVE, lightlyNormalizedWord);
            TextNormalizedView keystrokeTextNormalizedView = textNormalizer
                    .normalizeWithKeystroke(aggressivelyNormalizedView, lightlyNormalizedView)
                    .getTextNormalizedView();
            // 키 입력 정규화가 단어 자체의 글자를 지우면(개ㅅ끼 → ㄱㅐㄲㅣ) 단어보다 짧은 형태가 더 많은 텍스트와 매칭되므로 제외
            if (keystrokeTextNormalizedView.length() > 0
                    && coversWholeLightlyNormalizedText(keystrokeTextNormalizedView, lightlyNormalizedWord.length())) {
                keywords.put(KEYSTROKE, keystrokeTextNormalizedView.getText());
            }
        }
        return keywords;
    }

    private static boolean coversWholeLightlyNormalizedText(TextNormalizedView view, int lightlyNormalizedLength) {
        boolean[] covered = new boolean[lightlyNormalizedLength];
        for (int index = 0; index < view.length(); index++) {
            Arrays.fill(covered, view.getLightlyNormalizedStart(index), view.getLightlyNormalizedEnd(index), true);
        }
        for (boolean isCovered : covered) {
            if (!isCovered) {
                return false;
            }
        }
        return true;
    }

    private static boolean isInsideNotSwearKeywordMatch(List<KeywordMatch> notSwearKeywordMatches, int start, int end) {
        for (KeywordMatch notSwearKeywordMatch : notSwearKeywordMatches) {
            if (notSwearKeywordMatch.start() <= start && end <= notSwearKeywordMatch.end()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidMatch(TextNormalizationType textNormalizationType, CompiledSwearWord compiledSwearWord,
                                        TextNormalizedView view, TextNormalizedView lightlyNormalizedView,
                                        TextNormalizedKeystrokeView keystrokeView, KeyboardRuns keyboardRuns,
                                        int start, int end) {
        return switch (textNormalizationType) {
            case LIGHT -> true;
            case AGGRESSIVE -> isTokenAlignedAcrossWhitespace(view, lightlyNormalizedView, start, end);
            case KEYSTROKE -> isTokenAlignedAcrossWhitespace(view, lightlyNormalizedView, start, end)
                    && isValidKeystrokeMatch(compiledSwearWord.swearWord().script(), keystrokeView, keyboardRuns,
                    start, end);
            case LINE_INITIAL -> compiledSwearWord.codePointCount() >= MINIMUM_LINE_INITIAL_WORD_CODE_POINT_COUNT;
        };
    }

    private static boolean isValidKeystrokeMatch(SwearScript swearScript, TextNormalizedKeystrokeView keystrokeView,
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
    private static boolean hasValidJamoRoles(TextNormalizedKeystrokeView keystrokeView, int start, int end) {
        TextNormalizedKeystrokeView.JamoPosition firstJamoPosition = keystrokeView.getFirstSourceJamoPosition(start);
        int lastIndex = end - 1;
        boolean startsAtChoseongOrStandaloneJamo = firstJamoPosition == CHOSEONG || firstJamoPosition == STANDALONE;
        boolean endsAtSyllableEndOrStandaloneJamo = keystrokeView.isSyllableEnd(lastIndex)
                || keystrokeView.getLastSourceJamoPosition(lastIndex) == STANDALONE;
        return startsAtChoseongOrStandaloneJamo && endsAtSyllableEndOrStandaloneJamo;
    }

    // ASCII 소문자에서 변환된 유닛이 있으면, 그 유닛이 닿는 경량 정규화 뷰의 자판 문자 연속 구간을 모두 덮어야 유효
    private static boolean coversWholeKeyboardRuns(TextNormalizedKeystrokeView keystrokeView, KeyboardRuns keyboardRuns,
                                                   int start, int end) {
        TextNormalizedView textNormalizedView = keystrokeView.getTextNormalizedView();
        int lightlyNormalizedStart = calculateLightlyNormalizedStart(textNormalizedView, start, end);
        int lightlyNormalizedEnd = calculateLightlyNormalizedEnd(textNormalizedView, start, end);
        for (int index = start; index < end; index++) {
            if (keystrokeView.getSourceKind(index) == ASCII_LOWER_CASE_LETTER
                    && !keyboardRuns.isCoveredBy(textNormalizedView.getLightlyNormalizedStart(index),
                    lightlyNormalizedStart, lightlyNormalizedEnd)) {
                return false;
            }
        }
        return true;
    }

    // 라틴 단어의 키 입력 매칭은 모든 유닛이 낱자 자모로 입력되어야 유효
    private static boolean isTypedAsStandaloneJamo(TextNormalizedKeystrokeView keystrokeView, int start, int end) {
        for (int index = start; index < end; index++) {
            if (keystrokeView.getSourceKind(index) != STANDALONE_JAMO) {
                return false;
            }
        }
        return true;
    }

    private static boolean isKeyboardLetter(int codePoint) {
        return isAsciiLowerCaseLetter(codePoint)
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
