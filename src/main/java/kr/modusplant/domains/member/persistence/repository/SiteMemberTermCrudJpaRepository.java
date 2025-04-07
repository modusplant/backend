<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/persistence/repository/SiteMemberTermCrudJpaRepository.java
package kr.modusplant.domains.member.persistence.repository;

import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.domains.member.persistence.repository.supers.SiteMemberUuidPrimaryKeyRepository;
========
package kr.modusplant.api.crud.member.persistence.repository;

import kr.modusplant.api.crud.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.api.crud.member.persistence.repository.supers.SiteMemberUuidPrimaryKeyRepository;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/member/persistence/repository/SiteMemberTermJpaRepository.java
import kr.modusplant.global.persistence.repository.supers.LastModifiedAtRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SiteMemberTermCrudJpaRepository extends LastModifiedAtRepository<SiteMemberTermEntity>, SiteMemberUuidPrimaryKeyRepository<SiteMemberTermEntity>, JpaRepository<SiteMemberTermEntity, UUID> {
    List<SiteMemberTermEntity> findByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<SiteMemberTermEntity> findByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<SiteMemberTermEntity> findByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion);
}