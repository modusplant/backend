package kr.modusplant.legacy.domains.communication.common.util.entity;

import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity.CommPostEntityBuilder;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityConstant;

public interface CommPostEntityTestUtils extends SiteMemberEntityConstant, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils, CommPostTestUtils {
    default CommPostEntityBuilder createCommPostEntityBuilder() {
        return CommPostEntity.builder()
                .likeCount(TEST_COMM_POST.getLikeCount())
                .viewCount(TEST_COMM_POST.getViewCount())
                .title(TEST_COMM_POST.getTitle())
                .content(TEST_COMM_POST.getContent());
    }
}
