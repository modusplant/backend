package kr.modusplant.api.crud.member.persistence.repository;

import kr.modusplant.api.crud.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.api.crud.member.persistence.repository.supers.SiteMemberUuidPrimaryKeyRepository;
import kr.modusplant.global.persistence.repository.supers.LastModifiedAtRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SiteMemberTermJpaRepository extends LastModifiedAtRepository<SiteMemberTermEntity>, SiteMemberUuidPrimaryKeyRepository<SiteMemberTermEntity>, JpaRepository<SiteMemberTermEntity, UUID> {
    List<SiteMemberTermEntity> findByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<SiteMemberTermEntity> findByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<SiteMemberTermEntity> findByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion);
}