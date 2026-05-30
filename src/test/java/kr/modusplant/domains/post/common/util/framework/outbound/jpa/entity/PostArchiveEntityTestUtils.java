package kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostArchiveEntity;

import static kr.modusplant.domains.post.common.constant.PostConstant.*;

public interface PostArchiveEntityTestUtils extends PostTestUtils {
    default PostArchiveEntity createPostArchiveEntity() {
        return PostArchiveEntity.builder()
                .ulid(testPostId.getValue())
                .primaryCategoryId(testPrimaryCategoryId.getValue())
                .secondaryCategoryId(testSecondaryCategoryId.getValue())
                .authMemberUuid(testAuthorId.getValue())
                .title(testPostContent.getTitle())
                .contentText(TEST_POST_CONTENT_TEXT)
                .createdAt(TEST_POST_CREATED_AT)
                .archivedAt(TEST_POST_ARCHIVED_AT)
                .updatedAt(TEST_POST_UPDATED_AT)
                .publishedAt(TEST_POST_PUBLISHED_AT)
                .editedAt(TEST_POST_EDITED_AT)
                .build();
    }
}
