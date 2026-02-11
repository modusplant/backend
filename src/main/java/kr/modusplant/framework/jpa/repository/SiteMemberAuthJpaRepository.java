package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.persistence.repository.LastModifiedAtRepository;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface SiteMemberAuthJpaRepository extends LastModifiedAtRepository<SiteMemberAuthEntity>, UuidPrimaryKeyRepository<SiteMemberAuthEntity>, JpaRepository<SiteMemberAuthEntity, UUID> {
    List<SiteMemberAuthEntity> findByEmail(String email);

    List<SiteMemberAuthEntity> findByProvider(AuthProvider provider);

    List<SiteMemberAuthEntity> findByProviderId(String providerId);

    Optional<SiteMemberAuthEntity> findByOriginalMember(SiteMemberEntity originalMember);

    Optional<SiteMemberAuthEntity> findByEmailAndProvider(String email, AuthProvider provider);

    Optional<SiteMemberAuthEntity> findByProviderAndProviderId(AuthProvider provider, String providerId);

    boolean existsByEmail(String email);

    boolean existsByOriginalMember(SiteMemberEntity originalMember);
}