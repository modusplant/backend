package kr.modusplant.domains.member.common.util.entity;

import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;

import static kr.modusplant.domains.member.common.util.domain.SiteMemberRoleTestUtils.*;

public interface SiteMemberRoleEntityTestUtils extends SiteMemberEntityTestUtils {
    default SiteMemberRoleEntity createMemberRoleAdminEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicAdminEntity()).role(memberRoleAdmin.getRole()).build();
    }

    default SiteMemberRoleEntity createMemberRoleAdminEntityWithUuid() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicAdminEntityWithUuid()).role(memberRoleAdminWithUuid.getRole()).build();
    }

    default SiteMemberRoleEntity createMemberRoleUserEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicUserEntity()).role(memberRoleUser.getRole()).build();
    }

    default SiteMemberRoleEntity createMemberRoleUserEntityWithUuid() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicUserEntityWithUuid()).role(memberRoleUserWithUuid.getRole()).build();
    }
}
