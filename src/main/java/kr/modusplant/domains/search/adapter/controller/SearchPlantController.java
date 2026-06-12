package kr.modusplant.domains.search.adapter.controller;

import kr.modusplant.domains.search.domain.vo.SearchKeyword;
import kr.modusplant.domains.search.usecase.model.read.SearchPlantKoreanNameReadModel;
import kr.modusplant.domains.search.usecase.port.cache.SearchPlantCache;
import kr.modusplant.domains.search.usecase.port.transliterator.SearchTransliterator;
import kr.modusplant.domains.search.usecase.record.SearchPlantKoreanNameRecord;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.SEARCH_SIZE_OUT_OF_RANGE;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchPlantController {
    private final SearchPlantCache searchPlantCache;
    private final SearchTransliterator searchTransliterator;
    private final JaroWinklerSimilarity jaroWinklerSimilarity;

    @Transactional(readOnly = true)
    public List<SearchPlantKoreanNameReadModel> searchKoreanNameByKeyword(
            SearchPlantKoreanNameRecord record) {
        SearchKeyword keyword = SearchKeyword.create(
                searchTransliterator.separateKoreanIntoConsonantAndVowel(record.keyword()));
        String keywordValue = keyword.getValue();
        Integer maxSize = record.size();

        PriorityQueue<SearchPlantKoreanNameReadModel> similarityPriorityQueue = new PriorityQueue<>();
        for (String koreanName : searchPlantCache.getTransliteratedKoreanNames()) {
            double similarity = jaroWinklerSimilarity.apply(keywordValue, koreanName);
            if (similarity >= 0.8) {
                if (similarityPriorityQueue.size() < maxSize) {
                    similarityPriorityQueue.offer(
                            new SearchPlantKoreanNameReadModel(
                                    searchTransliterator.combineKoreanIntoConsonantAndVowel(koreanName), similarity));
                } else if (similarityPriorityQueue.peek() == null) {
                    throw new InvalidValueException(SEARCH_SIZE_OUT_OF_RANGE, "maxSize");
                } else if (similarity > similarityPriorityQueue.peek().similarity()) {
                    similarityPriorityQueue.poll();
                    similarityPriorityQueue.offer(
                            new SearchPlantKoreanNameReadModel(
                                    searchTransliterator.combineKoreanIntoConsonantAndVowel(koreanName), similarity));
                }
            }
        }

        int priorityQueueSize = similarityPriorityQueue.size();
        SearchPlantKoreanNameReadModel[] resultArray = new SearchPlantKoreanNameReadModel[priorityQueueSize];

        for (int i = priorityQueueSize - 1; i >= 0; i--) { // 배열의 끝부터 채워 넣음
            SearchPlantKoreanNameReadModel readModel = similarityPriorityQueue.poll();
            resultArray[i] = readModel != null ?
                    new SearchPlantKoreanNameReadModel(readModel.koreanName(), readModel.similarity()) : null;
        }

        return Arrays.stream(resultArray).filter(Objects::nonNull).toList();
    }
}
