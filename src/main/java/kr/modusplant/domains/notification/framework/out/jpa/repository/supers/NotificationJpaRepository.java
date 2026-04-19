package kr.modusplant.domains.notification.framework.out.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.CommNotificationEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.enums.NotificationStatusType;
import kr.modusplant.shared.persistence.repository.UlidPrimaryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface NotificationJpaRepository extends UlidPrimaryRepository<CommNotificationEntity>, JpaRepository<CommNotificationEntity, String> {

    Optional<CommNotificationEntity> findByUlidAndRecipient(String ulid, SiteMemberEntity recipient);

    @Modifying
    @Query("UPDATE CommNotificationEntity t SET t.status = 'READ' WHERE t.recipient.uuid = :recipientId AND t.status = 'UNREAD'")
    int updateUnreadStatus(@Param("recipientId") UUID recipientId);

    long countByRecipientAndStatus(SiteMemberEntity recipient, NotificationStatusType status);

    void deleteByCreatedAtBefore(LocalDateTime cutoff);

    // TODO: 알림 생성 로직 개발 완료 후 삭제
    void deleteByRecipient(SiteMemberEntity recipient);
}
