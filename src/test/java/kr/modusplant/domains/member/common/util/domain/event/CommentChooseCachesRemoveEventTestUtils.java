package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.CommentChooseCachesRemoveEvent;

import java.util.Map;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentChooseCachesRemoveEventTestUtils {
    Map<String, String> testChosenCommentPathsByPostUlid = Map.of(TEST_POST_ULID, TEST_COMMENT_PATH);
    CommentChooseCachesRemoveEvent testCommentChooseCachesRemoveEvent =
            CommentChooseCachesRemoveEvent.create(testChosenCommentPathsByPostUlid);
}
