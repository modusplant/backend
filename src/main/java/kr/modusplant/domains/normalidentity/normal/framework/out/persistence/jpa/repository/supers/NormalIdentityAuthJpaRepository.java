package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NormalIdentityAuthJpaRepository extends JpaRepository<SiteMemberAuthEntity, UUID> {

    boolean existsByEmailAndProvider(String email, AuthProvider provider);
}
