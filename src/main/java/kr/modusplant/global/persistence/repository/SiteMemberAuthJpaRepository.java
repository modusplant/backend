package kr.modusplant.global.persistence.repository;

import kr.modusplant.global.enums.AuthProvider;
import kr.modusplant.global.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.supers.LastModifiedAtRepository;
import kr.modusplant.global.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SiteMemberAuthJpaRepository extends LastModifiedAtRepository<SiteMemberAuthEntity>, UuidPrimaryKeyRepository<SiteMemberAuthEntity>, JpaRepository<SiteMemberAuthEntity, UUID> {
    List<SiteMemberAuthEntity> findByActiveMember(SiteMemberEntity activeMember);

    List<SiteMemberAuthEntity> findByEmail(String email);

    List<SiteMemberAuthEntity> findByPw(String pw);

    List<SiteMemberAuthEntity> findByProvider(AuthProvider provider);

    List<SiteMemberAuthEntity> findByProviderId(String providerId);

    List<SiteMemberAuthEntity> findByProviderAndProviderId(AuthProvider provider, String providerId);

    List<SiteMemberAuthEntity> findByFailedAttempt(Integer failedAttempt);

    Optional<SiteMemberAuthEntity> findByOriginalMember(SiteMemberEntity originalMember);
}