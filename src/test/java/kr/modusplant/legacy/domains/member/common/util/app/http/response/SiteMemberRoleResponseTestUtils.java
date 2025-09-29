package kr.modusplant.legacy.domains.member.common.util.app.http.response;

import kr.modusplant.framework.out.jpa.entity.constant.SiteMemberRoleEntityConstant;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberRoleResponse;

public interface SiteMemberRoleResponseTestUtils extends SiteMemberRoleEntityConstant {
    SiteMemberRoleResponse memberRoleUserResponse = new SiteMemberRoleResponse(MEMBER_ROLE_USER_UUID, MEMBER_ROLE_USER_ROLE);
}
