package kr.modusplant.legacy.domains.member.common.util.entity;

import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;

import static kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberRoleConstant.MEMBER_ROLE_ADMIN_ROLE;
import static kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberRoleConstant.MEMBER_ROLE_USER_ROLE;

public interface SiteMemberRoleEntityTestUtils extends SiteMemberEntityTestUtils {
    default SiteMemberRoleEntity createMemberRoleAdminEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicAdminEntity()).role(MEMBER_ROLE_ADMIN_ROLE).build();
    }

    default SiteMemberRoleEntity createMemberRoleAdminEntityWithUuid() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicAdminEntityWithUuid()).role(MEMBER_ROLE_ADMIN_ROLE).build();
    }

    default SiteMemberRoleEntity createMemberRoleUserEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicUserEntity()).role(MEMBER_ROLE_USER_ROLE).build();
    }

    default SiteMemberRoleEntity createMemberRoleUserEntityWithUuid() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicUserEntityWithUuid()).role(MEMBER_ROLE_USER_ROLE).build();
    }
}
