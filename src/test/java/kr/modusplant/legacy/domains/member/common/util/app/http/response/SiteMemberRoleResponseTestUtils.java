package kr.modusplant.legacy.domains.member.common.util.app.http.response;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberRoleConstant;

public interface SiteMemberRoleResponseTestUtils extends SiteMemberRoleConstant {
    SiteMemberRoleResponse memberRoleUserResponse = new SiteMemberRoleResponse(memberRoleUserWithUuid.getUuid(), memberRoleUser.getRole());
}
