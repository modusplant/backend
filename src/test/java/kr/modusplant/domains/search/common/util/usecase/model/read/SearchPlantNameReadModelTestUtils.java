package kr.modusplant.domains.search.common.util.usecase.model.read;

import kr.modusplant.domains.search.usecase.model.read.SearchPlantNameReadModel;

import java.util.List;

import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_1;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_PLANT_NAME;

public interface SearchPlantNameReadModelTestUtils {
    SearchPlantNameReadModel testSearchPlantNameReadModel =
            new SearchPlantNameReadModel(TEST_SEARCH_PLANT_NAME, TEST_SEARCH_KEYWORD_SIMILARITY_1);

    List<SearchPlantNameReadModel> testSearchPlantNameReadModelList =
            List.of(testSearchPlantNameReadModel);
}
