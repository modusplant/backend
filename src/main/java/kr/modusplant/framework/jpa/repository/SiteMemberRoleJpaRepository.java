package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.jpa.repository.supers.SiteMemberUuidPrimaryKeyJpaRepository;
import kr.modusplant.infrastructure.security.enums.Role;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface SiteMemberRoleJpaRepository extends SiteMemberUuidPrimaryKeyJpaRepository<SiteMemberRoleEntity>, JpaRepository<SiteMemberRoleEntity, UUID> {
    List<SiteMemberRoleEntity> findByRole(Role role);

    Optional<SiteMemberRoleEntity> findByMember(SiteMemberEntity member);
}