package kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity.PostEntityBuilder;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_EDITED_AT;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_VIEW_COUNT;

public interface PostEntityTestUtils extends PostTestUtils, MemberEntityTestUtils, PrimaryCategoryEntityTestUtils, SecondaryCategoryEntityTestUtils {
    default PostEntityBuilder createPublishedPostEntityBuilder() {
        return PostEntity.builder()
                .likeCount(testLikeCount.getValue())
                .viewCount(TEST_POST_VIEW_COUNT)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(true)
                .editedAt(TEST_POST_EDITED_AT);
    }

    default PostEntityBuilder createPublishedPostEntityBuilderWithUuid() {
        return PostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(testLikeCount.getValue())
                .viewCount(TEST_POST_VIEW_COUNT)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(true)
                .editedAt(TEST_POST_EDITED_AT);
    }

    default PostEntityBuilder createDraftPostEntityBuilder() {
        return PostEntity.builder()
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(false)
                .editedAt(TEST_POST_EDITED_AT);
    }

    default PostEntityBuilder createDraftPostEntityBuilderWithUuid() {
        return PostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(false)
                .editedAt(TEST_POST_EDITED_AT);
    }

    default PostEntityBuilder createDraftPostEntityBuilderWithoutContent() {
        return PostEntity.builder()
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .isPublished(false)
                .editedAt(TEST_POST_EDITED_AT);
    }

    default PostEntityBuilder createDraftPostEntityBuilderWithoutContentWithUuid() {
        return PostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .isPublished(false)
                .editedAt(TEST_POST_EDITED_AT);
    }
}
