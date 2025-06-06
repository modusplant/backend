package kr.modusplant.modules.jwt.app.service;

import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.token-cleanup-cron}", zone = "Asia/Seoul")
    public void cleanup() {
        refreshTokenRepository.deleteByExpiredAtBefore(LocalDateTime.now());
    }
}
