package kr.modusplant.domains.search.usecase.record;

import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SearchPostRecord(
        String keyword,
        SearchPostTarget searchPostTarget,
        SearchPostSortCondition searchPostSortCondition,
        Integer primaryCategoryId,
        List<Integer> secondaryCategoryIds,
        String lastPostUlid,
        LocalDateTime lastPostPublishedAt,
        Integer lastPostImportance,
        Double lastPostSimilarity,
        Integer size,
        UUID memberId) {
}
