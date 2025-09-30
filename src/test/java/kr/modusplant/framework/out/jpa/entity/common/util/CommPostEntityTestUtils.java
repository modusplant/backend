package kr.modusplant.framework.out.jpa.entity.common.util;

import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity.CommPostEntityBuilder;

import static kr.modusplant.framework.out.jpa.entity.common.constant.CommPostConstant.*;

public interface CommPostEntityTestUtils extends SiteMemberEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils {
    default CommPostEntityBuilder createCommPostEntityBuilder() {
        return CommPostEntity.builder()
                .likeCount(TEST_COMM_POST_LIKE_COUNT)
                .viewCount(TEST_COMM_POST_VIEW_COUNT)
                .title(TEST_COMM_POST_TITLE)
                .content(TEST_COMM_POST_CONTENT);
    }
}
