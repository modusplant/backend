package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrimaryCategoryJpaRepository extends JpaRepository<CommPrimaryCategoryEntity, Integer> {
    Optional<CommPrimaryCategoryEntity> findById(Integer id);

    List<CommPrimaryCategoryEntity> findAllByOrderByOrderAsc();

    boolean existsById(Integer id);
}
