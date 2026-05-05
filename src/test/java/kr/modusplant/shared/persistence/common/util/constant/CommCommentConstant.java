package kr.modusplant.shared.persistence.common.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommCommentConstant {
    public static final String TEST_COMM_COMMENT_PATH = "1.6.2";
    public static final String TEST_COMM_COMMENT_CONTENT = "테스트 댓글 내용";
    public static final Integer TEST_COMM_COMMENT_LIKE_COUNT = 1;
    public static final Boolean TEST_COMM_POST_IS_DELETED_TRUE = true;
    public static final Boolean TEST_COMM_POST_IS_DELETED_FALSE = false;
    public static final LocalDateTime TEST_COMM_COMMENT_CREATED_AT = LocalDateTime.of(2026, 4, 25, 0, 0);
    public static final LocalDateTime TEST_COMM_COMMENT_UPDATED_AT = LocalDateTime.of(2026, 4, 27, 12, 0);
}
