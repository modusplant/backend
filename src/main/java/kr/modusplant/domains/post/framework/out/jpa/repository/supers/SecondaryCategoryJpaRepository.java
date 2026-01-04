package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecondaryCategoryJpaRepository extends JpaRepository<CommSecondaryCategoryEntity, Integer> {
    List<CommSecondaryCategoryEntity> findByPrimaryCategoryEntityOrderByOrderAsc(CommPrimaryCategoryEntity primaryCategoryEntity);

    Optional<CommSecondaryCategoryEntity> findById(Integer id);
}
