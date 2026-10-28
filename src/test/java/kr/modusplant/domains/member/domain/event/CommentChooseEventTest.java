package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentChooseEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 CommentChooseEvent 반환")
        void testCreate_givenValidParameters_willReturnCommentChooseEvent() {
            CommentChooseEvent event = CommentChooseEvent.create(MEMBER_BASIC_USER_UUID, TEST_POST_ULID, TEST_COMMENT_PATH);

            assertThat(event.getMemberId()).isEqualTo(MEMBER_BASIC_USER_UUID);
            assertThat(event.getPostUlid()).isEqualTo(TEST_POST_ULID);
            assertThat(event.getCommentPath()).isEqualTo(TEST_COMMENT_PATH);
        }

        @Test
        @DisplayName("memberId가 null일 때 예외 반환")
        void testCreate_givenNullMemberId_willThrowException() {
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> CommentChooseEvent.create(null, TEST_POST_ULID, TEST_COMMENT_PATH));

            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.EMPTY_VALUE);
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 예외 반환")
        void testCreate_givenBlankPostUlid_willThrowException() {
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> CommentChooseEvent.create(MEMBER_BASIC_USER_UUID, " ", TEST_COMMENT_PATH));

            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.EMPTY_VALUE);
        }

        @Test
        @DisplayName("commentPath가 비어 있을 때 예외 반환")
        void testCreate_givenBlankCommentPath_willThrowException() {
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> CommentChooseEvent.create(MEMBER_BASIC_USER_UUID, TEST_POST_ULID, " "));

            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.EMPTY_VALUE);
        }
    }
}
