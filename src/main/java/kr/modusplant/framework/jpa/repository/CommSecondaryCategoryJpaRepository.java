package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtRepository;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface CommSecondaryCategoryJpaRepository extends UuidPrimaryKeyRepository<CommSecondaryCategoryEntity>, CreatedAtRepository<CommSecondaryCategoryEntity>, JpaRepository<CommSecondaryCategoryEntity, UUID> {
    Optional<CommSecondaryCategoryEntity> findByOrder(Integer order);

    Optional<CommSecondaryCategoryEntity> findByCategory(String category);

    boolean existsByOrder(Integer order);

    boolean existsByCategory(String category);
}
