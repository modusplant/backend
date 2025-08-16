package kr.modusplant.domains.member.test.utils.domain;

import kr.modusplant.domains.member.domain.vo.MemberId;

import static kr.modusplant.domains.member.test.vo.MemberUuidVO.TEST_MEMBER_UUID;

public interface MemberIdTestUtils {
    MemberId testMemberId = MemberId.fromUuid(TEST_MEMBER_UUID);
}
