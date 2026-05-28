package kr.modusplant.domains.post.common.util.usecase.response;

import kr.modusplant.domains.post.usecase.response.PostDetailDataResponse;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;

import java.time.LocalDateTime;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_URL;
import static kr.modusplant.domains.post.common.constant.PostConstant.*;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.*;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
import static kr.modusplant.domains.post.common.constant.PrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.domains.post.common.constant.SecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
import static kr.modusplant.domains.post.common.constant.SecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_ID_1;
import static kr.modusplant.domains.post.common.util.usecase.model.PostReadModelTestUtils.likeCount;

public interface PostResponseTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);

    PostDetailResponse TEST_POST_DETAIL_RESPONSE = new PostDetailResponse(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_ID_1,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_UUID,
            MEMBER_BASIC_USER_NICKNAME,
            MEMBER_PROFILE_BASIC_USER_IMAGE_URL,
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

    PostDetailDataResponse TEST_POST_DETAIL_DATA_RESPONSE = new PostDetailDataResponse(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_ID_1,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_UUID,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            TEST_POST_CONTENT_THUMBNAIL_FILENAME,
            true,
            testDate,
            testDate
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
