package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.domain.vo.SecondaryCategory;

import java.util.List;

public interface SecondaryCategoryRepository {
    List<SecondaryCategory> getSecondaryCategoriesByPrimaryCategory(PrimaryCategoryId primaryCategoryId);
}
