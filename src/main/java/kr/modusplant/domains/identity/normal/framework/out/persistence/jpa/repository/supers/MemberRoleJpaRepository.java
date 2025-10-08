package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.entity.MemberRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MemberRoleJpaRepository extends JpaRepository<MemberRoleEntity, UUID> {

    @Modifying
    @Query(value = "INSERT INTO site_member_role " +
            "(uuid, role) " +
            "VALUES (:#{#roleEntity.uuid}, :#{#roleEntity.role})",
            nativeQuery = true)
    void saveRole(@Param("role") MemberRoleEntity roleEntity);
}
