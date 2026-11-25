package kr.modusplant.infrastructure.prohibitedword;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.BytesTrie;
import com.ibm.icu.util.CharsTrie;
import com.ibm.icu.util.CharsTrieBuilder;
import com.ibm.icu.util.StringTrieBuilder;
import kr.modusplant.infrastructure.config.exception.ConfigurationException;
import kr.modusplant.infrastructure.config.exception.enums.ConfigurationErrorCode;
import kr.modusplant.infrastructure.normalization.TextNormalizationType;
import kr.modusplant.infrastructure.normalization.TextNormalizer;
import kr.modusplant.infrastructure.normalization.view.AggressivelyNormalizedView;
import kr.modusplant.infrastructure.normalization.view.LightlyNormalizedView;
import kr.modusplant.infrastructure.normalization.view.SyllableNormalizedView;
import kr.modusplant.infrastructure.normalization.view.supers.TextNormalizedView;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isAsciiLowerCaseLetter;
import static kr.modusplant.infrastructure.normalization.TextNormalizationType.*;

public final class ProhibitedWordEngine {
    private static final int MINIMUM_LINE_INITIAL_KEYWORD_CODE_POINT_COUNT = 2;

    private final TextNormalizer textNormalizer;
    private final Map<TextNormalizationType, KeywordTrie> prohibitedWordKeywordTries;
    private final Map<TextNormalizationType, KeywordTrie> allowedWordKeywordTries;
    private final Map<TextNormalizationType, Map<String, List<ProhibitedWord>>> keywordProhibitedWords;

    private ProhibitedWordEngine(TextNormalizer textNormalizer,
                        Map<TextNormalizationType, KeywordTrie> prohibitedWordKeywordTries,
                        Map<TextNormalizationType, KeywordTrie> allowedWordKeywordTries,
                        Map<TextNormalizationType, Map<String, List<ProhibitedWord>>> keywordProhibitedWords) {
        this.textNormalizer = textNormalizer;
        this.prohibitedWordKeywordTries = prohibitedWordKeywordTries;
        this.allowedWordKeywordTries = allowedWordKeywordTries;
        this.keywordProhibitedWords = keywordProhibitedWords;
    }

    public static ProhibitedWordEngine create(TextNormalizer textNormalizer,
                                     List<ProhibitedWord> prohibitedWords,
                                     List<String> allowedWords) {
        Map<TextNormalizationType, Map<String, List<ProhibitedWord>>> keywordProhibitedWords =
                new EnumMap<>(TextNormalizationType.class);
        for (TextNormalizationType textNormalizationType : TextNormalizationType.values()) {
            keywordProhibitedWords.put(textNormalizationType, new HashMap<>());
        }
        List<String> scriptMismatches = new ArrayList<>();
        for (ProhibitedWord prohibitedWord : prohibitedWords) {
            Map<TextNormalizationType, String> keywords = normalizeWord(textNormalizer, prohibitedWord.word());
            if (keywords.isEmpty()) {
                continue;
            }
            ProhibitedWordScript derivedScript = ProhibitedWordScript.of(keywords.get(LIGHT));
            if (prohibitedWord.script() != derivedScript) {
                scriptMismatches.add(prohibitedWord.word() + "(" + prohibitedWord.script() + " → " + derivedScript + ")");
                continue;
            }
            if (prohibitedWord.script() != ProhibitedWordScript.HANGUL) {
                keywords.remove(KEYSTROKE);
            }
            keywords.forEach((textNormalizationType, keyword) ->
                    keywordProhibitedWords.get(textNormalizationType)
                            .computeIfAbsent(keyword, ignored -> new ArrayList<>())
                            .add(prohibitedWord));
        }
        if (!scriptMismatches.isEmpty()) {
            throw new ConfigurationException(
                    ConfigurationErrorCode.INCORRECT_PROHIBITED_WORD_SCRIPT, scriptMismatches.toArray(String[]::new));
        }

        Map<TextNormalizationType, List<String>> keywordAllowedWords = new EnumMap<>(TextNormalizationType.class);
        for (TextNormalizationType textNormalizationType : TextNormalizationType.values()) {
            keywordAllowedWords.put(textNormalizationType, new ArrayList<>());
        }
        for (String allowedWord : allowedWords) {
            normalizeWord(textNormalizer, allowedWord)
                    .forEach((textNormalizationType, keyword) ->
                            keywordAllowedWords.get(textNormalizationType).add(keyword));
        }

        Map<TextNormalizationType, KeywordTrie> prohibitedWordKeywordTries = new EnumMap<>(TextNormalizationType.class);
        keywordProhibitedWords.forEach((textNormalizationType, keywordProhibitedWordsAsValue) ->
                prohibitedWordKeywordTries.put(textNormalizationType, KeywordTrie.of(keywordProhibitedWordsAsValue.keySet())));
        Map<TextNormalizationType, Map<String, List<ProhibitedWord>>> immutableKeywordProhibitedWords =
                keywordProhibitedWords.entrySet().stream()
                        .collect(Collectors.toUnmodifiableMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().entrySet().stream()
                                        .collect(Collectors.toUnmodifiableMap(
                                                Map.Entry::getKey,
                                                keywordEntry -> List.copyOf(keywordEntry.getValue())))));
        Map<TextNormalizationType, KeywordTrie> allowedWordKeywordTries = new EnumMap<>(TextNormalizationType.class);
        keywordAllowedWords.forEach((textNormalizationType, allowedWordKeywordsAsValue) ->
                allowedWordKeywordTries.put(textNormalizationType, KeywordTrie.of(allowedWordKeywordsAsValue)));

        return new ProhibitedWordEngine(
                textNormalizer,
                Collections.unmodifiableMap(prohibitedWordKeywordTries),
                Collections.unmodifiableMap(allowedWordKeywordTries),
                immutableKeywordProhibitedWords);
    }

    public List<ProhibitedWordMatch> match(String text) {
        if (StringUtils.isBlank(text)) {
            return List.of();
        }
        LightlyNormalizedView lightlyNormalizedView = textNormalizer.normalizeLightly(text);
        AggressivelyNormalizedView aggressivelyNormalizedView =
                textNormalizer.normalizeAggressively(lightlyNormalizedView);
        SyllableNormalizedView syllableView =
                textNormalizer.normalizeWithSyllable(aggressivelyNormalizedView, lightlyNormalizedView);
        Map<TextNormalizationType, TextNormalizedView> views = new EnumMap<>(TextNormalizationType.class);
        views.put(LIGHT, lightlyNormalizedView);
        views.put(AGGRESSIVE, aggressivelyNormalizedView);
        views.put(SYLLABLE, syllableView);
        views.put(KEYSTROKE, textNormalizer.normalizeWithKeystroke(aggressivelyNormalizedView));
        views.put(LINE_INITIAL, textNormalizer.normalizeWithLineInitial(lightlyNormalizedView));
        AsciiLowerCaseRuns asciiLowerCaseRuns = AsciiLowerCaseRuns.of(lightlyNormalizedView.getText());

        List<ProhibitedWordMatch> keptMatches = new ArrayList<>();
        Map<ProhibitedWord, TreeMap<Integer, Integer>> keptOriginalSpans = new HashMap<>();
        for (TextNormalizationType textNormalizationType : TextNormalizationType.values()) {
            TextNormalizedView view = views.get(textNormalizationType);
            List<KeywordMatch> allowedWordKeywordMatches =
                    allowedWordKeywordTries.get(textNormalizationType).findAll(view.getText());
            for (KeywordMatch keywordMatch : prohibitedWordKeywordTries.get(textNormalizationType).findAll(view.getText())) {
                int keywordMatchStart = keywordMatch.start();
                int keywordMatchEnd = keywordMatch.end();
                if (isInsideAllowedWordKeywordMatch(allowedWordKeywordMatches, keywordMatchStart, keywordMatchEnd)) {
                    continue;
                }
                for (ProhibitedWord prohibitedWord :
                        keywordProhibitedWords.get(textNormalizationType).get(keywordMatch.keyword())) {
                    if (!isValidMatch(textNormalizationType, keywordMatch.keyword(), view,
                            lightlyNormalizedView, asciiLowerCaseRuns, keywordMatchStart, keywordMatchEnd)) {
                        continue;
                    }
                    ProhibitedWordMatch prohibitedWordMatch = new ProhibitedWordMatch(prohibitedWord, textNormalizationType,
                            view.getOriginalStart(keywordMatchStart), view.getOriginalEnd(keywordMatchEnd - 1));
                    if (!overlapsSameWordWithKeptMatch(keptOriginalSpans, prohibitedWordMatch)) {
                        keptMatches.add(prohibitedWordMatch);
                        keptOriginalSpans.computeIfAbsent(prohibitedWord, ignored -> new TreeMap<>())
                                .put(prohibitedWordMatch.originalStart(), prohibitedWordMatch.originalEnd());
                    }
                }
            }
        }
        return List.copyOf(keptMatches);
    }

    private static Map<TextNormalizationType, String> normalizeWord(TextNormalizer textNormalizer, String word) {
        Map<TextNormalizationType, String> keywords = new EnumMap<>(TextNormalizationType.class);
        LightlyNormalizedView lightlyNormalizedView = textNormalizer.normalizeLightly(word);
        String lightlyNormalizedKeyword = lightlyNormalizedView.getText();
        if (lightlyNormalizedKeyword.isEmpty()) {
            return keywords;
        }
        keywords.put(LIGHT, lightlyNormalizedKeyword);
        keywords.put(LINE_INITIAL, lightlyNormalizedKeyword);
        AggressivelyNormalizedView aggressivelyNormalizedView = textNormalizer.normalizeAggressively(lightlyNormalizedView);
        if (aggressivelyNormalizedView.getText().equals(lightlyNormalizedKeyword)) {
            keywords.put(AGGRESSIVE, lightlyNormalizedKeyword);
            if (textNormalizer.normalizeWithSyllable(aggressivelyNormalizedView, lightlyNormalizedView).getText()
                    .equals(lightlyNormalizedKeyword)) {
                keywords.put(SYLLABLE, lightlyNormalizedKeyword);
            }
            keywords.put(KEYSTROKE, textNormalizer.normalizeWithKeystroke(aggressivelyNormalizedView).getText());
        }
        return keywords;
    }

    private static boolean isInsideAllowedWordKeywordMatch(List<KeywordMatch> allowedWordKeywordMatches, int start, int end) {
        for (KeywordMatch allowedWordKeywordMatch : allowedWordKeywordMatches) {
            if (allowedWordKeywordMatch.start() <= start && end <= allowedWordKeywordMatch.end()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidMatch(TextNormalizationType textNormalizationType,
                                        String keyword, TextNormalizedView view,
                                        LightlyNormalizedView lightlyNormalizedView,
                                        AsciiLowerCaseRuns asciiLowerCaseRuns,
                                        int keywordMatchStart, int keywordMatchEnd) {
        return switch (textNormalizationType) {
            case LIGHT -> true;
            case AGGRESSIVE, SYLLABLE -> isWithinOneTokenOrTokenAligned(
                    view, lightlyNormalizedView, keywordMatchStart, keywordMatchEnd);
            case KEYSTROKE -> isWithinOneTokenOrTokenAligned(
                    view, lightlyNormalizedView, keywordMatchStart, keywordMatchEnd)
                    && coversWholeAsciiLowerCaseRuns(view, asciiLowerCaseRuns, keywordMatchStart, keywordMatchEnd);
            case LINE_INITIAL -> keyword.codePointCount(0, keyword.length())
                    >= MINIMUM_LINE_INITIAL_KEYWORD_CODE_POINT_COUNT;
        };
    }

    private static boolean isWithinOneTokenOrTokenAligned(TextNormalizedView view,
                                                          LightlyNormalizedView lightlyNormalizedView,
                                                          int start, int end) {
        int lightlyNormalizedStart = view.getLightlyNormalizedStart(start);
        int lightlyNormalizedEnd = view.getLightlyNormalizedEnd(end - 1);
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

    // ASCII 소문자가 아닌 위치는 자기 위치의 빈 구간을 가지므로 항상 덮인 것으로 판정됨
    private static boolean coversWholeAsciiLowerCaseRuns(TextNormalizedView view,
                                                         AsciiLowerCaseRuns asciiLowerCaseRuns,
                                                         int keywordMatchStart, int keywordMatchEnd) {
        int lightlyNormalizedStart = view.getLightlyNormalizedStart(keywordMatchStart);
        int lightlyNormalizedEnd = view.getLightlyNormalizedEnd(keywordMatchEnd - 1);
        return asciiLowerCaseRuns.isCoveredBy(lightlyNormalizedStart, lightlyNormalizedStart, lightlyNormalizedEnd)
                && asciiLowerCaseRuns.isCoveredBy(lightlyNormalizedEnd - 1, lightlyNormalizedStart, lightlyNormalizedEnd);
    }

    private static boolean containsWhitespace(String text, int start, int end) {
        int codeUnitIndex = start;
        while (codeUnitIndex < end) {
            int codePoint = text.codePointAt(codeUnitIndex);
            if (UCharacter.isUWhiteSpace(codePoint)) {
                return true;
            }
            codeUnitIndex += Character.charCount(codePoint);
        }
        return false;
    }

    // 후보의 원문 끝보다 앞에서 시작하는 구간 중 가장 늦게 시작하는 구간 하나만 확인함
    private static boolean overlapsSameWordWithKeptMatch(Map<ProhibitedWord, TreeMap<Integer, Integer>> keptOriginalSpans,
                                                         ProhibitedWordMatch prohibitedWordMatch) {
        TreeMap<Integer, Integer> originalSpans = keptOriginalSpans.get(prohibitedWordMatch.prohibitedWord());
        if (originalSpans == null) {
            return false;
        }
        Map.Entry<Integer, Integer> precedingOriginalSpan = originalSpans.lowerEntry(prohibitedWordMatch.originalEnd());
        return precedingOriginalSpan != null && prohibitedWordMatch.originalStart() < precedingOriginalSpan.getValue();
    }

    private record KeywordTrie(CharSequence serializedSequence) {
        private static KeywordTrie of(Collection<String> keywords) {
            if (keywords.isEmpty()) {
                return new KeywordTrie("");
            }
            CharsTrieBuilder charsTrieBuilder = new CharsTrieBuilder();
            for (String keyword : new HashSet<>(keywords)) {
                charsTrieBuilder.add(keyword, 0);
            }
            return new KeywordTrie(charsTrieBuilder.buildCharSequence(StringTrieBuilder.Option.FAST).toString());
        }

        private List<KeywordMatch> findAll(String text) {
            if (serializedSequence.isEmpty()) {
                return List.of();
            }
            List<KeywordMatch> keywordMatches = new ArrayList<>();
            CharsTrie charsTrie = new CharsTrie(serializedSequence, 0);
            for (int start = 0; start < text.length(); start++) {
                charsTrie.reset();
                for (int index = start; index < text.length(); index++) {
                    BytesTrie.Result result = charsTrie.next(text.charAt(index));
                    if (result.hasValue()) {
                        keywordMatches.add(new KeywordMatch(text.substring(start, index + 1), start, index + 1));
                    }
                    if (!result.hasNext()) {
                        break;
                    }
                }
            }
            return List.copyOf(keywordMatches);
        }
    }

    // 경량 정규화 텍스트의 각 코드 유닛 인덱스가 속한 ASCII 소문자 연속 구간 [runStart, runEnd)를 한 번의 순회로 계산함
    private record AsciiLowerCaseRuns(int[] runStarts, int[] runEnds) {
        private static AsciiLowerCaseRuns of(String lightlyNormalizedText) {
            int[] runStarts = new int[lightlyNormalizedText.length()];
            int[] runEnds = new int[lightlyNormalizedText.length()];
            int runStart = 0;
            int codeUnitIndex = 0;
            while (codeUnitIndex < lightlyNormalizedText.length()) {
                int codePoint = lightlyNormalizedText.codePointAt(codeUnitIndex);
                int nextCodeUnitIndex = codeUnitIndex + Character.charCount(codePoint);
                if (!isAsciiLowerCaseLetter(codePoint)) {
                    fillRun(runStarts, runEnds, runStart, codeUnitIndex);
                    Arrays.fill(runStarts, codeUnitIndex, nextCodeUnitIndex, codeUnitIndex);
                    Arrays.fill(runEnds, codeUnitIndex, nextCodeUnitIndex, codeUnitIndex);
                    runStart = nextCodeUnitIndex;
                }
                codeUnitIndex = nextCodeUnitIndex;
            }
            fillRun(runStarts, runEnds, runStart, lightlyNormalizedText.length());
            return new AsciiLowerCaseRuns(runStarts, runEnds);
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

    private record KeywordMatch(String keyword, int start, int end) {
    }
}
