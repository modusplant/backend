package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtRepository;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public interface TipCategoryRepository extends CreatedAtRepository<TipCategoryEntity>, JpaRepository<TipCategoryEntity, Integer> {
    Optional<TipCategoryEntity> findByOrder(Integer order);

    Optional<TipCategoryEntity> findByCategory(String category);

    void deleteByOrder(Integer order);

    boolean existsByOrder(Integer order);
}