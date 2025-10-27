package kr.modusplant.domains.post.common.util.framework.out.jpa.entity;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostArchiveEntity;

import java.time.LocalDateTime;

public interface PostArchiveEntityTestUtils extends PostTestUtils {
    default PostArchiveEntity createPostArchieveEntity() {
        LocalDateTime time = LocalDateTime.now();
        return PostArchiveEntity.builder()
                .ulid(testPostId.getValue())
                .primaryCategoryUuid(testPrimaryCategoryId.getValue())
                .secondaryCategoryUuid(testSecondaryCategoryId.getValue())
                .authMemberUuid(testAuthorId.getValue())
                .createMemberUuid(testAuthorId.getValue())
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .createdAt(time)
                .updatedAt(time)
                .publishedAt(time)
                .build();
    }
}
