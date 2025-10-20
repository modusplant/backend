package kr.modusplant.domains.member.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberUuidConstant {
    public static final UUID TEST_MEMBER_ID_UUID = MEMBER_BASIC_USER_UUID;
}
