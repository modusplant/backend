package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public interface CommPrimaryCategoryJpaRepository extends CreatedAtRepository<CommPrimaryCategoryEntity>, JpaRepository<CommPrimaryCategoryEntity, Integer> {
    Optional<CommPrimaryCategoryEntity> findById(Integer id);

    Optional<CommPrimaryCategoryEntity> findByOrder(Integer order);

    Optional<CommPrimaryCategoryEntity> findByCategory(String category);

    boolean existsById(Integer id);

    boolean existsByOrder(Integer order);

    boolean existsByCategory(String category);
}
