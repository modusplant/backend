package kr.modusplant.legacy.domains.member.common.util.app.http.response;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberRoleResponse;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberRoleConstant.MEMBER_ROLE_USER_ROLE;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberRoleConstant.MEMBER_ROLE_USER_UUID;

public interface SiteMemberRoleResponseTestUtils {
    SiteMemberRoleResponse memberRoleUserResponse = new SiteMemberRoleResponse(MEMBER_ROLE_USER_UUID, MEMBER_ROLE_USER_ROLE);
}
