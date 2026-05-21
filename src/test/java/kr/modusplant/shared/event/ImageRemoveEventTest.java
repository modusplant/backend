package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageRemoveEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("imageFileKey가 null일 때 오류 발생")
        void testCreate_givenNullImageFileKey_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    ImageRemoveEvent.create(null));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_IMAGE_FILE_KEY);
        }

        @Test
        @DisplayName("imageFileKey가 빈 문자열일 때 오류 발생")
        void testCreate_givenEmptyImageFileKey_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    ImageRemoveEvent.create(" "));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_IMAGE_FILE_KEY);
        }
    }
}