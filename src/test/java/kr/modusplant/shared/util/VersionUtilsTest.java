package kr.modusplant.shared.util;

import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.INVALID_INPUT;
import static kr.modusplant.shared.util.VersionUtils.createVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VersionUtilsTest {

    @Test
    @DisplayName("올바른 형식의 버전 반환")
    void inputVersion_givenValidInt_willReturnVersion() {
        assertThat(createVersion(1, 0, 0)).isEqualTo("v1.0.0");
        assertThat(createVersion(1, 10, 0)).isEqualTo("v1.10.0");
    }

    @Test
    @DisplayName("버전 숫자가 0보다 작을 때 예외 발생")
    void inputVersion_givenVersionLowerThanZero_willThrowIllegalArgumentException() {
        assertThat(assertThrows(InvalidValueException.class, () -> createVersion(-1, 0, 0)).getErrorCode()).isEqualTo(INVALID_INPUT);
        assertThat(assertThrows(InvalidValueException.class, () -> createVersion(0, -1, 0)).getErrorCode()).isEqualTo(INVALID_INPUT);
        assertThat(assertThrows(InvalidValueException.class, () -> createVersion(0, 0, -1)).getErrorCode()).isEqualTo(INVALID_INPUT);
    }
}
