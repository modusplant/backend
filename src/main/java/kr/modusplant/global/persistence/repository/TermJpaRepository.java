package kr.modusplant.global.persistence.repository;

import kr.modusplant.global.persistence.entity.TermEntity;
import kr.modusplant.global.persistence.repository.supers.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.global.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TermJpaRepository extends CreatedAtAndLastModifiedAtRepository<TermEntity>, UuidPrimaryKeyRepository<TermEntity>, JpaRepository<TermEntity, UUID> {
    Optional<TermEntity> findByName(String name);

    Optional<TermEntity> findByVersion(String version);
}