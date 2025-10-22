package kr.modusplant.domains.identity.social.framework.out.jpa.repository.supers;

import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberRoleEntity;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface MemberRoleJpaRepository extends UuidPrimaryKeyRepository<MemberRoleEntity>, JpaRepository<MemberRoleEntity, UUID> {
    Optional<MemberRoleEntity> findByMember(MemberEntity member);

    List<MemberRoleEntity> findByRole(Role role);
}