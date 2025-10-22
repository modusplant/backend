package kr.modusplant.domains.identity.social.framework.out.jpa.repository.supers;

import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface MemberAuthJpaRepository extends UuidPrimaryKeyRepository<MemberAuthEntity>, JpaRepository<MemberAuthEntity, UUID> {
    Optional<MemberAuthEntity> findByProviderAndProviderId(AuthProvider provider, String providerId);
}