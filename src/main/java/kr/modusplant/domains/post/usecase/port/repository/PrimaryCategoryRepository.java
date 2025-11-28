package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.PrimaryCategory;

import java.util.List;

public interface PrimaryCategoryRepository {
    List<PrimaryCategory> getPrimaryCategories();
}
