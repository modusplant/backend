package kr.modusplant.domains.post.common.util.framework.out.jpa.entity;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity.*;

public interface PostEntityTestUtils extends PostTestUtils {
    default PostEntityBuilder createPublishedPostEntityBuilder() {
        return PostEntity.builder()
                .likeCount(testLikeCount.getValue())
                .viewCount(5L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .isPublished(true);
    }

    default PostEntityBuilder createPublishedPostEntityBuilderWithUuid() {
        return PostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(testLikeCount.getValue())
                .viewCount(5L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .isPublished(true);
    }

    default PostEntityBuilder createDraftPostEntityBuilder() {
        return PostEntity.builder()
                .likeCount(testLikeCount.getValue())
                .viewCount(5L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .isPublished(false);
    }

    default PostEntityBuilder createDraftPostEntityBuilderWithUuid() {
        return PostEntity.builder()
                .ulid(testPostId.getValue())
                .likeCount(testLikeCount.getValue())
                .viewCount(5L)
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .isPublished(false);
    }
}
