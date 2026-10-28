package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentChooseCachesRemoveEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 CommentChooseCachesRemoveEvent 반환")
        void testCreate_givenValidParameter_willReturnCommentChooseCachesRemoveEvent() {
            Map<String, String> map = Map.of(TEST_POST_ULID, TEST_COMMENT_PATH);

            CommentChooseCachesRemoveEvent event = CommentChooseCachesRemoveEvent.create(map);

            assertThat(event.getChosenCommentPathsByPostUlid()).isEqualTo(map);
        }

        @Test
        @DisplayName("비어 있는 맵으로 예외 반환")
        void testCreate_givenEmptyMap_willThrowException() {
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> CommentChooseCachesRemoveEvent.create(Map.of()));

            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.EMPTY_VALUE);
        }

        @Test
        @DisplayName("null로 예외 반환")
        void testCreate_givenNull_willThrowException() {
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> CommentChooseCachesRemoveEvent.create(null));

            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.EMPTY_VALUE);
        }
    }
}
