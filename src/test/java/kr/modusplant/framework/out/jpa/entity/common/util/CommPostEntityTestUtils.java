package kr.modusplant.framework.out.jpa.entity.common.util;

import kr.modusplant.framework.out.jpa.entity.CommPostEntity;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.*;

public interface CommPostEntityTestUtils extends SiteMemberEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils {
    default CommPostEntity.CommPostEntityBuilder createCommPostEntityBuilder() {
        return CommPostEntity.builder()
                .likeCount(TEST_COMM_POST_LIKE_COUNT)
                .viewCount(TEST_COMM_POST_VIEW_COUNT)
                .title(TEST_COMM_POST_TITLE)
                .content(TEST_COMM_POST_CONTENT)
                .isPublished(TEST_COMM_POST_IS_PUBLISHED);
    }
}
