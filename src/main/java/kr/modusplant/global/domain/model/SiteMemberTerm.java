package kr.modusplant.global.domain.model;

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
}
