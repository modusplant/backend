package kr.modusplant.domains.search.common.util.usecase.model.read;

import kr.modusplant.domains.search.usecase.model.read.SearchPlantKoreanNameReadModel;

import java.util.List;

import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_1;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_PLANT_KOREAN_NAME;

public interface SearchPlantKoreanNameReadModelTestUtils {
    SearchPlantKoreanNameReadModel testSearchPlantKoreanNameReadModel =
            new SearchPlantKoreanNameReadModel(TEST_SEARCH_PLANT_KOREAN_NAME, TEST_SEARCH_KEYWORD_SIMILARITY_1);

    List<SearchPlantKoreanNameReadModel> testSearchPlantKoreanNameReadModelList =
            List.of(testSearchPlantKoreanNameReadModel);
}
