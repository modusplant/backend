package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrimaryCategoryJpaRepository extends UuidPrimaryKeyRepository<CommPrimaryCategoryEntity>, JpaRepository<CommPrimaryCategoryEntity, UUID> {
    List<CommPrimaryCategoryEntity> findAllByOrderByOrderAsc();
}
