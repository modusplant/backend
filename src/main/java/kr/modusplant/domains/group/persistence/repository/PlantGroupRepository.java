package kr.modusplant.domains.group.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtRepository;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantGroupRepository extends CreatedAtRepository<PlantGroupEntity>, JpaRepository<PlantGroupEntity, Integer> {
    Optional<PlantGroupEntity> findByOrder(Integer order);

    Optional<PlantGroupEntity> findByCategory(String category);
}
