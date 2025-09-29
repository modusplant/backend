package kr.modusplant.legacy.domains.member.common.util.app.http.request;

import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberRoleInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberRoleUpdateRequest;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberRoleConstant;

public interface SiteMemberRoleRequestTestUtils extends SiteMemberRoleConstant {
    SiteMemberRoleInsertRequest memberRoleUserInsertRequest = new SiteMemberRoleInsertRequest(memberRoleUserWithUuid.getUuid(), memberRoleUser.getRole());

    SiteMemberRoleUpdateRequest memberRoleUserUpdateRequest = new SiteMemberRoleUpdateRequest(memberRoleUserWithUuid.getUuid(), memberRoleUser.getRole());
}
