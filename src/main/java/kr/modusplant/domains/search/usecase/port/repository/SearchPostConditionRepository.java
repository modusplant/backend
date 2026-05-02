package kr.modusplant.domains.search.usecase.port.repository;

import java.util.List;

public interface SearchPostConditionRepository {
    boolean isIdExist(Integer primaryCategoryId);

    boolean isIdsExist(Integer primaryCategoryId, List<Integer> secondaryCategoryIds);
}
