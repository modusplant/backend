<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/term/persistence/repository/TermCrudJpaRepository.java
package kr.modusplant.domains.term.persistence.repository;

import kr.modusplant.domains.term.persistence.entity.TermEntity;
========
package kr.modusplant.api.crud.term.persistence.repository;

import kr.modusplant.api.crud.term.persistence.entity.TermEntity;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/term/persistence/repository/TermJpaRepository.java
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