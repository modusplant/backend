package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.TargetPostId;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_POST_ID_STRING;

public interface TargetPostIdTestUtils {
    TargetPostId testTargetPostId = TargetPostId.create(TEST_TARGET_POST_ID_STRING);
}
