package kr.modusplant.domains.communication.common.util.app.http.response;

import kr.modusplant.domains.communication.app.http.response.CommPostResponse;
import kr.modusplant.domains.communication.common.util.domain.CommPostTestUtils;

import java.time.LocalDateTime;

public interface CommPostResponseTestUtils extends CommPostTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);

    CommPostResponse TEST_COMM_POST_RESPONSE = new CommPostResponse(
            TEST_COMM_POST_WITH_ULID.getUlid(),
            TEST_COMM_PRIMARY_CATEGORY_WITH_UUID.getCategory(),
            TEST_COMM_PRIMARY_CATEGORY_WITH_UUID.getUuid(),
            TEST_COMM_PRIMARY_CATEGORY_WITH_UUID.getOrder(),
            TEST_COMM_SECONDARY_CATEGORY_WITH_UUID.getCategory(),
            TEST_COMM_SECONDARY_CATEGORY_WITH_UUID.getUuid(),
            TEST_COMM_SECONDARY_CATEGORY_WITH_UUID.getOrder(),
            TEST_COMM_POST_WITH_ULID.getAuthMemberUuid(),
            memberBasicUserWithUuid.getNickname(),
            5,
            76L,
            TEST_COMM_POST_WITH_ULID.getTitle(),
            TEST_COMM_POST_WITH_ULID.getContent(),
            testDate,
            testDate.plusMinutes(24)
    );
}
