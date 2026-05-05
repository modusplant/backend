package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public interface CommSecondaryCategoryJpaRepository extends
        CreatedAtRepository<CommSecondaryCategoryEntity>,
        JpaRepository<CommSecondaryCategoryEntity, Integer> {
    Optional<CommSecondaryCategoryEntity> findByOrder(Integer order);

    Optional<CommSecondaryCategoryEntity> findByCategory(String category);

    List<CommSecondaryCategoryEntity> findByPrimaryCategoryOrderByOrderAsc(CommPrimaryCategoryEntity primaryCategory);

    boolean existsById(Integer id);

    boolean existsByOrder(Integer order);

    boolean existsByCategory(String category);
}
