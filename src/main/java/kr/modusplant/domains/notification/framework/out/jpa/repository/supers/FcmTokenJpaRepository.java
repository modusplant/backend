package kr.modusplant.domains.notification.framework.out.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.FcmTokenEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, Long> {
    Optional<FcmTokenEntity> findByToken(String token);

    List<FcmTokenEntity> findAllByMember(SiteMemberEntity memberEntity);

    void deleteByToken(String token);
}
