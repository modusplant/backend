package kr.modusplant.domains.post.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostIntegerConstant {
    public static final Integer TEST_POST_IMPORTANCE_TITLE = 4;
    public static final Integer TEST_POST_IMPORTANCE_CONTENT = 3;
    public static final Integer TEST_POST_IMPORTANCE_COMMENT_CONTENT = 2;
    public static final Integer TEST_POST_IMPORTANCE_OTHERS = 1;
}
