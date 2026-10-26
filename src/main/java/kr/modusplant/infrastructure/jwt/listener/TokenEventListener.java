package kr.modusplant.infrastructure.jwt.listener;

import kr.modusplant.infrastructure.jwt.event.AccessTokenBlacklistEvent;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenEventListener {
    private final TokenService tokenService;

    @Async("jwtExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAccessTokenBlacklist(AccessTokenBlacklistEvent event) {
        try {
            tokenService.blacklistAccessToken(event.getAccessToken());
        } catch (Exception e) {
            log.error("[JWT] 접근 토큰 블랙리스팅 실패", e);
        }
    }
}
