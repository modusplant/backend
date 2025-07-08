package kr.modusplant.domains.communication.common.util.entity;

import kr.modusplant.domains.communication.persistence.entity.CommLikeEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;

public interface CommLikeEntityTestUtils extends CommPostEntityTestUtils, SiteMemberEntityTestUtils {
    default CommLikeEntity createCommLikeEntity() {
        return CommLikeEntity.of(TEST_COMM_POST.getUlid(), memberBasicUserWithUuid.getUuid());
    }
}
