package kr.modusplant.support.util.domain;

import kr.modusplant.global.domain.model.SiteMemberRole;
import kr.modusplant.global.enums.Role;

public interface SiteMemberRoleTestUtils extends SiteMemberTestUtils {
    SiteMemberRole memberRoleAdmin = SiteMemberRole.builder().role(Role.ROLE_ADMIN).build();

    SiteMemberRole memberRoleUser = SiteMemberRole.builder().role(Role.ROLE_USER).build();
}
