package kr.modusplant.domains.post.adapter.controller;

import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.usecase.port.mapper.CategoryMapper;
import kr.modusplant.domains.post.usecase.port.repository.PrimaryCategoryRepository;
import kr.modusplant.domains.post.usecase.port.repository.SecondaryCategoryRepository;
import kr.modusplant.domains.post.usecase.response.PrimaryCategoryResponse;
import kr.modusplant.domains.post.usecase.response.SecondaryCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryController {
    private final PrimaryCategoryRepository primaryCategoryRepository;
    private final SecondaryCategoryRepository secondaryCategoryRepository;
    private final CategoryMapper categoryMapper;

    public List<PrimaryCategoryResponse> getPrimaryCategories() {
        return primaryCategoryRepository.getPrimaryCategories().stream()
                .map(categoryMapper::toPrimaryCategoryResponse)
                .toList();
    }

    public List<SecondaryCategoryResponse> getSecondaryCategoriesByPrimaryCategory(UUID primaryCategoryUuid) {
        return secondaryCategoryRepository.getSecondaryCategoriesByPrimaryCategory(PrimaryCategoryId.fromUuid(primaryCategoryUuid)).stream()
                .map(categoryMapper::toSecondaryCategoryResponse)
                .toList();
    }
}
