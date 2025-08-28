package kr.modusplant.legacy.domains.communication.common.util.entity;

import kr.modusplant.framework.out.persistence.entity.CommLikeEntity;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityTestUtils;

public interface CommLikeEntityTestUtils extends CommPostEntityTestUtils, SiteMemberEntityTestUtils {
    default CommLikeEntity createCommLikeEntity() {
        return CommLikeEntity.of(TEST_COMM_POST.getUlid(), memberBasicUserWithUuid.getUuid());
    }
}
