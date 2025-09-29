package kr.modusplant.legacy.domains.member.common.util.domain;

import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.legacy.domains.member.domain.model.SiteMemberRole;

public interface SiteMemberRoleConstant extends SiteMemberConstant {
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
