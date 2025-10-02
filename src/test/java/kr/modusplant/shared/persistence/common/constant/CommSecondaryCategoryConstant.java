package kr.modusplant.shared.persistence.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommSecondaryCategoryConstant {
    public static final UUID TEST_COMM_SECONDARY_CATEGORY_UUID = UUID.fromString("bd7b287c-4a5b-4711-a641-0ed4e168ba56");
    public static final String TEST_COMM_SECONDARY_CATEGORY_CATEGORY = "컨텐츠 2차 항목";
    public static final Integer TEST_COMM_SECONDARY_CATEGORY_ORDER = 2;
}
