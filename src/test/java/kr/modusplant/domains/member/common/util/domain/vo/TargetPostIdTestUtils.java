package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.TargetPostId;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface TargetPostIdTestUtils {
    TargetPostId testTargetPostId = TargetPostId.create(TEST_POST_ULID);
}
