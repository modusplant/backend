package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.jpa.repository.supers.SiteMemberUuidPrimaryKeyJpaRepository;
import kr.modusplant.shared.persistence.repository.LastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Primary
public interface SiteMemberTermJpaRepository extends LastModifiedAtRepository<SiteMemberTermEntity>, SiteMemberUuidPrimaryKeyJpaRepository<SiteMemberTermEntity>, JpaRepository<SiteMemberTermEntity, UUID> {
    List<SiteMemberTermEntity> findByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<SiteMemberTermEntity> findByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<SiteMemberTermEntity> findByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion);
}