package kr.modusplant.domains.post.common.util.framework.out.jpa.entity;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.framework.jpa.entity.CommPostArchiveEntity;

import java.time.LocalDateTime;

public interface PostArchiveEntityTestUtils extends PostTestUtils {
    default CommPostArchiveEntity createPostArchieveEntity() {
        LocalDateTime time = LocalDateTime.now();
        return CommPostArchiveEntity.builder()
                .ulid(testPostId.getValue())
                .primaryCategoryId(testPrimaryCategoryId.getValue())
                .secondaryCategoryId(testSecondaryCategoryId.getValue())
                .authMemberUuid(testAuthorId.getValue())
                .title(testPostContent.getTitle())
                .content(testPostContent.getContent())
                .createdAt(time)
                .updatedAt(time)
                .publishedAt(time)
                .build();
    }
}
