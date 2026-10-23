package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostViewCountRemoveEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("postIds가 null일 때 예외 반환")
        void testCreate_givenNullPostIds_willThrowException() {
            // given & when
            EmptyValueException emptyValueException = assertThrows(EmptyValueException.class, () ->
                    PostViewCountRemoveEvent.create(null));

            // then
            assertThat(emptyValueException.getErrorCode()).isEqualTo(GeneralErrorCode.EMPTY_VALUE);
        }

        @Test
        @DisplayName("postIds가 비어 있을 때 예외 반환")
        void testCreate_givenEmptyPostIds_willThrowException() {
            // given & when
            EmptyValueException emptyValueException = assertThrows(EmptyValueException.class, () ->
                    PostViewCountRemoveEvent.create(new String[]{}));

            // then
            assertThat(emptyValueException.getErrorCode()).isEqualTo(GeneralErrorCode.EMPTY_VALUE);
        }
    }
}