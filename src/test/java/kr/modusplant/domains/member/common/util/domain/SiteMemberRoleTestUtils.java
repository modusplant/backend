<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/member/common/util/domain/SiteMemberRoleTestUtils.java
package kr.modusplant.domains.member.common.util.domain;

import kr.modusplant.domains.member.domain.model.SiteMemberRole;
========
package kr.modusplant.api.crud.member.common.util.domain;

import kr.modusplant.api.crud.member.domain.model.SiteMemberRole;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/member/common/util/domain/SiteMemberRoleTestUtils.java
import kr.modusplant.global.enums.Role;

public interface SiteMemberRoleTestUtils extends SiteMemberTestUtils {
    SiteMemberRole memberRoleAdmin = SiteMemberRole.builder().role(Role.ROLE_ADMIN).build();

    SiteMemberRole memberRoleAdminWithUuid = SiteMemberRole.builder()
            .uuid(memberBasicAdminWithUuid.getUuid())
            .role(memberRoleAdmin.getRole())
            .build();

    SiteMemberRole memberRoleUser = SiteMemberRole.builder().role(Role.ROLE_USER).build();

    SiteMemberRole memberRoleUserWithUuid = SiteMemberRole.builder()
            .uuid(memberBasicUserWithUuid.getUuid())
            .role(memberRoleUser.getRole())
            .build();
}
