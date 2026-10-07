package kr.modusplant.domains.search.adapter.controller;

import kr.modusplant.domains.search.common.constant.SearchStringConstant;
import kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode;
import kr.modusplant.domains.search.usecase.model.read.SearchPlantNameReadModel;
import kr.modusplant.domains.search.usecase.port.cache.SearchPlantCache;
import kr.modusplant.domains.search.usecase.record.SearchPlantNameRecord;
import kr.modusplant.infrastructure.transliterate.UnicodeTransliterator;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.*;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.*;
import static kr.modusplant.domains.search.common.util.usecase.record.SearchPlantNameRecordTestUtils.testSearchPlantNameRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class SearchPlantControllerTest {

    private final SearchPlantCache searchPlantCache = Mockito.mock(SearchPlantCache.class);
    private final UnicodeTransliterator unicodeTransliterator = Mockito.mock(UnicodeTransliterator.class);
    private final JaroWinklerSimilarity jaroWinklerSimilarity = Mockito.mock(JaroWinklerSimilarity.class);
    private final SearchPlantController searchPlantController =
            new SearchPlantController(searchPlantCache, unicodeTransliterator, jaroWinklerSimilarity);

    @Test
    @DisplayName("유효한 키워드로 searchPlantNameByKeyword 호출 시 식물명 목록 반환")
    void testSearchPlantNameByKeyword_givenMatchingKeyword_willReturnReadModelList() {
        // given
        given(unicodeTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedPlantNames()).willReturn(List.of(TEST_SEARCH_PLANT_NFD_NAME));
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        given(unicodeTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_NAME);

        // when
        List<SearchPlantNameReadModel> result =
                searchPlantController.searchPlantNameByKeyword(testSearchPlantNameRecord);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().plantName()).isEqualTo(TEST_SEARCH_PLANT_NAME);
        assertThat(result.getFirst().similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_1);

        verify(unicodeTransliterator).separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD);
        verify(unicodeTransliterator).combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME);
    }

    @Test
    @DisplayName("비어있는 캐시로 searchPlantNameByKeyword 호출 시 빈 목록 반환")
    void testSearchPlantNameByKeyword_givenEmptyCache_willReturnEmptyList() {
        // given
        given(unicodeTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedPlantNames()).willReturn(Collections.emptyList());

        // when
        List<SearchPlantNameReadModel> result =
                searchPlantController.searchPlantNameByKeyword(testSearchPlantNameRecord);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("2개의 식물명과 size=1로 searchPlantNameByKeyword 호출 시 가장 유사한 식물명 1개 반환")
    void testSearchPlantNameByKeyword_givenMultipleNamesAndSizeOne_willReturnTopMatch() {
        // given
        SearchPlantNameRecord record = new SearchPlantNameRecord(TEST_SEARCH_KEYWORD, 1);
        given(unicodeTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedPlantNames()).willReturn(List.of(TEST_SEARCH_PLANT_OTHER_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME));
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_0_8);
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        given(unicodeTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_OTHER_NAME);
        given(unicodeTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_NAME);

        // when
        List<SearchPlantNameReadModel> result = searchPlantController.searchPlantNameByKeyword(record);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().plantName()).isEqualTo(TEST_SEARCH_PLANT_NAME);
        assertThat(result.getFirst().similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_1);
    }

    @Test
    @DisplayName("2개의 식물명과 size=2로 searchPlantNameByKeyword 호출 시 유사도 내림차순 정렬된 목록 반환")
    void testSearchPlantNameByKeyword_givenMultipleNamesAndSizeTwo_willReturnDescendingOrderList() {
        // given
        SearchPlantNameRecord record = new SearchPlantNameRecord(TEST_SEARCH_KEYWORD, 2);
        given(unicodeTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedPlantNames()).willReturn(List.of(TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME));
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_0_8);
        given(unicodeTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_NAME);
        given(unicodeTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_OTHER_NAME);

        // when
        List<SearchPlantNameReadModel> result = searchPlantController.searchPlantNameByKeyword(record);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        assertThat(result.get(1).similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_0_8);
    }

    @Test
    @DisplayName("searchPlantNameByKeyword 호출 시 유사도가 0.8보다 작은 결과는 무시하도록 목록 반환")
    void testSearchPlantNameByKeyword_givenSimilarityLowerThan0_8_willReturnListIgnoringThatResult() {
        // given
        SearchPlantNameRecord record = new SearchPlantNameRecord(TEST_SEARCH_KEYWORD, 2);
        given(unicodeTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedPlantNames()).willReturn(List.of(TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME));
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_0_6);
        given(unicodeTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_NAME);

        // when
        List<SearchPlantNameReadModel> result = searchPlantController.searchPlantNameByKeyword(record);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_1);
    }

    @Test
    @DisplayName("음절 분리 결과가 공백인 키워드로 searchPlantNameByKeyword 호출 시 예외 발생")
    void testSearchPlantNameByKeyword_givenBlankTransliteratedKeyword_willThrowException() {
        // given
        given(unicodeTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn("   ");

        // when
        EmptyValueException exception = assertThrows(EmptyValueException.class,
                () -> searchPlantController.searchPlantNameByKeyword(testSearchPlantNameRecord));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.EMPTY_SEARCH_KEYWORD);
    }

    @Test
    @DisplayName("size가 null인 레코드로 searchPlantNameByKeyword 호출 시 예외 발생")
    void testSearchPlantNameByKeyword_givenNullSize_willThrowException() {
        // given
        SearchPlantNameRecord record = new SearchPlantNameRecord(TEST_SEARCH_KEYWORD, null);
        given(unicodeTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);

        // when
        EmptyValueException exception = assertThrows(EmptyValueException.class,
                () -> searchPlantController.searchPlantNameByKeyword(record));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.EMPTY_SEARCH_RESULT_LIST_SIZE);
    }

    @Test
    @DisplayName("허용 범위를 벗어난 size로 searchPlantNameByKeyword 호출 시 예외 발생")
    void testSearchPlantNameByKeyword_givenSizeOutOfRange_willThrowException() {
        // given
        SearchPlantNameRecord record = new SearchPlantNameRecord(TEST_SEARCH_KEYWORD, 51);
        given(unicodeTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> searchPlantController.searchPlantNameByKeyword(record));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.SEARCH_RESULT_LIST_SIZE_OUT_OF_RANGE);
    }
}
