package kr.modusplant.domains.search.adapter.controller;

import kr.modusplant.domains.search.common.constant.SearchStringConstant;
import kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode;
import kr.modusplant.domains.search.usecase.model.read.SearchPlantKoreanNameReadModel;
import kr.modusplant.domains.search.usecase.port.cache.SearchPlantCache;
import kr.modusplant.domains.search.usecase.port.transliterator.SearchTransliterator;
import kr.modusplant.domains.search.usecase.record.SearchPlantKoreanNameRecord;
import kr.modusplant.shared.exception.EmptyValueException;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.*;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.*;
import static kr.modusplant.domains.search.common.util.usecase.record.SearchPlantKoreanNameRecordTestUtils.testSearchPlantKoreanNameRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class SearchPlantControllerTest {

    private final SearchPlantCache searchPlantCache = Mockito.mock(SearchPlantCache.class);
    private final SearchTransliterator searchTransliterator = Mockito.mock(SearchTransliterator.class);
    private final JaroWinklerSimilarity jaroWinklerSimilarity = Mockito.mock(JaroWinklerSimilarity.class);
    private final SearchPlantController searchPlantController =
            new SearchPlantController(searchPlantCache, searchTransliterator, jaroWinklerSimilarity);

    @Test
    @DisplayName("유효한 키워드로 searchKoreanNameByKeyword 호출 시 국명 목록 반환")
    void testSearchKoreanNameByKeyword_givenMatchingKeyword_willReturnReadModelList() {
        // given
        given(searchTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedKoreanNames()).willReturn(List.of(TEST_SEARCH_PLANT_NFD_NAME));
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        given(searchTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_KOREAN_NAME);

        // when
        List<SearchPlantKoreanNameReadModel> result =
                searchPlantController.searchKoreanNameByKeyword(testSearchPlantKoreanNameRecord);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().koreanName()).isEqualTo(TEST_SEARCH_PLANT_KOREAN_NAME);
        assertThat(result.getFirst().similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_1);

        verify(searchTransliterator).separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD);
        verify(searchTransliterator).combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME);
    }

    @Test
    @DisplayName("비어있는 캐시로 searchKoreanNameByKeyword 호출 시 빈 목록 반환")
    void testSearchKoreanNameByKeyword_givenEmptyCache_willReturnEmptyList() {
        // given
        given(searchTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedKoreanNames()).willReturn(Collections.emptyList());

        // when
        List<SearchPlantKoreanNameReadModel> result =
                searchPlantController.searchKoreanNameByKeyword(testSearchPlantKoreanNameRecord);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("2개의 국명과 size=1로 searchKoreanNameByKeyword 호출 시 가장 유사한 국명 1개 반환")
    void testSearchKoreanNameByKeyword_givenMultipleNamesAndSizeOne_willReturnTopMatch() {
        // given
        SearchPlantKoreanNameRecord record = new SearchPlantKoreanNameRecord(TEST_SEARCH_KEYWORD, 1);
        given(searchTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedKoreanNames()).willReturn(List.of(TEST_SEARCH_PLANT_OTHER_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME));
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_0_8);
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        given(searchTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_OTHER_KOREAN_NAME);
        given(searchTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_KOREAN_NAME);

        // when
        List<SearchPlantKoreanNameReadModel> result = searchPlantController.searchKoreanNameByKeyword(record);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().koreanName()).isEqualTo(TEST_SEARCH_PLANT_KOREAN_NAME);
        assertThat(result.getFirst().similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_1);
    }

    @Test
    @DisplayName("2개의 국명과 size=2로 searchKoreanNameByKeyword 호출 시 유사도 내림차순 정렬된 목록 반환")
    void testSearchKoreanNameByKeyword_givenMultipleNamesAndSizeTwo_willReturnDescendingOrderList() {
        // given
        SearchPlantKoreanNameRecord record = new SearchPlantKoreanNameRecord(TEST_SEARCH_KEYWORD, 2);
        given(searchTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedKoreanNames()).willReturn(List.of(TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME));
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_0_8);
        given(searchTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_KOREAN_NAME);
        given(searchTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_OTHER_KOREAN_NAME);

        // when
        List<SearchPlantKoreanNameReadModel> result = searchPlantController.searchKoreanNameByKeyword(record);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        assertThat(result.get(1).similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_0_8);
    }

    @Test
    @DisplayName("searchKoreanNameByKeyword 호출 시 유사도가 0.8보다 작은 결과는 무시하도록 목록 반환")
    void testSearchKoreanNameByKeyword_givenSimilarityLowerThan0_8_willReturnListIgnoringThatResult() {
        // given
        SearchPlantKoreanNameRecord record = new SearchPlantKoreanNameRecord(TEST_SEARCH_KEYWORD, 2);
        given(searchTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME);
        given(searchPlantCache.getTransliteratedKoreanNames()).willReturn(List.of(TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME));
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_1);
        given(jaroWinklerSimilarity.apply(SearchStringConstant.TEST_SEARCH_PLANT_NFD_NAME, TEST_SEARCH_PLANT_OTHER_NFD_NAME)).willReturn(TEST_SEARCH_KEYWORD_SIMILARITY_0_6);
        given(searchTransliterator.combineKoreanIntoConsonantAndVowel(TEST_SEARCH_PLANT_NFD_NAME)).willReturn(TEST_SEARCH_PLANT_KOREAN_NAME);

        // when
        List<SearchPlantKoreanNameReadModel> result = searchPlantController.searchKoreanNameByKeyword(record);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().similarity()).isEqualTo(TEST_SEARCH_KEYWORD_SIMILARITY_1);
    }

    @Test
    @DisplayName("음절 분리 결과가 공백인 키워드로 searchKoreanNameByKeyword 호출 시 예외 발생")
    void testSearchKoreanNameByKeyword_givenBlankTransliteratedKeyword_willThrowException() {
        // given
        given(searchTransliterator.separateKoreanIntoConsonantAndVowel(TEST_SEARCH_KEYWORD)).willReturn("   ");

        // when
        EmptyValueException exception = assertThrows(EmptyValueException.class,
                () -> searchPlantController.searchKoreanNameByKeyword(testSearchPlantKoreanNameRecord));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.EMPTY_SEARCH_KEYWORD);
    }
}
