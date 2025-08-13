package kr.modusplant.legacy.domains.member.persistence.repository;

import kr.modusplant.global.enums.Role;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.legacy.domains.member.persistence.repository.supers.SiteMemberUuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Primary
public interface SiteMemberRoleRepository extends SiteMemberUuidPrimaryKeyRepository<SiteMemberRoleEntity>, JpaRepository<SiteMemberRoleEntity, UUID> {
    List<SiteMemberRoleEntity> findByRole(Role role);
}