package kr.modusplant.domains.search.adapter.controller;

import kr.modusplant.domains.search.domain.vo.SearchKeyword;
import kr.modusplant.domains.search.domain.vo.SearchResultListSize;
import kr.modusplant.domains.search.usecase.model.read.SearchPlantNameReadModel;
import kr.modusplant.domains.search.usecase.port.cache.SearchPlantCache;
import kr.modusplant.domains.search.usecase.port.transliterator.SearchTransliterator;
import kr.modusplant.domains.search.usecase.record.SearchPlantNameRecord;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.SEARCH_SIZE_OUT_OF_RANGE;

@Service
@RequiredArgsConstructor
public class SearchPlantController {
    private final SearchPlantCache searchPlantCache;
    private final SearchTransliterator searchTransliterator;
    private final JaroWinklerSimilarity jaroWinklerSimilarity;

    public List<SearchPlantNameReadModel> searchPlantNameByKeyword(
            SearchPlantNameRecord record) {
        SearchKeyword keyword = SearchKeyword.create(
                searchTransliterator.separateKoreanIntoConsonantAndVowel(record.keyword()));
        String keywordValue = keyword.getValue();
        SearchResultListSize searchResultListSize = SearchResultListSize.create(record.size());
        int resultListSize = searchResultListSize.getValue();

        PriorityQueue<SearchPlantNameReadModel> similarityPriorityQueue = new PriorityQueue<>();
        for (String plantName : searchPlantCache.getTransliteratedPlantNames()) {
            double similarity = jaroWinklerSimilarity.apply(keywordValue, plantName);
            if (similarity >= 0.8) {
                if (similarityPriorityQueue.size() < resultListSize) {
                    similarityPriorityQueue.offer(
                            new SearchPlantNameReadModel(
                                    searchTransliterator.combineKoreanIntoConsonantAndVowel(plantName), similarity));
                } else if (similarityPriorityQueue.peek() == null) {
                    throw new InvalidValueException(SEARCH_SIZE_OUT_OF_RANGE, "searchSize");
                } else if (similarity > similarityPriorityQueue.peek().similarity()) {
                    similarityPriorityQueue.poll();
                    similarityPriorityQueue.offer(
                            new SearchPlantNameReadModel(
                                    searchTransliterator.combineKoreanIntoConsonantAndVowel(plantName), similarity));
                }
            }
        }

        int priorityQueueSize = similarityPriorityQueue.size();
        SearchPlantNameReadModel[] resultArray = new SearchPlantNameReadModel[priorityQueueSize];

        for (int i = priorityQueueSize - 1; i >= 0; i--) { // 배열의 끝부터 채워 넣음
            SearchPlantNameReadModel readModel = similarityPriorityQueue.poll();
            resultArray[i] = readModel != null ?
                    new SearchPlantNameReadModel(readModel.plantName(), readModel.similarity()) : null;
        }

        return Arrays.stream(resultArray).filter(Objects::nonNull).toList();
    }
}
