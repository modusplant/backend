package kr.modusplant.legacy.modules.auth.normal.login.app.service;

import kr.modusplant.framework.out.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.persistence.entity.SiteMemberEntity;
import kr.modusplant.framework.out.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.framework.out.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.modules.auth.normal.login.persistence.repository.LockOutRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class LockOutApplicationServiceTest {
    @Mock
    private SiteMemberRepository siteMemberRepository;

    @Mock
    private SiteMemberAuthRepository siteMemberAuthRepository;

    @Mock
    private LockOutRedisRepository lockOutRedisRepository;

    @InjectMocks
    private LockOutApplicationService lockOutApplicationService;

    private final UUID originalMemberUuid = UUID.randomUUID();
    private final String email = "test@example.com";
    private final Duration FAILED_ATTEMPT_DURATION = Duration.ofMinutes(10);
    private final Duration LOCKOUT_DURATION = Duration.ofMinutes(30);

    private SiteMemberEntity siteMemberEntity;
    private SiteMemberAuthEntity siteMemberAuthEntity;

    @BeforeEach
    void setUp() {
        siteMemberEntity = mock(SiteMemberEntity.class);
        siteMemberAuthEntity = mock(SiteMemberAuthEntity.class);
    }

    @Test
    @DisplayName("로그인 성공 시, Redis 삭제 및 락아웃 해제 저장하기")
    void applyOnLoginSuccessTest() {
        // given
        given(siteMemberRepository.findByUuid(originalMemberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(siteMemberAuthRepository.findByOriginalMember(siteMemberEntity)).willReturn(Optional.of(siteMemberAuthEntity));

        // when
        lockOutApplicationService.applyOnLoginSuccess(originalMemberUuid, email);

        // then
        then(lockOutRedisRepository).should().removeFailedAttempt(email);
        then(siteMemberAuthEntity).should().updateLockoutUntil(null);
        then(siteMemberAuthRepository).should().save(siteMemberAuthEntity);
    }

    @Test
    @DisplayName("로그인 실패 시, 실패횟수 증가하고 기준에 도달하지 않으면 락아웃 처리를 하지 않기")
    void applyOnLoginFailureWhenBelowMaxFailedAttemptsTest() {
        // given
        given(lockOutRedisRepository.increaseFailedAttempt(email,FAILED_ATTEMPT_DURATION)).willReturn(3);

        // when
        int result = lockOutApplicationService.applyOnLoginFailure(originalMemberUuid, email);

        // then
        assertThat(result).isEqualTo(3);
        then(lockOutRedisRepository).should().increaseFailedAttempt(email,FAILED_ATTEMPT_DURATION);
        then(siteMemberRepository).should(never()).findByUuid(originalMemberUuid);
        then(siteMemberAuthRepository).should(never()).findByOriginalMember(siteMemberEntity);
        then(lockOutRedisRepository).should(never()).removeFailedAttempt(email);
    }

    @Test
    @DisplayName("로그인 실패 시, 실패횟수 증가하고 기준에 도달하면 락아웃 처리 하기")
    void applyOnLoginFailureWhenOverMaxFailedAttemptsTest() {
        // given
        given(lockOutRedisRepository.increaseFailedAttempt(email,FAILED_ATTEMPT_DURATION)).willReturn(5);
        given(siteMemberRepository.findByUuid(originalMemberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(siteMemberAuthRepository.findByOriginalMember(siteMemberEntity)).willReturn(Optional.of(siteMemberAuthEntity));

        // when
        int result = lockOutApplicationService.applyOnLoginFailure(originalMemberUuid, email);

        // then
        assertThat(result).isEqualTo(5);
        then(lockOutRedisRepository).should().increaseFailedAttempt(email,FAILED_ATTEMPT_DURATION);
        then(siteMemberAuthEntity).should().updateLockoutUntil(any(LocalDateTime.class));
        then(siteMemberAuthRepository).should().save(siteMemberAuthEntity);
        then(lockOutRedisRepository).should().removeFailedAttempt(email);
    }

    @Test
    @DisplayName("현재 실패횟수 조회하기")
    void getCurrentFailedAttemptsTest() {
        // given
        given(lockOutRedisRepository.getFailedAttempts(email)).willReturn(3);

        // when
        int result = lockOutApplicationService.getCurrentFailedAttempts(email);

        // then
        assertThat(result).isEqualTo(3);
        then(lockOutRedisRepository).should().getFailedAttempts(email);
    }

}