package kr.modusplant.domains.member.domain.event;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_ACTOR_ID;
import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostLikeEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 객체 생성 성공")
        void testCreate_givenValidParameters_willReturnEvent() {
            // when
            PostLikeEvent event = PostLikeEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID);

            // then
            assertNotNull(event);
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, event.getMemberId());
            assertEquals(TEST_NOTIFICATION_POST_ULID, event.getPostUlid());
        }

        @Test
        @DisplayName("memberId가 null일 때 오류 발생")
        void testCreate_givenNullMemberId_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    PostLikeEvent.create(null, TEST_NOTIFICATION_POST_ULID));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND_MEMBER);
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 오류 발생")
        void testCreate_givenEmptyPostUlid_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    PostLikeEvent.create(TEST_NOTIFICATION_ACTOR_ID, ""));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_POST);
        }
    }
}