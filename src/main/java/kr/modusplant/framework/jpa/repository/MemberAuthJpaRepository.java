package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.MemberAuthEntity;
import kr.modusplant.framework.jpa.entity.MemberEntity;
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
public interface MemberAuthJpaRepository extends
        LastModifiedAtRepository<MemberAuthEntity>,
        UuidPrimaryKeyRepository<MemberAuthEntity>,
        JpaRepository<MemberAuthEntity, UUID> {
    Optional<MemberAuthEntity> findByEmail(String email);

    List<MemberAuthEntity> findByProvider(AuthProvider provider);

    Optional<MemberAuthEntity> findByProviderId(String providerId);

    Optional<MemberAuthEntity> findByMember(MemberEntity member);

    Optional<MemberAuthEntity> findByEmailAndProvider(String email, AuthProvider provider);

    Optional<MemberAuthEntity> findByProviderAndProviderId(AuthProvider provider, String providerId);

    boolean existsByEmail(String email);

    boolean existsByMember(MemberEntity member);
}