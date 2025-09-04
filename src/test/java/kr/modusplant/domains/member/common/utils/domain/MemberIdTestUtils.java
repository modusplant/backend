package kr.modusplant.domains.member.common.utils.domain;

import kr.modusplant.domains.member.domain.vo.MemberId;

import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_UUID;

public interface MemberIdTestUtils {
    MemberId testMemberId = MemberId.fromUuid(TEST_MEMBER_UUID);
}
