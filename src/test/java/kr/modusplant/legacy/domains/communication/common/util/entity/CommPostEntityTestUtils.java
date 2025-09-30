package kr.modusplant.legacy.domains.communication.common.util.entity;

import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity.CommPostEntityBuilder;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberEntityTestUtils;

import static kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils.*;

public interface CommPostEntityTestUtils extends SiteMemberEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils {
    default CommPostEntityBuilder createCommPostEntityBuilder() {
        return CommPostEntity.builder()
                .likeCount(TEST_COMM_POST_LIKE_COUNT)
                .viewCount(TEST_COMM_POST_VIEW_COUNT)
                .title(TEST_COMM_POST_TITLE)
                .content(TEST_COMM_POST_CONTENT);
    }
}
