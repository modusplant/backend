package kr.modusplant.legacy.domains.member.common.util.domain;

import kr.modusplant.legacy.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.security.framework.legacy.enums.Role;

public interface SiteMemberRoleTestUtils extends SiteMemberTestUtils {
    SiteMemberRole memberRoleAdmin = SiteMemberRole.builder().role(Role.ADMIN).build();

    SiteMemberRole memberRoleAdminWithUuid = SiteMemberRole.builder()
            .uuid(memberBasicAdminWithUuid.getUuid())
            .role(memberRoleAdmin.getRole())
            .build();

    SiteMemberRole memberRoleUser = SiteMemberRole.builder().role(Role.USER).build();

    SiteMemberRole memberRoleUserWithUuid = SiteMemberRole.builder()
            .uuid(memberBasicUserWithUuid.getUuid())
            .role(memberRoleUser.getRole())
            .build();
}
