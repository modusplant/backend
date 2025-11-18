package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtRepository;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface CommPrimaryCategoryJpaRepository extends UuidPrimaryKeyRepository<CommPrimaryCategoryEntity>, CreatedAtRepository<CommPrimaryCategoryEntity>, JpaRepository<CommPrimaryCategoryEntity, UUID> {
    Optional<CommPrimaryCategoryEntity> findByOrder(Integer order);

    Optional<CommPrimaryCategoryEntity> findByCategory(String category);

    boolean existsByOrder(Integer order);

    boolean existsByCategory(String category);
}
