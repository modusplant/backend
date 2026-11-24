package kr.modusplant.domains.record.framework.outbound.jpa.repository;

import kr.modusplant.domains.record.framework.outbound.jpa.entity.PlantRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantRecordJpaRepository extends JpaRepository<PlantRecordEntity, String> {

    Optional<PlantRecordEntity> findByUlid(String ulid);

    boolean existsByUlid(String ulid);
}
