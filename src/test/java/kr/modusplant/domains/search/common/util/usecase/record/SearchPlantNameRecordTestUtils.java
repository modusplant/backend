package kr.modusplant.domains.search.common.util.usecase.record;

import kr.modusplant.domains.search.usecase.record.SearchPlantNameRecord;

import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_PLANT_SIZE;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_KEYWORD;

public interface SearchPlantNameRecordTestUtils {
    SearchPlantNameRecord testSearchPlantNameRecord =
            new SearchPlantNameRecord(TEST_SEARCH_KEYWORD, TEST_SEARCH_PLANT_SIZE);
}
