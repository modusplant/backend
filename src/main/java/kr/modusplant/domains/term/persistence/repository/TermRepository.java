package kr.modusplant.domains.term.persistence.repository;

import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface TermRepository extends CreatedAtAndLastModifiedAtRepository<TermEntity>, UuidPrimaryKeyRepository<TermEntity>, JpaRepository<TermEntity, UUID> {
    List<TermEntity> findByVersion(String version);

    Optional<TermEntity> findByName(String name);
}