package kr.modusplant.infrastructure.security.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThatThrownBy(() -> SecurityAssertionUtils.requireSelf(authenticatedUuid, targetUuid))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("본인의 리소스만 접근할 수 있습니다.");
    }
}
