package kr.modusplant.domains.search.adapter.controller;

import kr.modusplant.domains.search.usecase.port.cache.SearchPlantCache;
import kr.modusplant.domains.search.usecase.record.SearchPlantKoreanNameRecord;
import kr.modusplant.domains.search.usecase.response.SearchPostRelevanceSortedPageResponse;
import kr.modusplant.domains.search.usecase.response.SearchPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchPlantController {
    private final SearchPlantCache searchPlantCache;

//    @Transactional(readOnly = true)
//    public SearchPostRelevanceSortedPageResponse<SearchPostResponse> searchKoreanNameByKeyword(
//            SearchPlantKoreanNameRecord record) {
//        List<String> koreanNames = searchPlantCache.getKoreanNames();
//
//    }
}
