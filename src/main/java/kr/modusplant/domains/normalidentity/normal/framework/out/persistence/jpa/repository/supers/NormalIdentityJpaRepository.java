package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NormalIdentityJpaRepository extends JpaRepository<SiteMemberEntity, UUID> {
    boolean existsByNickname(String nickname);
}
