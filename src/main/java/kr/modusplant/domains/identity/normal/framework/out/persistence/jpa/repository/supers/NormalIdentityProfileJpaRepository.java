package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.framework.out.jpa.entity.SiteMemberProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NormalIdentityProfileJpaRepository extends JpaRepository<SiteMemberProfileEntity, UUID> {
}
