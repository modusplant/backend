package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecentlyViewPostRemoveEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("postIds가 null일 때 오류 발생")
        void testCreate_givenNullPostIds_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    RecentlyViewPostRemoveEvent.create(null));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_POST_ID);
        }

        @Test
        @DisplayName("postIds가 비어 있을 때 오류 발생")
        void testCreate_givenEmptyPostIds_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    RecentlyViewPostRemoveEvent.create(new String[]{}));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_POST_ID);
        }
    }
}