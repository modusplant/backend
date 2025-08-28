package kr.modusplant.legacy.modules.jwt.persistence.repository;

import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.legacy.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends UuidPrimaryKeyRepository<RefreshTokenEntity>,JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByMemberAndRefreshToken(SiteMemberEntity member, String refreshToken);

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    Boolean existsByRefreshToken(String refreshToken);

    void deleteByExpiredAtBefore(LocalDateTime expiredAt);
}
