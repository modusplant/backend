package kr.modusplant.support.util;

import kr.modusplant.global.enums.Role;
import kr.modusplant.global.persistence.entity.SiteMemberRoleEntity;

public interface SiteMemberRoleEntityTestUtils extends SiteMemberEntityTestUtils {
    default SiteMemberRoleEntity createMemberRoleAdminEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicAdminEntity()).role(Role.ROLE_ADMIN).build();
    }

    default SiteMemberRoleEntity createMemberRoleUserEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicUserEntity()).build();
    }
}
