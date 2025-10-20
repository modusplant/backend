package kr.modusplant.infrastructure.jwt.scheduler;

import kr.modusplant.infrastructure.jwt.framework.out.jpa.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {
    private final RefreshTokenJpaRepository refreshTokenRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.token-cleanup-cron}", zone = "Asia/Seoul")
    public void cleanup() {
        refreshTokenRepository.deleteByExpiredAtBefore(LocalDateTime.now());
    }
}
