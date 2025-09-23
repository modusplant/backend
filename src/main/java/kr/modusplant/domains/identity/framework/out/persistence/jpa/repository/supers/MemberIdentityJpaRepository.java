package kr.modusplant.domains.identity.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.identity.domain.vo.SignUpData;
import kr.modusplant.domains.identity.framework.out.persistence.jpa.entity.MemberIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MemberIdentityJpaRepository extends JpaRepository<MemberIdentityEntity, UUID> {

    @Modifying
    @Query(value = "INSERT INTO site_member (nickname) VALUES (:#{#sign.nickname})",
            nativeQuery = true)
    MemberIdentityEntity saveIdentity(@Param("sign") SignUpData sign);

}
