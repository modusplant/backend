package kr.modusplant.domains.post.common.util.usecase.model;

import kr.modusplant.domains.post.usecase.record.PostSummaryWithSearchInfoReadModel;

import static kr.modusplant.domains.post.common.constant.PostDoubleConstant.TEST_POST_MAX_WORD_SIMILARITY_0_8;
import static kr.modusplant.domains.post.common.constant.PostDoubleConstant.TEST_POST_MAX_WORD_SIMILARITY_1;
import static kr.modusplant.domains.post.common.constant.PostIntegerConstant.TEST_POST_IMPORTANCE_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostIntegerConstant.TEST_POST_IMPORTANCE_TITLE;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_THUMBNAIL_KEY;
import static kr.modusplant.domains.post.common.constant.PostStringConstant.TEST_POST_TITLE;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID2;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_PUBLISHED_AT;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface PostWithSearchInfoReadModelTestUtils {
    int likeCount = 5;

    PostSummaryWithSearchInfoReadModel TEST_POST_SUMMARY_WITH_SEARCH_INFO_READ_MODEL = new PostSummaryWithSearchInfoReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            TEST_POST_CONTENT_THUMBNAIL_KEY,
            likeCount,
            TEST_COMM_POST_PUBLISHED_AT,
            5,
            true,
            false,
            TEST_POST_IMPORTANCE_TITLE,
            TEST_POST_MAX_WORD_SIMILARITY_1
    );

    PostSummaryWithSearchInfoReadModel TEST_POST_SUMMARY_WITH_SEARCH_INFO_READ_MODEL_NULL = new PostSummaryWithSearchInfoReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            TEST_POST_CONTENT_THUMBNAIL_KEY,
            likeCount,
            TEST_COMM_POST_PUBLISHED_AT,
            5,
            true,
            false,
            null,
            null
    );

    PostSummaryWithSearchInfoReadModel TEST_POST_SUMMARY_WITH_SEARCH_INFO_READ_MODEL2 = new PostSummaryWithSearchInfoReadModel(
            TEST_POST_ULID2,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_POST_TITLE,
            TEST_POST_CONTENT,
            TEST_POST_CONTENT_THUMBNAIL_KEY,
            likeCount,
            TEST_COMM_POST_PUBLISHED_AT,
            5,
            true,
            false,
            TEST_POST_IMPORTANCE_CONTENT,
            TEST_POST_MAX_WORD_SIMILARITY_0_8
    );
}
