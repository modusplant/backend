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
}
