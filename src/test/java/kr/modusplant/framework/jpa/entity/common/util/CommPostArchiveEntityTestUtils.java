package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommPostArchiveEntity;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_CONTENT;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_TITLE;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface CommPostArchiveEntityTestUtils extends SiteMemberEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils {
    default CommPostArchiveEntity createCommPostArchiveEntity() {
        return CommPostArchiveEntity.builder()
                .authMemberUuid(MEMBER_BASIC_USER_UUID)
                .primaryCategoryId(TEST_COMM_PRIMARY_CATEGORY_ID)
                .secondaryCategoryId(TEST_COMM_SECONDARY_CATEGORY_ID)
                .title(TEST_COMM_POST_TITLE)
                .content(TEST_COMM_POST_CONTENT)
                .build();
    }
}
