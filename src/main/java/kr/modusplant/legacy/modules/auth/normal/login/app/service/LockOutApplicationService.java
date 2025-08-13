package kr.modusplant.legacy.modules.auth.normal.login.app.service;

import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.modules.auth.normal.login.persistence.repository.LockOutRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LockOutApplicationService {
    private final SiteMemberAuthRepository siteMemberAuthRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final LockOutRedisRepository lockOutRedisRepository;

    private final int MAX_FAILED_ATTEMPTS = 5;
    private final Duration FAILED_ATTEMPT_DURATION = Duration.ofMinutes(10);
    private final Duration LOCKOUT_DURATION = Duration.ofMinutes(30);

    public void applyOnLoginSuccess(UUID originalMemberUuid, String email) {
        lockOutRedisRepository.removeFailedAttempt(email);

        SiteMemberAuthEntity siteMemberAuth = siteMemberAuthRepository.findByOriginalMember(
                siteMemberRepository.findByUuid(originalMemberUuid).orElseThrow()
        ).orElseThrow();
        siteMemberAuth.updateLockoutUntil(null);
        siteMemberAuthRepository.save(siteMemberAuth);
    }

    public int applyOnLoginFailure(UUID originalMemberUuid, String email) {
        int failedAttempt = lockOutRedisRepository.increaseFailedAttempt(email,FAILED_ATTEMPT_DURATION);

        if (failedAttempt >= MAX_FAILED_ATTEMPTS) {
            SiteMemberAuthEntity siteMemberAuth = siteMemberAuthRepository.findByOriginalMember(
                    siteMemberRepository.findByUuid(originalMemberUuid).orElseThrow()
            ).orElseThrow();
            siteMemberAuth.updateLockoutUntil(LocalDateTime.now().plus(LOCKOUT_DURATION));
            siteMemberAuthRepository.save(siteMemberAuth);

            lockOutRedisRepository.removeFailedAttempt(email);
        }

        return failedAttempt;
    }

    public int getCurrentFailedAttempts(String email) {
        return lockOutRedisRepository.getFailedAttempts(email);
    }
}
