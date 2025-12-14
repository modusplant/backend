package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NormalIdentityProfileJpaRepository extends JpaRepository<SiteMemberProfileEntity, UUID> {
}
