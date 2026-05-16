package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.PostEntity;

import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.*;

public interface PostEntityTestUtils extends MemberEntityTestUtils, PrimaryCategoryEntityTestUtils, SecondaryCategoryEntityTestUtils {
    default PostEntity.PostEntityBuilder createPostEntityBuilder() {
        return PostEntity.builder()
                .likeCount(TEST_COMM_POST_LIKE_COUNT)
                .viewCount(TEST_COMM_POST_VIEW_COUNT)
                .title(TEST_COMM_POST_TITLE)
                .content(TEST_COMM_POST_CONTENT_JSON_NODE)
                .isPublished(TEST_COMM_POST_IS_PUBLISHED);
    }

    default PostEntity.PostEntityBuilder createPostEntityBuilderWithUlid() {
        return PostEntity.builder()
                .ulid(TEST_COMM_POST_ULID)
                .likeCount(TEST_COMM_POST_LIKE_COUNT)
                .viewCount(TEST_COMM_POST_VIEW_COUNT)
                .title(TEST_COMM_POST_TITLE)
                .content(TEST_COMM_POST_CONTENT_JSON_NODE)
                .isPublished(TEST_COMM_POST_IS_PUBLISHED);
    }
}
