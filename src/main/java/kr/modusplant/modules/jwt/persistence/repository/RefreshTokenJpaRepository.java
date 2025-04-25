package kr.modusplant.modules.jwt.persistence.repository;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenJpaRepository extends UuidPrimaryKeyRepository<RefreshTokenEntity>,JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByMemberAndDeviceId(SiteMemberEntity member, UUID deviceId);

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    Optional<RefreshTokenEntity> findByDeviceId(UUID deviceId);
}
