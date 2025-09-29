package kr.modusplant.legacy.domains.member.common.util.app.http.request;

import kr.modusplant.framework.out.jpa.entity.constant.SiteMemberRoleEntityConstant;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberRoleInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberRoleUpdateRequest;

public interface SiteMemberRoleRequestTestUtils extends SiteMemberRoleEntityConstant {
    SiteMemberRoleInsertRequest memberRoleUserInsertRequest = new SiteMemberRoleInsertRequest(MEMBER_ROLE_USER_UUID, MEMBER_ROLE_USER_ROLE);

    SiteMemberRoleUpdateRequest memberRoleUserUpdateRequest = new SiteMemberRoleUpdateRequest(MEMBER_ROLE_USER_UUID, MEMBER_ROLE_USER_ROLE);
}
