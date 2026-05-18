package kr.modusplant.domains.term.common.util.domain.vo;

import kr.modusplant.domains.term.domain.vo.SiteMemberTermId;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface SiteMemberTermIdTestUtils {
    SiteMemberTermId testSiteMemberTermId = SiteMemberTermId.fromUuid(MEMBER_BASIC_USER_UUID);
}
