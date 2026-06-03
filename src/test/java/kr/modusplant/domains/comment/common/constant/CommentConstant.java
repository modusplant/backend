package kr.modusplant.domains.comment.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentConstant {
    public static final String TEST_COMMENT_PATH = "1.6.2";
    public static final String TEST_COMMENT_CONTENT = "테스트 댓글 내용";
    public static final Integer TEST_COMMENT_LIKE_COUNT = 1;
    public static final Boolean TEST_POST_IS_DELETED_TRUE = true;
    public static final Boolean TEST_POST_IS_DELETED_FALSE = false;
    public static final LocalDateTime TEST_COMMENT_CREATED_AT = LocalDateTime.of(2026, 4, 25, 0, 0);
    public static final LocalDateTime TEST_COMMENT_UPDATED_AT = LocalDateTime.of(2026, 4, 27, 12, 0);
    public static final LocalDateTime TEST_COMMENT_EDITED_AT = LocalDateTime.of(2026, 4, 28, 5, 0);
}
