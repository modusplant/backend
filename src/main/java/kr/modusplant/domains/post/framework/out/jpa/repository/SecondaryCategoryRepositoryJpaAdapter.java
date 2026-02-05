package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
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
                primaryCategoryJpaRepository.findById(primaryCategoryId.getValue()).orElseThrow(() -> new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ID))
        ).stream().map(secondaryCategoryJpaMapper::toSecondaryCategory).toList();
    }
}
