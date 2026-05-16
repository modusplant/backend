package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public interface PrimaryCategoryJpaRepository extends
        CreatedAtRepository<PrimaryCategoryEntity>, JpaRepository<PrimaryCategoryEntity, Integer> {
    Optional<PrimaryCategoryEntity> findByOrder(Integer order);

    Optional<PrimaryCategoryEntity> findByCategory(String category);

    List<PrimaryCategoryEntity> findAllByOrderByOrderAsc();
}
