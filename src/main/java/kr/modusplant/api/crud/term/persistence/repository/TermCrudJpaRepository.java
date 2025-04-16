package kr.modusplant.api.crud.term.persistence.repository;

import kr.modusplant.api.crud.term.persistence.entity.TermEntity;
import kr.modusplant.global.persistence.repository.supers.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.global.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TermCrudJpaRepository extends CreatedAtAndLastModifiedAtRepository<TermEntity>, UuidPrimaryKeyRepository<TermEntity>, JpaRepository<TermEntity, UUID> {
    List<TermEntity> findByVersion(String version);

    Optional<TermEntity> findByName(String name);
}