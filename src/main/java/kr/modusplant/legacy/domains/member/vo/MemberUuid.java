package kr.modusplant.legacy.domains.member.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberUuid {
    public static final String ACTIVE_MEMBER_UUID = "activeMemberUuid";
    public static final String MEMBER_UUID = "memberUuid";
    public static final String ORIGINAL_MEMBER_UUID = "originalMemberUuid";

    public static final String SNAKE_MEMB_UUID = "memb_uuid";
    public static final String SNAKE_AUTH_MEMB_UUID = "auth_memb_uuid";
    public static final String SNAKE_CREA_MEMB_UUID = "crea_memb_uuid";
}
