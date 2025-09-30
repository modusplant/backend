package kr.modusplant.framework.out.jpa.entity.common.constant;

import kr.modusplant.infrastructure.security.enums.Role;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static kr.modusplant.framework.out.jpa.entity.common.constant.SiteMemberEntityConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.framework.out.jpa.entity.common.constant.SiteMemberEntityConstant.MEMBER_BASIC_USER_UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SiteMemberRoleEntityConstant {
    public static final UUID MEMBER_ROLE_ADMIN_UUID = MEMBER_BASIC_ADMIN_UUID;
    public static final Role MEMBER_ROLE_ADMIN_ROLE = Role.ADMIN;

    public static final UUID MEMBER_ROLE_USER_UUID = MEMBER_BASIC_USER_UUID;
    public static final Role MEMBER_ROLE_USER_ROLE = Role.USER;
}
