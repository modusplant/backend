package kr.modusplant.domains.notification.framework.out.jpa.repository.supers;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.framework.out.jpa.entity.FcmTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, Long> {
    Optional<FcmTokenEntity> findByToken(String token);

    List<FcmTokenEntity> findAllByMember(MemberEntity memberEntity);

    void deleteByToken(String token);
}
