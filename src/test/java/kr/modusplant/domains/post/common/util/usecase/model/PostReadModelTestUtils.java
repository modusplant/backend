package kr.modusplant.domains.post.common.util.usecase.model;

import kr.modusplant.domains.post.usecase.record.DraftPostReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailDataReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;

import java.time.LocalDateTime;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostStringConstant.TEST_POST_TITLE;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID2;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;

public interface PostReadModelTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);
    int likeCount = 5;

    PostDetailReadModel TEST_PUBLISHED_POST_DETAIL_READ_MODEL = new PostDetailReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_ID,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_UUID,
            MEMBER_BASIC_USER_NICKNAME,
            MEMBER_PROFILE_BASIC_USER_IMAGE_PATH,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            likeCount,
            true,
            testDate,
            testDate,
            true,
            false
    );

    PostDetailDataReadModel TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL = new PostDetailDataReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_ID,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_UUID,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            true,
            testDate,
            testDate
    );

    PostDetailDataReadModel TEST_DRAFT_POST_DETAIL_DATA_READ_MODEL = new PostDetailDataReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_ID,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_UUID,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            false,
            null,
            testDate
    );

    PostSummaryReadModel TEST_POST_SUMMARY_READ_MODEL = new PostSummaryReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            likeCount,
            testDate,
            5,
            true,
            false
    );

    PostSummaryReadModel TEST_POST_SUMMARY_READ_MODEL2 = new PostSummaryReadModel(
            TEST_POST_ULID2,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            likeCount,
            testDate,
            5,
            true,
            false
    );

    DraftPostReadModel TEST_DRAFT_POST_READ_MODEL = new DraftPostReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            testDate
    );


}
