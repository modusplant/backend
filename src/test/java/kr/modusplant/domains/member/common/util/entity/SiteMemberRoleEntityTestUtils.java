<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/member/common/util/entity/SiteMemberRoleEntityTestUtils.java
package kr.modusplant.domains.member.common.util.entity;

import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;

import static kr.modusplant.domains.member.common.util.domain.SiteMemberRoleTestUtils.*;
========
package kr.modusplant.api.crud.member.common.util.entity;

import kr.modusplant.api.crud.member.persistence.entity.SiteMemberRoleEntity;

import static kr.modusplant.api.crud.member.common.util.domain.SiteMemberRoleTestUtils.*;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/member/common/util/entity/SiteMemberRoleEntityTestUtils.java

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
