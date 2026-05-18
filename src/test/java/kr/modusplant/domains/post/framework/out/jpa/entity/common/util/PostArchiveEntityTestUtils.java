package kr.modusplant.domains.post.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostArchiveEntity;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.*;
import static kr.modusplant.domains.post.common.constant.PrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.domains.post.common.constant.SecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_ID_1;

public interface PostArchiveEntityTestUtils extends MemberEntityTestUtils, PrimaryCategoryEntityTestUtils, SecondaryCategoryEntityTestUtils {
    default PostArchiveEntity createPostArchiveEntity() {
        return PostArchiveEntity.builder()
                .authMemberUuid(MEMBER_BASIC_USER_UUID)
                .primaryCategoryId(TEST_COMM_PRIMARY_CATEGORY_ID)
                .secondaryCategoryId(TEST_COMM_SECONDARY_CATEGORY_ID_1)
                .title(TEST_POST_TITLE)
                .contentText(TEST_POST_CONTENT_TEXT)
                .createdAt(TEST_POST_CREATED_AT)
                .archivedAt(TEST_POST_ARCHIVED_AT)
                .updatedAt(TEST_POST_UPDATED_AT)
                .publishedAt(TEST_POST_PUBLISHED_AT)
                .build();
    }
}
