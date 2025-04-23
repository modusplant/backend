package kr.modusplant.domains.member.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class SiteMemberTerm {
    private final UUID uuid;

    private final String agreedTermsOfUseVersion;

    private final String agreedPrivacyPolicyVersion;

    private final String agreedAdInfoReceivingVersion;

    public static class SiteMemberTermBuilder {
        private UUID uuid;
        private String agreedTermsOfUseVersion;
        private String agreedPrivacyPolicyVersion;
        private String agreedAdInfoReceivingVersion;

        public SiteMemberTermBuilder memberTerm(SiteMemberTerm memberTerm) {
            this.uuid = memberTerm.getUuid();
            this.agreedTermsOfUseVersion = memberTerm.getAgreedTermsOfUseVersion();
            this.agreedPrivacyPolicyVersion = memberTerm.getAgreedPrivacyPolicyVersion();
            this.agreedAdInfoReceivingVersion = memberTerm.getAgreedAdInfoReceivingVersion();
            return this;
        }

        public SiteMemberTerm build() {
            return new SiteMemberTerm(this.uuid, this.agreedTermsOfUseVersion, this.agreedPrivacyPolicyVersion, this.agreedAdInfoReceivingVersion);
        }
    }
}
