package kr.modusplant.framework.out.jpa.entity.common.util;

import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;

import static kr.modusplant.shared.persistence.common.constant.SiteMemberRoleConstant.MEMBER_ROLE_ADMIN_ROLE;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberRoleConstant.MEMBER_ROLE_USER_ROLE;

public interface SiteMemberRoleEntityTestUtils extends SiteMemberEntityTestUtils {
    default SiteMemberRoleEntity createMemberRoleAdminEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicAdminEntity())
                .role(MEMBER_ROLE_ADMIN_ROLE).build();
    }

    default SiteMemberRoleEntity createMemberRoleAdminEntityWithUuid() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicAdminEntityWithUuid())
                .role(MEMBER_ROLE_ADMIN_ROLE).build();
    }

    default SiteMemberRoleEntity createMemberRoleUserEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicUserEntity())
                .role(MEMBER_ROLE_USER_ROLE).build();
    }

    default SiteMemberRoleEntity createMemberRoleUserEntityWithUuid() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicUserEntityWithUuid())
                .role(MEMBER_ROLE_USER_ROLE).build();
    }
}
