package kr.modusplant.framework.out.jpa.entity.constant;

import kr.modusplant.infrastructure.security.enums.Role;

import java.util.UUID;

public interface SiteMemberRoleEntityConstant extends SiteMemberEntityConstant {
    UUID MEMBER_ROLE_ADMIN_UUID = MEMBER_BASIC_ADMIN_UUID;
    Role MEMBER_ROLE_ADMIN_ROLE = Role.ADMIN;
    UUID MEMBER_ROLE_USER_UUID = MEMBER_BASIC_USER_UUID;
    Role MEMBER_ROLE_USER_ROLE = Role.USER;
}
