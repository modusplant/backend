package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberId;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface MemberIdTestUtils {
    MemberId testMemberId = MemberId.fromUuid(MEMBER_BASIC_USER_UUID);
}
