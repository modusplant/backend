package kr.modusplant.domains.member.test.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberUuidVO {
    public static final UUID TEST_MEMBER_UUID = UUID.fromString("7a071932-b666-4c37-925e-ede1593ba9b8");
}
