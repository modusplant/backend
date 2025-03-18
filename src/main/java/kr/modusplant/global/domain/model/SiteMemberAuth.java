package kr.modusplant.global.domain.model;

import kr.modusplant.global.enums.AuthProvider;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class SiteMemberAuth {
    private final UUID uuid;

    private final UUID activeMemberUuid;

    private final UUID originalMemberUuid;

    private final String email;

    private final String pw;

    private final AuthProvider provider;

    private final String providerId;

    private final Integer failedAttempt;

    private final LocalDateTime lockoutRefreshAt;

    private final LocalDateTime lockoutUntil;

    public static class SiteMemberAuthBuilder {
        private UUID uuid;
        private UUID activeMemberUuid;
        private UUID originalMemberUuid;
        private String email;
        private String pw;
        private AuthProvider provider;
        private String providerId;
        private Integer failedAttempt;
        private LocalDateTime lockoutRefreshAt;
        private LocalDateTime lockoutUntil;

        public SiteMemberAuthBuilder memberAuth(SiteMemberAuth memberAuth) {
            this.uuid = memberAuth.getUuid();
            this.activeMemberUuid = memberAuth.getActiveMemberUuid();
            this.originalMemberUuid = memberAuth.getOriginalMemberUuid();
            this.email = memberAuth.getEmail();
            this.pw = memberAuth.getPw();
            this.provider = memberAuth.getProvider();
            this.providerId = memberAuth.getProviderId();
            this.failedAttempt = memberAuth.getFailedAttempt();
            this.lockoutRefreshAt = memberAuth.getLockoutRefreshAt();
            this.lockoutUntil = memberAuth.getLockoutUntil();
            return this;
        }

        public SiteMemberAuth build() {
            return new SiteMemberAuth(this.uuid, this.activeMemberUuid, this.originalMemberUuid, this.email, this.pw, this.provider, this.providerId, this.failedAttempt, this.lockoutRefreshAt, this.lockoutUntil);
        }
    }
}
