package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageRemoveEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("imageFileKeys가 null일 때 오류 발생")
        void testCreate_givenNullImageFileKeys_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    ImageRemoveEvent.create(null));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_IMAGE_FILE_KEYS);
        }

        @Test
        @DisplayName("imageFileKeys가 비어 있을 때 오류 발생")
        void testCreate_givenEmptyImageFileKeys_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    ImageRemoveEvent.create(List.of()));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_IMAGE_FILE_KEYS);
        }
    }
}