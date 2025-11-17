package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.shared.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NormalIdentityAuthJpaRepository extends JpaRepository<SiteMemberAuthEntity, UUID> {

    boolean existsByEmailAndProvider(String email, AuthProvider provider);
}
