package kr.modusplant.domains.identity.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.identity.framework.out.persistence.jpa.entity.MemberAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MemberAuthJpaRepository extends JpaRepository<MemberAuthEntity, UUID> {

    @Modifying
    @Query(value = "INSERT INTO site_member_auth " +
            "(uuid, act_memb_uuid, email, password, provider) " +
            "VALUES (:#{#auth.originalMemberUuid}, :#{#auth.activeMemberUuid}, :#{#auth.email}, " +
            ":#{#auth.password}, :#{#auth.provider})",
            nativeQuery = true)
    void saveAuth(@Param("auth") MemberAuthEntity auth);
}
