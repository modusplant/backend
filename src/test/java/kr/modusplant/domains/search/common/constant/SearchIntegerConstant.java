package kr.modusplant.domains.search.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchIntegerConstant {
    public static final Integer TEST_SEARCH_POST_COMMENT_COUNT = 8;
    public static final Integer TEST_SEARCH_POST_IMPORTANCE_TITLE = 4;
    public static final Integer TEST_SEARCH_POST_IMPORTANCE_CONTENT = 3;
    public static final Integer TEST_SEARCH_POST_IMPORTANCE_COMMENT_CONTENT = 2;
    public static final Integer TEST_SEARCH_POST_IMPORTANCE_OTHERS = 1;
    public static final Integer TEST_SEARCH_POST_SIZE = 1;
}
