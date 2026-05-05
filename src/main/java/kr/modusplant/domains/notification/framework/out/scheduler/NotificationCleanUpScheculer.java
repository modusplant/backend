package kr.modusplant.domains.notification.framework.out.scheduler;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationCleanUpScheculer {
    private final NotificationJpaRepository notificationJpaRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.notification-cleanup-cron}", zone = "Asia/Seoul")
    public void cleanup() {
        notificationJpaRepository.deleteByCreatedAtBefore(LocalDateTime.now().minusDays(30));
    }
}
