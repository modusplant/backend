package kr.modusplant.domains.member.common.util.app.http.response;

import kr.modusplant.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.domains.member.common.util.domain.SiteMemberRoleTestUtils;

public interface SiteMemberRoleResponseTestUtils extends SiteMemberRoleTestUtils {
    SiteMemberRoleResponse memberRoleUserResponse = new SiteMemberRoleResponse(memberRoleUserWithUuid.getUuid(), memberRoleUser.getRole());
}
