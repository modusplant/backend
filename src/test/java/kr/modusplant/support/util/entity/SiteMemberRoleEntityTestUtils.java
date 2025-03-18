package kr.modusplant.support.util.entity;

import kr.modusplant.global.persistence.entity.SiteMemberRoleEntity;

import static kr.modusplant.support.util.domain.SiteMemberRoleTestUtils.memberRoleAdmin;
import static kr.modusplant.support.util.domain.SiteMemberRoleTestUtils.memberRoleUser;

public interface SiteMemberRoleEntityTestUtils extends SiteMemberEntityTestUtils {
    default SiteMemberRoleEntity createMemberRoleAdminEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicAdminEntity()).role(memberRoleAdmin.getRole()).build();
    }

    default SiteMemberRoleEntity createMemberRoleUserEntity() {
        return SiteMemberRoleEntity.builder().member(createMemberBasicUserEntity()).role(memberRoleUser.getRole()).build();
    }
}
