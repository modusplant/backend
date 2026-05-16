package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public interface SecondaryCategoryJpaRepository extends
        CreatedAtRepository<SecondaryCategoryEntity>,
        JpaRepository<SecondaryCategoryEntity, Integer> {
    Optional<SecondaryCategoryEntity> findByOrder(Integer order);

    Optional<SecondaryCategoryEntity> findByCategory(String category);

    List<SecondaryCategoryEntity> findByPrimaryCategoryOrderByOrderAsc(PrimaryCategoryEntity primaryCategory);

    boolean existsById(Integer id);

    boolean existsByOrder(Integer order);

    boolean existsByCategory(String category);
}
