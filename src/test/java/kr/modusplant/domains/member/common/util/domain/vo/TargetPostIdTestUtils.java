package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.TargetPostId;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;

public interface TargetPostIdTestUtils {
    TargetPostId testTargetPostId = TargetPostId.create(TEST_COMM_POST_ULID);
}
