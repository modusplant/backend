package kr.modusplant.modules.jwt.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
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
