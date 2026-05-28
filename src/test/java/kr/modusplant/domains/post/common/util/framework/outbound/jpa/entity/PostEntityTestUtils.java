package kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity.PostEntityBuilder;

public interface PostEntityTestUtils extends PostTestUtils {
    default PostEntityBuilder createPublishedPostEntityBuilder() {
        return PostEntity.builder()
                .likeCount(testLikeCount.getValue())
                .viewCount(5L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(true);
    }

    default PostEntityBuilder createPublishedPostEntityBuilderWithUuid() {
        return PostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(testLikeCount.getValue())
                .viewCount(5L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(true);
    }

    default PostEntityBuilder createDraftPostEntityBuilder() {
        return PostEntity.builder()
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(false);
    }

    default PostEntityBuilder createDraftPostEntityBuilderWithUuid() {
        return PostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(false);
    }

    default PostEntityBuilder createDraftPostEntityBuilderWithoutContent() {
        return PostEntity.builder()
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .isPublished(false);
    }

    default PostEntityBuilder createDraftPostEntityBuilderWithoutContentWithUuid() {
        return PostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .isPublished(false);
    }
}
