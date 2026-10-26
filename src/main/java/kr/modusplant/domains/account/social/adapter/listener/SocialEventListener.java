package kr.modusplant.domains.account.social.adapter.listener;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.domain.event.SocialAccessRevokeEvent;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocialEventListener {
    private final SocialAuthClientFactory clientFactory;

    @Async("socialDomainExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSocialAccessRevoke(SocialAccessRevokeEvent event) {
        try {
            clientFactory.getClient(SocialProvider.valueOf(event.getAuthProvider().toUpperCase(Locale.ROOT)))
                    .revokeAccess(event.getSocialAccessToken());
        } catch (Exception e) {
            log.error("[Social Domain] 소셜 계정 연결 해제 실패 - memberId = {}, authProvider = {}",
                    event.getMemberId(), event.getAuthProvider(), e);
        }
    }
}
