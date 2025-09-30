package kr.modusplant.domains.member.framework.out.jpa.repository.supers;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.persistence.repository.supers.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<SiteMemberEntity, UUID>, CreatedAtAndLastModifiedAtRepository<SiteMemberEntity>, UuidPrimaryKeyRepository<SiteMemberEntity> {
    Optional<SiteMemberEntity> findByNickname(String nickname);

    boolean existsByNickname(String nickname);
}