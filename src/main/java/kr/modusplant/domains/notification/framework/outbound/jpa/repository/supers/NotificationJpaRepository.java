package kr.modusplant.domains.notification.framework.outbound.jpa.repository.supers;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.domain.enums.NotificationStatusType;
import kr.modusplant.domains.notification.framework.outbound.jpa.entity.NotificationEntity;
import kr.modusplant.shared.persistence.repository.UlidPrimaryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationJpaRepository extends UlidPrimaryRepository<NotificationEntity>, JpaRepository<NotificationEntity, String> {

    @Modifying
    @Query("UPDATE NotificationEntity t SET t.status = 'READ' WHERE t.recipient.uuid = :recipientId AND t.ulid = :notificationId AND t.status = 'UNREAD'")
    int updateUnreadStatusById(@Param("notificationId") String notificationId, @Param("recipientId") UUID recipientId);

    @Modifying
    @Query("UPDATE NotificationEntity t SET t.status = 'READ' WHERE t.recipient.uuid = :recipientId AND t.status = 'UNREAD'")
    int updateUnreadStatus(@Param("recipientId") UUID recipientId);

    long countByRecipientAndStatus(MemberEntity recipient, NotificationStatusType status);

    void deleteByCreatedAtBefore(LocalDateTime cutoff);

    @Query("SELECT n.ulid FROM NotificationEntity n WHERE n.recipient.uuid = :recipientId ORDER BY n.ulid DESC")
    List<String> findUlidsByRecipientId(@Param("recipientId") UUID recipientId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.recipient.uuid = :recipientId AND n.ulid < :cutoffUlid")
    void deleteByRecipientIdAndUlidBefore(@Param("recipientId") UUID recipientId, @Param("cutoffUlid") String cutOffUlid);

    // TODO: 알림 생성 로직 개발 완료 후 삭제
    void deleteByRecipient(MemberEntity recipient);
}
