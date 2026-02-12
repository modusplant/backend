package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface SiteMemberJpaRepository extends CreatedAtAndLastModifiedAtRepository<SiteMemberEntity>, UuidPrimaryKeyRepository<SiteMemberEntity>, JpaRepository<SiteMemberEntity, UUID> {
    List<SiteMemberEntity> findByBirthDate(LocalDate birthDate);

    List<SiteMemberEntity> findByIsActive(Boolean isActive);

    List<SiteMemberEntity> findByIsBanned(Boolean isBanned);

    List<SiteMemberEntity> findByIsDeleted(Boolean isDeleted);

    List<SiteMemberEntity> findByLoggedInAt(LocalDateTime loggedInAt);

    Optional<SiteMemberEntity> findByNickname(String nickname);

    boolean existsByNickname(String nickname);
}