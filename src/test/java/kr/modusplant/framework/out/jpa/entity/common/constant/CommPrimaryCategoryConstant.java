package kr.modusplant.framework.out.jpa.entity.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommPrimaryCategoryConstant {
    public static final UUID TEST_COMM_PRIMARY_CATEGORY_UUID = UUID.fromString("ba6927c3-da49-4593-bb4f-0ea3e2f29c84");
    public static final String TEST_COMM_PRIMARY_CATEGORY_CATEGORY = "컨텐츠 1차 항목";
    public static final Integer TEST_COMM_PRIMARY_CATEGORY_ORDER = 1;
}
