package kr.modusplant.domains.member.domain.event;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
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
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    MemberSearchHistoryRemoveEvent.create(null));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND_MEMBER);
        }
    }
}
