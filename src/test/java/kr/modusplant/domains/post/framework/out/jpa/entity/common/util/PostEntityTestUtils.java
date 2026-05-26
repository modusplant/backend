package kr.modusplant.domains.post.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;

import static kr.modusplant.domains.post.common.constant.PostConstant.*;

public interface PostEntityTestUtils extends MemberEntityTestUtils, PrimaryCategoryEntityTestUtils, SecondaryCategoryEntityTestUtils {
    default PostEntity.PostEntityBuilder createPostEntityBuilder() {
        return PostEntity.builder()
                .likeCount(TEST_POST_LIKE_COUNT)
                .viewCount(TEST_POST_VIEW_COUNT)
                .title(TEST_POST_TITLE)
                .content(TEST_POST_CONTENT_JSON_NODE)
                .isPublished(TEST_POST_IS_PUBLISHED);
    }

    default PostEntity.PostEntityBuilder createPostEntityBuilderWithUlid() {
        return PostEntity.builder()
                .ulid(TEST_POST_ULID)
                .likeCount(TEST_POST_LIKE_COUNT)
                .viewCount(TEST_POST_VIEW_COUNT)
                .title(TEST_POST_TITLE)
                .content(TEST_POST_CONTENT_JSON_NODE)
                .isPublished(TEST_POST_IS_PUBLISHED);
    }
}
