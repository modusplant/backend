package kr.modusplant.domains.member.common.util.app.http.request;

import kr.modusplant.domains.member.app.http.request.SiteMemberRoleInsertRequest;
import kr.modusplant.domains.member.app.http.request.SiteMemberRoleUpdateRequest;
import kr.modusplant.domains.member.common.util.domain.SiteMemberRoleTestUtils;

public interface SiteMemberRoleRequestTestUtils extends SiteMemberRoleTestUtils {
    SiteMemberRoleInsertRequest memberRoleUserInsertRequest = new SiteMemberRoleInsertRequest(memberRoleUserWithUuid.getUuid(), memberRoleUser.getRole());

    SiteMemberRoleUpdateRequest memberRoleUserUpdateRequest = new SiteMemberRoleUpdateRequest(memberRoleUserWithUuid.getUuid(), memberRoleUser.getRole());
}
