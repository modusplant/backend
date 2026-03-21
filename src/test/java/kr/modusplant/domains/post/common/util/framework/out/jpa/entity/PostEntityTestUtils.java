package kr.modusplant.domains.post.common.util.framework.out.jpa.entity;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.CommPostEntity.CommPostEntityBuilder;

public interface PostEntityTestUtils extends PostTestUtils {
    default CommPostEntityBuilder createPublishedPostEntityBuilder() {
        return CommPostEntity.builder()
                .likeCount(testLikeCount.getValue())
                .viewCount(5L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(true);
    }

    default CommPostEntityBuilder createPublishedPostEntityBuilderWithUuid() {
        return CommPostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(testLikeCount.getValue())
                .viewCount(5L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(true);
    }

    default CommPostEntityBuilder createDraftPostEntityBuilder() {
        return CommPostEntity.builder()
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(false);
    }

    default CommPostEntityBuilder createDraftPostEntityBuilderWithUuid() {
        return CommPostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .thumbnailPath(testPostContent.getThumbnailPath())
                .isPublished(false);
    }

    default CommPostEntityBuilder createDraftPostEntityBuilderWithoutContent() {
        return CommPostEntity.builder()
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .isPublished(false);
    }

    default CommPostEntityBuilder createDraftPostEntityBuilderWithoutContentWithUuid() {
        return CommPostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(0)
                .viewCount(0L)
                .title(testPostContent.getTitle())
                .isPublished(false);
    }
}
