package kr.modusplant.shared.persistence.common.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommSecondaryCategoryConstant {
    public static final Integer TEST_COMM_SECONDARY_CATEGORY_ID_1 = 1;
    public static final Integer TEST_COMM_SECONDARY_CATEGORY_ID_2 = 2;
    public static final Integer TEST_COMM_SECONDARY_CATEGORY_ID_3 = 3;
    public static final List<Integer> TEST_COMM_SECONDARY_CATEGORIES_ID =
            List.of(TEST_COMM_SECONDARY_CATEGORY_ID_1,
                    TEST_COMM_SECONDARY_CATEGORY_ID_2,
                    TEST_COMM_SECONDARY_CATEGORY_ID_3);
    public static final String TEST_COMM_SECONDARY_CATEGORY_CATEGORY = "컨텐츠 2차 항목";
    public static final Integer TEST_COMM_SECONDARY_CATEGORY_ORDER = 99;
}
