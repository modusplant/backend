package kr.modusplant.legacy.domains.communication.common.util.entity;

import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity.CommPostEntityBuilder;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils;

public interface CommPostEntityTestUtils extends SiteMemberEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils, CommPostTestUtils {
    default CommPostEntityBuilder createCommPostEntityBuilder() {
        return CommPostEntity.builder()
                .likeCount(TEST_COMM_POST.getLikeCount())
                .viewCount(TEST_COMM_POST.getViewCount())
                .title(TEST_COMM_POST.getTitle())
                .content(TEST_COMM_POST.getContent());
    }
}
