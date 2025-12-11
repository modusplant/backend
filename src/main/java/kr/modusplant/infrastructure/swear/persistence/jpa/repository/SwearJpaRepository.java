package kr.modusplant.infrastructure.swear.persistence.jpa.repository;

import kr.modusplant.infrastructure.swear.persistence.jpa.entity.SwearEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SwearJpaRepository extends JpaRepository<SwearEntity, UUID> {

}
