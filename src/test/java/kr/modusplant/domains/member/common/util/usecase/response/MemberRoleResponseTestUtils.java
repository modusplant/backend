package kr.modusplant.domains.member.common.util.usecase.response;

import kr.modusplant.domains.member.usecase.response.MemberRoleResponse;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_ROLE;

public interface MemberRoleResponseTestUtils {
    MemberRoleResponse testMemberRoleResponse = new MemberRoleResponse(MEMBER_BASIC_USER_ROLE.name());
}
