package kr.modusplant.domains.member.persistence.repository;

import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.supers.SiteMemberUuidPrimaryKeyRepository;
import kr.modusplant.global.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SiteMemberRoleRepository extends SiteMemberUuidPrimaryKeyRepository<SiteMemberRoleEntity>, JpaRepository<SiteMemberRoleEntity, UUID> {
    List<SiteMemberRoleEntity> findByRole(Role role);
}