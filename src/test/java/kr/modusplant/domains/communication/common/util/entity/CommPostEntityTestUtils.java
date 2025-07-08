package kr.modusplant.domains.communication.common.util.entity;

import kr.modusplant.domains.communication.common.util.domain.CommPostTestUtils;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity.CommPostEntityBuilder;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;

public interface CommPostEntityTestUtils extends SiteMemberEntityTestUtils, CommSecondaryCategoryEntityTestUtils, CommPostTestUtils {
    default CommPostEntityBuilder createCommPostEntityBuilder() {
        return CommPostEntity.builder()
                .likeCount(TEST_COMM_POST.getLikeCount())
                .viewCount(TEST_COMM_POST.getViewCount())
                .title(TEST_COMM_POST.getTitle())
                .content(TEST_COMM_POST.getContent());
    }
}
