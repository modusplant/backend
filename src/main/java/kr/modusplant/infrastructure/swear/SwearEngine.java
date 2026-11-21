package kr.modusplant.infrastructure.swear;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.BytesTrie;
import com.ibm.icu.util.CharsTrie;
import com.ibm.icu.util.CharsTrieBuilder;
import com.ibm.icu.util.StringTrieBuilder;
import kr.modusplant.infrastructure.config.exception.ConfigurationException;
import kr.modusplant.infrastructure.config.exception.enums.ConfigurationErrorCode;
import kr.modusplant.infrastructure.normalization.TextNormalizationType;
import kr.modusplant.infrastructure.normalization.view.AggressivelyNormalizedView;
import kr.modusplant.infrastructure.normalization.view.LightlyNormalizedView;
import kr.modusplant.infrastructure.normalization.view.SyllableNormalizedView;
import kr.modusplant.infrastructure.normalization.view.supers.TextNormalizedView;
import kr.modusplant.infrastructure.normalization.TextNormalizer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static kr.modusplant.infrastructure.normalization.TextNormalizationHelper.isAsciiLowerCaseLetter;
import static kr.modusplant.infrastructure.normalization.TextNormalizationType.*;

public final class SwearEngine {
    private static final int MINIMUM_LINE_INITIAL_KEYWORD_CODE_POINT_COUNT = 2;

    private final TextNormalizer textNormalizer;
    private final Map<TextNormalizationType, KeywordTrie> swearKeywordTries;
    private final Map<TextNormalizationType, KeywordTrie> notSwearKeywordTries;
    private final Map<TextNormalizationType, Map<String, List<SwearWord>>> keywordSwearWords;

    private SwearEngine(TextNormalizer textNormalizer,
                        Map<TextNormalizationType, KeywordTrie> swearKeywordTries,
                        Map<TextNormalizationType, KeywordTrie> notSwearKeywordTries,
                        Map<TextNormalizationType, Map<String, List<SwearWord>>> keywordSwearWords) {
        this.textNormalizer = textNormalizer;
        this.swearKeywordTries = swearKeywordTries;
        this.notSwearKeywordTries = notSwearKeywordTries;
        this.keywordSwearWords = keywordSwearWords;
    }

    public static SwearEngine create(TextNormalizer textNormalizer,
                                     List<SwearWord> swearWords,
                                     List<String> notSwearWords) {
        Map<TextNormalizationType, Map<String, List<SwearWord>>> keywordSwearWords =
                new EnumMap<>(TextNormalizationType.class);
        for (TextNormalizationType textNormalizationType : TextNormalizationType.values()) {
            keywordSwearWords.put(textNormalizationType, new HashMap<>());
        }
        List<String> scriptMismatches = new ArrayList<>();
        for (SwearWord swearWord : swearWords) {
            Map<TextNormalizationType, String> keywords = normalizeWord(textNormalizer, swearWord.word());
            if (keywords.isEmpty()) {
                continue;
            }
            SwearScript derivedScript = SwearScript.of(keywords.get(LIGHT));
            if (swearWord.script() != derivedScript) {
                scriptMismatches.add(swearWord.word() + "(" + swearWord.script() + " → " + derivedScript + ")");
                continue;
            }
            if (swearWord.script() != SwearScript.HANGUL) {
                keywords.remove(KEYSTROKE);
            }
            keywords.forEach((textNormalizationType, keyword) ->
                    keywordSwearWords.get(textNormalizationType)
                            .computeIfAbsent(keyword, ignored -> new ArrayList<>())
                            .add(swearWord));
        }
        if (!scriptMismatches.isEmpty()) {
            throw new ConfigurationException(
                    ConfigurationErrorCode.INCORRECT_SWEAR_SCRIPT, scriptMismatches.toArray(String[]::new));
        }

        Map<TextNormalizationType, List<String>> keywordNotSwearWords = new EnumMap<>(TextNormalizationType.class);
        for (TextNormalizationType textNormalizationType : TextNormalizationType.values()) {
            keywordNotSwearWords.put(textNormalizationType, new ArrayList<>());
        }
        for (String notSwearWord : notSwearWords) {
            normalizeWord(textNormalizer, notSwearWord)
                    .forEach((textNormalizationType, keyword) ->
                            keywordNotSwearWords.get(textNormalizationType).add(keyword));
        }

        Map<TextNormalizationType, KeywordTrie> swearKeywordTries = new EnumMap<>(TextNormalizationType.class);
        keywordSwearWords.forEach((textNormalizationType, keywordSwearWordsAsValue) ->
                swearKeywordTries.put(textNormalizationType, KeywordTrie.of(keywordSwearWordsAsValue.keySet())));
        Map<TextNormalizationType, Map<String, List<SwearWord>>> immutableKeywordSwearWords =
                keywordSwearWords.entrySet().stream()
                        .collect(Collectors.toUnmodifiableMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().entrySet().stream()
                                        .collect(Collectors.toUnmodifiableMap(
                                                Map.Entry::getKey,
                                                keywordEntry -> List.copyOf(keywordEntry.getValue())))));
        Map<TextNormalizationType, KeywordTrie> notSwearKeywordTries = new EnumMap<>(TextNormalizationType.class);
        keywordNotSwearWords.forEach((textNormalizationType, notSwearKeywordsAsValue) ->
                notSwearKeywordTries.put(textNormalizationType, KeywordTrie.of(notSwearKeywordsAsValue)));

        return new SwearEngine(
                textNormalizer,
                Collections.unmodifiableMap(swearKeywordTries),
                Collections.unmodifiableMap(notSwearKeywordTries),
                immutableKeywordSwearWords);
    }

    public List<SwearMatch> match(String text) {
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

        List<SwearMatch> keptMatches = new ArrayList<>();
        for (TextNormalizationType textNormalizationType : TextNormalizationType.values()) {
            TextNormalizedView view = views.get(textNormalizationType);
            List<KeywordMatch> notSwearKeywordMatches =
                    notSwearKeywordTries.get(textNormalizationType).findAll(view.getText());
            for (KeywordMatch keywordMatch : swearKeywordTries.get(textNormalizationType).findAll(view.getText())) {
                int keywordMatchStart = keywordMatch.start();
                int keywordMatchEnd = keywordMatch.end();
                if (isInsideNotSwearKeywordMatch(notSwearKeywordMatches, keywordMatchStart, keywordMatchEnd)) {
                    continue;
                }
                for (SwearWord swearWord :
                        keywordSwearWords.get(textNormalizationType).get(keywordMatch.keyword())) {
                    if (!isValidMatch(textNormalizationType, keywordMatch.keyword(), view,
                            lightlyNormalizedView, asciiLowerCaseRuns, keywordMatchStart, keywordMatchEnd)) {
                        continue;
                    }
                    SwearMatch swearMatch = new SwearMatch(swearWord, textNormalizationType,
                            view.getOriginalStart(keywordMatchStart), view.getOriginalEnd(keywordMatchEnd - 1));
                    if (!overlapsSameWordWithKeptMatch(keptMatches, swearMatch)) {
                        keptMatches.add(swearMatch);
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

    private static boolean isInsideNotSwearKeywordMatch(List<KeywordMatch> notSwearKeywordMatches, int start, int end) {
        for (KeywordMatch notSwearKeywordMatch : notSwearKeywordMatches) {
            if (notSwearKeywordMatch.start() <= start && end <= notSwearKeywordMatch.end()) {
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
        for (int lightlyNormalizedIndex = lightlyNormalizedStart; lightlyNormalizedIndex < lightlyNormalizedEnd; lightlyNormalizedIndex++) {
            if (!asciiLowerCaseRuns.isCoveredBy(lightlyNormalizedIndex, lightlyNormalizedStart, lightlyNormalizedEnd)) {
                return false;
            }
        }
        return true;
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

    // 시작 위치마다 트라이를 루트로 되돌리며, 이어지는 키워드가 없거나 텍스트가 끝나면 다음 시작 위치로 넘어감
    // findAll 호출마다 만든 CharsTrie 하나를 모든 시작 위치에서 재사용함
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
