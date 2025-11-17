package kr.modusplant.infrastructure.jwt.framework.out.jpa.repository;

import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.infrastructure.jwt.framework.out.jpa.entity.RefreshTokenEntity;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenJpaRepository extends UuidPrimaryKeyRepository<RefreshTokenEntity>,JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByMemberAndRefreshToken(SiteMemberEntity member, String refreshToken);

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    Boolean existsByRefreshToken(String refreshToken);

    void deleteByExpiredAtBefore(LocalDateTime expiredAt);
}
