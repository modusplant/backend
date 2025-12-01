package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.MemberId;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface MemberIdTestUtils {
    MemberId testMemberId = MemberId.create(MEMBER_BASIC_USER_UUID);
}
