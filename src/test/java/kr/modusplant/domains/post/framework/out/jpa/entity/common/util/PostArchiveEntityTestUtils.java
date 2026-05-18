package kr.modusplant.domains.post.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostArchiveEntity;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.PrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_ID_1;

public interface PostArchiveEntityTestUtils extends MemberEntityTestUtils, PrimaryCategoryEntityTestUtils, SecondaryCategoryEntityTestUtils {
    default PostArchiveEntity createPostArchiveEntity() {
        return PostArchiveEntity.builder()
                .authMemberUuid(MEMBER_BASIC_USER_UUID)
                .primaryCategoryId(TEST_COMM_PRIMARY_CATEGORY_ID)
                .secondaryCategoryId(TEST_COMM_SECONDARY_CATEGORY_ID_1)
                .title(TEST_COMM_POST_TITLE)
                .contentText(TEST_COMM_POST_CONTENT_TEXT)
                .createdAt(TEST_COMM_POST_CREATED_AT)
                .archivedAt(TEST_COMM_POST_ARCHIVED_AT)
                .updatedAt(TEST_COMM_POST_UPDATED_AT)
                .publishedAt(TEST_COMM_POST_PUBLISHED_AT)
                .build();
    }
}
