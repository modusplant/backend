package kr.modusplant.domains.post.common.util.usecase.response;

import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;

import java.time.LocalDateTime;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_PREVIEW;
import static kr.modusplant.domains.post.common.constant.PostStringConstant.TEST_POST_TITLE;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID2;
import static kr.modusplant.domains.post.common.util.usecase.model.PostReadModelTestUtils.likeCount;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostResponseTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);

    PostDetailResponse TEST_POST_DETAIL_RESPONSE = new PostDetailResponse(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_ID,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_UUID,
            MEMBER_BASIC_USER_NICKNAME,
            5,
            50L,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            true,
            testDate,
            testDate,
            true,
            false
    );

    PostSummaryResponse TEST_POST_SUMMARY_RESPONSE = new PostSummaryResponse(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT_PREVIEW,
            likeCount,
            testDate,
            5,
            true,
            false
    );

    PostSummaryResponse TEST_POST_SUMMARY_RESPONSE2 = new PostSummaryResponse(
            TEST_POST_ULID2,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT_PREVIEW,
            likeCount,
            testDate,
            5,
            true,
            false
    );
}
