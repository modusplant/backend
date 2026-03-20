package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostBookmarkCancelEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("memberId가 null일 때 오류 발생")
        void testCreate_givenNullMemberId_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    PostBookmarkCancelEvent.create(null, TEST_COMM_POST_ULID));

            // then
            assertThat(invalidValueException.getMessage()).contains("NOT_FOUND_MEMBER");
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 오류 발생")
        void testCreate_givenEmptyPostUlid_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    PostBookmarkCancelEvent.create(MEMBER_BASIC_USER_UUID, ""));

            // then
            assertThat(invalidValueException.getMessage()).contains("NOT_FOUND_POST");
        }
    }
}