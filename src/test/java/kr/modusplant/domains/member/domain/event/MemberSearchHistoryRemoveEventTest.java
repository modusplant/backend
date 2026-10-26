package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberSearchHistoryRemoveEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("memberId가 null일 때 예외 반환")
        void testCreate_givenNullMemberId_willThrowException() {
            // given & when
            EmptyValueException emptyValueException = assertThrows(EmptyValueException.class, () ->
                    MemberSearchHistoryRemoveEvent.create(null));

            // then
            assertThat(emptyValueException.getErrorCode()).isEqualTo(GeneralErrorCode.EMPTY_VALUE);
        }
    }
}
