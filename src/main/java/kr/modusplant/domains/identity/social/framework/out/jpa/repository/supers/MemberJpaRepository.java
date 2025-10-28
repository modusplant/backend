package kr.modusplant.domains.identity.social.framework.out.jpa.repository.supers;

import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.shared.persistence.repository.supers.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
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
public interface MemberJpaRepository extends CreatedAtAndLastModifiedAtRepository<MemberEntity>, UuidPrimaryKeyRepository<MemberEntity>, JpaRepository<MemberEntity, UUID> {
    List<MemberEntity> findByBirthDate(LocalDate birthDate);

    List<MemberEntity> findByIsActive(Boolean isActive);

    List<MemberEntity> findByIsDisabledByLinking(Boolean isDisabledByLinking);

    List<MemberEntity> findByIsBanned(Boolean isBanned);

    List<MemberEntity> findByIsDeleted(Boolean isDeleted);

    List<MemberEntity> findByLoggedInAt(LocalDateTime loggedInAt);

    Optional<MemberEntity> findByNickname(String nickname);

    boolean existsByNickname(String nickname);
}