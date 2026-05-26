package kr.modusplant.infrastructure.jwt.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.infrastructure.jwt.framework.outbound.jpa.entity.RefreshTokenEntity;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenJpaRepository extends UuidPrimaryKeyRepository<RefreshTokenEntity>,JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByMemberAndRefreshToken(MemberEntity member, String refreshToken);

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    Boolean existsByRefreshToken(String refreshToken);

    void deleteByMember(MemberEntity member);

    void deleteByExpiredAtBefore(LocalDateTime expiredAt);
}
