package kr.modusplant.domains.post.common.util.usecase.model;

import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;

import java.time.LocalDateTime;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostStringConstant.TEST_POST_TITLE;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostReadModelTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);
    int likeCount = 5;

    PostDetailReadModel TEST_PUBLISHED_POST_DETAIL_READ_MODEL = new PostDetailReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_UUID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_UUID,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_UUID,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            likeCount,
            true,
            testDate,
            testDate,
            true,
            false
    );

    PostDetailReadModel TEST_DRAFT_POST_DETAIL_READ_MODEL = new PostDetailReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_UUID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_UUID,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_UUID,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            likeCount,
            false,
            null,
            testDate,
            false,
            false
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
}
