package kr.modusplant.domains.member.persistence.repository;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Primary
public interface SiteMemberRepository extends CreatedAtAndLastModifiedAtRepository<SiteMemberEntity>, UuidPrimaryKeyRepository<SiteMemberEntity>, JpaRepository<SiteMemberEntity, UUID> {
    List<SiteMemberEntity> findByNickname(String nickname);

    List<SiteMemberEntity> findByBirthDate(LocalDate birthDate);

    List<SiteMemberEntity> findByIsActive(Boolean isActive);

    List<SiteMemberEntity> findByIsDisabledByLinking(Boolean isDisabledByLinking);

    List<SiteMemberEntity> findByIsBanned(Boolean isBanned);

    List<SiteMemberEntity> findByIsDeleted(Boolean isDeleted);

    List<SiteMemberEntity> findByLoggedInAt(LocalDateTime loggedInAt);
}