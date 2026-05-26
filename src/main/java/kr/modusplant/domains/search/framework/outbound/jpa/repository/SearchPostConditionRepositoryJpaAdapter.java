package kr.modusplant.domains.search.framework.outbound.jpa.repository;

import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.SecondaryCategoryJpaRepository;
import kr.modusplant.domains.search.usecase.port.repository.SearchPostConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SearchPostConditionRepositoryJpaAdapter implements SearchPostConditionRepository {
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryJpaRepository;

    @Override
    public boolean isIdExist(Integer primaryCategoryId) {
        return primaryCategoryJpaRepository.existsById(primaryCategoryId);
    }

    @Override
    public boolean isIdsExist(Integer primaryCategoryId, List<Integer> secondaryCategoryIds) {
        Set<Integer> uniqueIds = new HashSet<>(secondaryCategoryIds);
        Set<Integer> foundIds = secondaryCategoryJpaRepository.findByPrimaryCategoryOrderByOrderAsc(
                        primaryCategoryJpaRepository.findById(primaryCategoryId).orElseThrow())
                .stream()
                .map(SecondaryCategoryEntity::getId).collect(Collectors.toUnmodifiableSet());
        return foundIds.containsAll(uniqueIds);
    }
}
