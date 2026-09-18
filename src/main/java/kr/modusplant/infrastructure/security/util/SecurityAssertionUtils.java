package kr.modusplant.infrastructure.security.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityAssertionUtils {

    public static void requireSelf(UUID authenticatedUuid, UUID targetUuid) {
        if (!Objects.equals(authenticatedUuid, targetUuid)) {
            throw new AccessDeniedException("본인의 리소스만 접근할 수 있습니다.");
        }
    }
}
