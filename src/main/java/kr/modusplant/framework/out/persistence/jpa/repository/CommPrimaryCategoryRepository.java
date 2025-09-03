package kr.modusplant.framework.out.persistence.jpa.repository;

import kr.modusplant.framework.out.persistence.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.supers.CreatedAtRepository;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface CommPrimaryCategoryRepository extends UuidPrimaryKeyRepository<CommPrimaryCategoryEntity>, CreatedAtRepository<CommPrimaryCategoryEntity>, JpaRepository<CommPrimaryCategoryEntity, UUID> {
    Optional<CommPrimaryCategoryEntity> findByOrder(Integer order);

    Optional<CommPrimaryCategoryEntity> findByCategory(String category);

    boolean existsByOrder(Integer order);

    boolean existsByCategory(String category);
}
