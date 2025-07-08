package kr.modusplant.domains.communication.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface CommSecondaryCategoryRepository extends UuidPrimaryKeyRepository<CommSecondaryCategoryEntity>, CreatedAtRepository<CommSecondaryCategoryEntity>, JpaRepository<CommSecondaryCategoryEntity, UUID> {
    Optional<CommSecondaryCategoryEntity> findByOrder(Integer order);

    Optional<CommSecondaryCategoryEntity> findByCategory(String category);

    boolean existsByOrder(Integer order);

    boolean existsByCategory(String category);
}
