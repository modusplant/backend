package kr.modusplant.domains.post.common.util.framework.out.jpa.entity;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.framework.jpa.entity.CommPostArchiveEntity;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.*;

public interface PostArchiveEntityTestUtils extends PostTestUtils {
    default CommPostArchiveEntity createPostArchiveEntity() {
        return CommPostArchiveEntity.builder()
                .ulid(testPostId.getValue())
                .primaryCategoryId(testPrimaryCategoryId.getValue())
                .secondaryCategoryId(testSecondaryCategoryId.getValue())
                .authMemberUuid(testAuthorId.getValue())
                .title(testPostContent.getTitle())
                .contentText(TEST_COMM_POST_CONTENT_TEXT)
                .createdAt(TEST_COMM_POST_CREATED_AT)
                .archivedAt(TEST_COMM_POST_ARCHIVED_AT)
                .updatedAt(TEST_COMM_POST_UPDATED_AT)
                .publishedAt(TEST_COMM_POST_PUBLISHED_AT)
                .build();
    }
}
