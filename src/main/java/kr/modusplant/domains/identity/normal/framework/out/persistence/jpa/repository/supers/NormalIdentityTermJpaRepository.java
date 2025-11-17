package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NormalIdentityTermJpaRepository extends JpaRepository<SiteMemberTermEntity, UUID> {
}
