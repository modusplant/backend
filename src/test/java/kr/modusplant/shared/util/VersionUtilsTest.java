package kr.modusplant.shared.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.util.VersionUtils.createVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VersionUtilsTest {

    @Test
    @DisplayName("올바른 형식의 버전 반환")
    void successfulVersionCreation() {
        assertThat(createVersion(1, 0, 0)).isEqualTo("v1.0.0");
        assertThat(createVersion(1, 10, 0)).isEqualTo("v1.10.0");
    }

    @Test
    @DisplayName("버전 숫자가 0보다 작을 때 예외 발생")
    void failedVersionCreationForVersionLowerThanZero() {
        assertThat(assertThrows(IllegalArgumentException.class, () -> createVersion(-1, 0, 0)).getMessage()).isEqualTo("유효하지 않은 시맨틱 확인됨");
        assertThat(assertThrows(IllegalArgumentException.class, () -> createVersion(0, -1, 0)).getMessage()).isEqualTo("유효하지 않은 시맨틱 확인됨");
        assertThat(assertThrows(IllegalArgumentException.class, () -> createVersion(0, 0, -1)).getMessage()).isEqualTo("유효하지 않은 시맨틱 확인됨");
    }
}
