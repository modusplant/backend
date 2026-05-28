package kr.modusplant.domains.post.framework.outbound.jpa.repository;

import kr.modusplant.domains.post.domain.vo.PrimaryCategory;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.supers.PrimaryCategoryJpaMapper;
import kr.modusplant.domains.post.usecase.port.repository.PrimaryCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PrimaryCategoryRepositoryJpaAdapter implements PrimaryCategoryRepository {
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final PrimaryCategoryJpaMapper primaryCategoryJpaMapper;

    @Override
    public List<PrimaryCategory> getPrimaryCategories() {
        return primaryCategoryJpaRepository.findAllByOrderByOrderAsc().stream()
                .map(primaryCategoryJpaMapper::toPrimaryCategory)
                .toList();
    }
}
