package kr.modusplant.infrastructure.security.util;

import kr.modusplant.shared.exception.NotAccessibleException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

import static kr.modusplant.infrastructure.security.enums.SecurityErrorCode.AUTHORIZATION_FAILED;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityAssertionUtils {

    public static void requireSelf(UUID authenticatedUuid, UUID targetUuid) {
        if (!Objects.equals(authenticatedUuid, targetUuid)) {
            throw new NotAccessibleException(
                    AUTHORIZATION_FAILED, "targetUuid", String.valueOf(targetUuid));
        }
    }
}
