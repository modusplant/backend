package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NormalIdentityRoleJpaRepository extends JpaRepository<SiteMemberRoleEntity, UUID> {
}
