package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.domain.vo.SecondaryCategory;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.SecondaryCategoryJpaMapper;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.SecondaryCategoryJpaRepository;
import kr.modusplant.domains.post.usecase.port.repository.SecondaryCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SecondaryCategoryRepositoryJpaAdapter implements SecondaryCategoryRepository {
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryJpaRepository;
    private final SecondaryCategoryJpaMapper secondaryCategoryJpaMapper;

    @Override
    public List<SecondaryCategory> getSecondaryCategoriesByPrimaryCategory(PrimaryCategoryId primaryCategoryId) {
        return secondaryCategoryJpaRepository.findByPrimaryCategoryEntityOrderByOrderAsc(
                primaryCategoryJpaRepository.findByUuid(primaryCategoryId.getValue()).orElseThrow()
        ).stream().map(secondaryCategoryJpaMapper::toSecondaryCategory).toList();
    }
}
