package kr.modusplant.domains.member.domain.model;

import kr.modusplant.domains.member.enums.AuthProvider;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class SiteMemberAuth {
    private final UUID originalMemberUuid;

    private final UUID activeMemberUuid;

    private final String email;

    private final String pw;

    private final AuthProvider provider;

    private final String providerId;

    private final LocalDateTime lockoutUntil;

    public static class SiteMemberAuthBuilder {
        private UUID originalMemberUuid;
        private UUID activeMemberUuid;
        private String email;
        private String pw;
        private AuthProvider provider;
        private String providerId;
        private LocalDateTime lockoutUntil;

        public SiteMemberAuthBuilder memberAuth(SiteMemberAuth memberAuth) {
            this.originalMemberUuid = memberAuth.getOriginalMemberUuid();
            this.activeMemberUuid = memberAuth.getActiveMemberUuid();
            this.email = memberAuth.getEmail();
            this.pw = memberAuth.getPw();
            this.provider = memberAuth.getProvider();
            this.providerId = memberAuth.getProviderId();
            this.lockoutUntil = memberAuth.getLockoutUntil();
            return this;
        }

        public SiteMemberAuth build() {
            return new SiteMemberAuth(this.originalMemberUuid, this.activeMemberUuid, this.email, this.pw, this.provider, this.providerId, this.lockoutUntil);
        }
    }
}
