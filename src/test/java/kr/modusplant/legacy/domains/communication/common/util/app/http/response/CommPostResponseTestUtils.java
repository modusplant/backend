package kr.modusplant.legacy.domains.communication.common.util.app.http.response;

import kr.modusplant.legacy.domains.communication.app.http.response.CommPostResponse;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface CommPostResponseTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);

    CommPostResponse TEST_COMM_POST_RESPONSE = new CommPostResponse(
            TEST_COMM_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_PRIMARY_CATEGORY_UUID,
            TEST_COMM_PRIMARY_CATEGORY_ORDER,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_UUID,
            TEST_COMM_SECONDARY_CATEGORY_ORDER,
            TEST_COMM_POST_AUTH_MEMBER_UUID,
            MEMBER_BASIC_USER_NICKNAME,
            5,
            76L,
            TEST_COMM_POST_TITLE,
            TEST_COMM_POST_CONTENT,
            testDate,
            testDate.plusMinutes(24)
    );
}
