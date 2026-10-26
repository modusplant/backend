package kr.modusplant.infrastructure.security.util;

import kr.modusplant.shared.exception.NotAccessibleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.infrastructure.security.enums.SecurityErrorCode.AUTHORIZATION_FAILED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityAssertionUtilsTest {

    @Test
    @DisplayName("일치하는 UUID로 예외 없이 활동 수행")
    void testRequireSelf_givenMatchingUuid_willProcessAction() {
        // given
        UUID uuid = UUID.randomUUID();

        // when & then
        assertThatCode(() -> SecurityAssertionUtils.requireSelf(uuid, uuid)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("불일치하는 UUID로 예외 반환")
    void testRequireSelf_givenMismatchingUuid_willThrowException() {
        // given
        UUID authenticatedUuid = UUID.randomUUID();
        UUID targetUuid = UUID.randomUUID();

        // when & then
        NotAccessibleException exception = assertThrows(NotAccessibleException.class,
                () -> SecurityAssertionUtils.requireSelf(authenticatedUuid, targetUuid));

        assertThat(exception.getErrorCode()).isEqualTo(AUTHORIZATION_FAILED);
    }
}
