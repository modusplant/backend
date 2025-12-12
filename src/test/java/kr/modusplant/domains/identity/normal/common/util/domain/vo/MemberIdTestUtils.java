package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.NormalMemberId;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface MemberIdTestUtils {
    NormalMemberId TEST_NORMAL_MEMBER_ID = NormalMemberId.create(MEMBER_BASIC_USER_UUID);
}
