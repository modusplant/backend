package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.MemberEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface MemberJpaRepository extends
        CreatedAtAndLastModifiedAtRepository<MemberEntity>,
        UuidPrimaryKeyRepository<MemberEntity>,
        JpaRepository<MemberEntity, UUID> {
    List<MemberEntity> findByIsActive(Boolean isActive);

    List<MemberEntity> findByIsBanned(Boolean isBanned);

    List<MemberEntity> findByLoggedInAt(LocalDateTime loggedInAt);

    Optional<MemberEntity> findByNickname(String nickname);

    boolean existsByNickname(String nickname);
}