package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.entity.MemberTermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MemberTermJpaRepository extends JpaRepository<MemberTermEntity, UUID> {

    @Modifying
    @Query(value = "INSERT INTO site_member_term (uuid, agreed_tou_ver, " +
            "agreed_priv_poli_ver, agreed_ad_info_rece_ver) " +
            "VALUES (:#{#term.uuid}, :#{#term.agreedTermsOfUseVersion}, " +
            ":#{#term.agreedPrivacyPolicyVersion}, :#{#term.agreedAdInfoReceivingVersion})",
            nativeQuery = true)
    void saveTerm(@Param("term") MemberTermEntity term);
}
