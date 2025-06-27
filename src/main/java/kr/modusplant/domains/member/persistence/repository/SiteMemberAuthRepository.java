package kr.modusplant.domains.member.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.LastModifiedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface SiteMemberAuthRepository extends LastModifiedAtRepository<SiteMemberAuthEntity>, UuidPrimaryKeyRepository<SiteMemberAuthEntity>, JpaRepository<SiteMemberAuthEntity, UUID> {
    List<SiteMemberAuthEntity> findByActiveMember(SiteMemberEntity activeMember);

    List<SiteMemberAuthEntity> findByEmail(String email);

    List<SiteMemberAuthEntity> findByProvider(AuthProvider provider);

    List<SiteMemberAuthEntity> findByProviderId(String providerId);

    Optional<SiteMemberAuthEntity> findByOriginalMember(SiteMemberEntity originalMember);

    Optional<SiteMemberAuthEntity> findByEmailAndProvider(String email, AuthProvider provider);

    Optional<SiteMemberAuthEntity> findByProviderAndProviderId(AuthProvider provider, String providerId);

    boolean existsByEmail(String email);

    boolean existsByOriginalMember(SiteMemberEntity originalMember);
}