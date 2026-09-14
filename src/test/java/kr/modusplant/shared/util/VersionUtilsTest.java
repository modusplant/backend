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
    @DisplayName("유효한 숫자로 문자열 반환")
    void testCreateVersion_givenValidNumbers_willReturnString() {
        // given & when & then
        assertThat(createVersion(1, 0, 0)).isEqualTo("v1.0.0");
        assertThat(createVersion(1, 10, 0)).isEqualTo("v1.10.0");
    }

    @Test
    @DisplayName("음수로 예외 반환")
    void testCreateVersion_givenNegativeNumber_willThrowException() {
        // given & when
        InvalidValueException majorException = assertThrows(InvalidValueException.class, () -> createVersion(-1, 0, 0));
        InvalidValueException minorException = assertThrows(InvalidValueException.class, () -> createVersion(0, -1, 0));
        InvalidValueException patchException = assertThrows(InvalidValueException.class, () -> createVersion(0, 0, -1));

        // then
        assertThat(majorException.getErrorCode()).isEqualTo(INVALID_INPUT);
        assertThat(minorException.getErrorCode()).isEqualTo(INVALID_INPUT);
        assertThat(patchException.getErrorCode()).isEqualTo(INVALID_INPUT);
    }
}
