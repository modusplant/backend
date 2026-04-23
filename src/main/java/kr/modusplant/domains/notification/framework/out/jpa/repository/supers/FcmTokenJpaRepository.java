package kr.modusplant.domains.notification.framework.out.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.FcmTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, Long> {
    Optional<FcmTokenEntity> findByToken(String token);
}
